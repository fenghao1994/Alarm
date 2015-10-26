package com.itca.alarm;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.LogRecord;

import cn.jpush.android.api.JPushInterface;


public class AlarmActivity extends Activity implements View.OnClickListener{

    public RelativeLayout sing, internet, chart,weibo,sleep,movie,game;
    public TextView sleepText;
    public static final float RADIUS = 450f;
    ArrayList<RelativeLayout> arrayList;
    TranslateAnimation[] list;
    float x,y;
    Handler handler = new Handler();
    int[][] location = new int[6][2];
    Runnable[] runnables = new Runnable[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        arrayList = new ArrayList<>();
        sing = (RelativeLayout) findViewById(R.id.sing);
        internet = (RelativeLayout) findViewById(R.id.internet);
        chart = (RelativeLayout) findViewById(R.id.chart);
        weibo = (RelativeLayout) findViewById(R.id.weibo);
        sleep = (RelativeLayout) findViewById(R.id.sleep);
        movie = (RelativeLayout) findViewById(R.id.movie);
        game = (RelativeLayout) findViewById(R.id.game);
        sleepText = (TextView) findViewById(R.id.sleep_text);
        if (JPushInterface.isPushStopped(getApplicationContext())){
            sleepText.setText("打开推送");
        }else{
            sleepText.setText("停止推送");
        }
        arrayList.add(sing);
        arrayList.add(internet);
        arrayList.add(chart);
        arrayList.add(weibo);
        arrayList.add(sleep);
        arrayList.add(movie);
        for (int i = 0 ; i < arrayList.size() ; i++){
            arrayList.get(i).setOnClickListener(this);
        }
        game.setOnClickListener(this);
        list = new TranslateAnimation[arrayList.size()];
        gameRotate();
    }

    public void gameRotate(){
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if ( operatingAnim != null){
            game.startAnimation(operatingAnim);
            game.post(new Runnable() {
                @Override
                public void run() {
                    x = game.getTranslationX();
                    y = game.getTranslationY();
                    other();
                }
            });
        }
    }
    public void other(){

        for(int i = 0; i < arrayList.size() ; i++){
            arrayList.get(i).getLocationInWindow(location[i]);
            ObjectAnimator.ofFloat(arrayList.get(i), "translationX", x, (float)(x + Math.sin(Math.toRadians(i * 60)) * RADIUS)).setDuration(500).start();
            ObjectAnimator.ofFloat(arrayList.get(i), "translationY", y, (float)(y - Math.cos(Math.toRadians(i * 60)) * RADIUS)).setDuration(500).start();
            location[i][0] = location[i][0] + (int)(Math.sin(Math.toRadians(i * 60)) * RADIUS);
            location[i][1] = location[i][1] - (int)(Math.cos(Math.toRadians(i * 60)) * RADIUS);
        }
        active();
    }
    public void active(){

        for (int i = 0 ; i < runnables.length ; i ++){
            final int finalI = i;
            runnables[i] = new Runnable() {
                @Override
                public void run() {
                    float flag ;
                    float flag1;
                    int[] loc = new int[2];
                    arrayList.get(finalI).getLocationInWindow(loc);
                    for(int j = 0; ; j++){
                        flag = -30f + (float) (Math.random() * 60);
                        flag1 = -30f + (float) (Math.random() * 60);
                        if ( ((location[finalI][0] + 30) > (loc[0] + flag )&& (location[finalI][0] - 30 ) < (loc[0] + flag))
                                && (( location[finalI][1] + 30) > (loc[1] + flag1) && (location[finalI][1] - 30) < (loc[1] + flag1))){
                            break;
                        }
                    }
                    ObjectAnimator.ofFloat(arrayList.get(finalI), "translationX", arrayList.get(finalI).getTranslationX(),arrayList.get(finalI).getTranslationX() + flag ).setDuration(1000).start();
                    ObjectAnimator.ofFloat(arrayList.get(finalI), "translationY", arrayList.get(finalI).getTranslationY() ,arrayList.get(finalI).getTranslationY() + flag1 ).setDuration(1000).start();
                    handler.postDelayed(this, 100 * (finalI + 6));
                }
            };
        }
        for (int i = 0; i < runnables.length; i++){
            handler.postDelayed(runnables[i], 1000);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.game:goGame();break;
            case R.id.sing:goSing();break;
            case R.id.internet:goInternet();break;
            case R.id.chart:goChart();break;
            case R.id.weibo:goWeibo();break;
            case R.id.sleep:goSleep();break;
            case R.id.movie:goMovie();break;
        }
    }
    public void goGame(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        MobclickAgent.onEvent(this, "game");
    }


    public void goSing(){
        Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
        HashMap<String,String> map = new HashMap<String,String>();

        if (intent == null){
            Toast.makeText(this, "未找到音乐播放器", Toast.LENGTH_LONG).show();
            map.put("install", "0");
            MobclickAgent.onEvent(this, "music", map);
        }else{
            map.put("install", "1");
            MobclickAgent.onEvent(this, "music", map);
            startActivity(intent);
        }
    }
    public void goInternet(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("http://www.baidu.com");
        intent.setData(content_url);
        startActivity(intent);
        MobclickAgent.onEvent(this, "internet");
    }
    public void goChart(){
        HashMap<String,String> map = new HashMap<String,String>();
        Intent intent = new Intent();
        PackageManager packageManager = getPackageManager();
        intent = packageManager.getLaunchIntentForPackage("com.tencent.mobileqq");
        if (intent == null){
            map.put("install", "0");
            MobclickAgent.onEvent(this, "qq", map);
            Toast.makeText(this,"APP未安装", Toast.LENGTH_LONG).show();
        }else{
            map.put("install", "1");
            MobclickAgent.onEvent(this, "qq", map);
            /*String url="mqqwpa://im/chat?chat_type=wpa&uin=593626601";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));*/
            startActivity(intent);
        }
    }
    public void goWeibo(){
        HashMap<String,String> map = new HashMap<String,String>();
        Intent intent = new Intent();
        PackageManager packageManager = getPackageManager();
        intent = packageManager.getLaunchIntentForPackage("com.sina.weibo");
        if (intent == null){
            map.put("install", "0");
            MobclickAgent.onEvent(this, "weibo", map);
            Toast.makeText(this,"APP未安装", Toast.LENGTH_LONG).show();
        }else{
            map.put("install", "1");
            MobclickAgent.onEvent(this, "weibo", map);
            startActivity(intent);
        }
    }
    /**
     * 停止推送
     */
    public void goSleep(){
        if (JPushInterface.isPushStopped(getApplicationContext())){
            JPushInterface.resumePush(getApplicationContext());
            sleepText.setText("停止推送");
            Toast.makeText(this, "以打开推送", Toast.LENGTH_LONG).show();
            MobclickAgent.onEvent(this, "open_push");
        }else{
            JPushInterface.stopPush(getApplicationContext());
            sleepText.setText("打开推送");
            Toast.makeText(this, "以停止推送", Toast.LENGTH_LONG).show();
            MobclickAgent.onEvent(this, "stop_push");
        }
    }

    /**
     * 打开微信
     */
    public void goMovie(){
        HashMap<String,String> map = new HashMap<String,String>();
        Intent intent = new Intent();
        PackageManager packageManager = getPackageManager();
        intent = packageManager.getLaunchIntentForPackage("com.tencent.mm");
        if (intent == null){
            map.put("install", "0");
            MobclickAgent.onEvent(this, "weixin", map);
            Toast.makeText(this,"APP未安装", Toast.LENGTH_LONG).show();
            MobclickAgent.onEvent(this, "game");
        }else{
            map.put("install", "1");
            MobclickAgent.onEvent(this, "weixin", map);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
