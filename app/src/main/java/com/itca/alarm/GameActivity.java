package com.itca.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zhy.view.HorizontalProgressBarWithNumber;

import java.util.HashMap;


/**
 * Created by fenghao on 2015/10/22.
 */
public class GameActivity extends Activity implements View.OnClickListener{

    public TextView bestGrade, text1, text2, text3, text4, grade, hard;
    public ImageView right, error;
    public LinearLayout layout;
    public RelativeLayout reStart;
    public Handler handler = new Handler();
    public DrawView view;
    public int num1, num2, num3, num4, operator;
    public SharedPreferences mySharedPreferences;
    public int changeSpeed;
    public float reduceLength = 10f;
    //判断是否正确
    public boolean isRight;
    //记录正确的个数
    public int count;
    //最高记录
    public int bestCount;
    float x,y;

    public interface Speed {
        void changeSpeed();
    }
    public Speed speed;
    public void setSpeed(Speed speed){
        this.speed = speed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mySharedPreferences = getSharedPreferences("like",
                Activity.MODE_PRIVATE);
        bestCount = mySharedPreferences.getInt("bestGrade", 0);
        view =new DrawView(this);
        hard = (TextView) findViewById(R.id.hard);
        bestGrade = (TextView) findViewById(R.id.best_grade);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);
        text4 = (TextView) findViewById(R.id.text5);
        grade = (TextView) findViewById(R.id.grade);
        bestGrade = (TextView) findViewById(R.id.best_grade);
        right = (ImageView) findViewById(R.id.right);
        error = (ImageView) findViewById(R.id.error);
        layout = (LinearLayout) findViewById(R.id.layout);
        reStart = (RelativeLayout) findViewById(R.id.restart);
        bestGrade.setText("" + bestCount);
        right.setOnClickListener(this);
        error.setOnClickListener(this);
        reStart.setOnClickListener(this);
        reStart.setClickable(false);

        hard.setText(((count / 5) + 1) + "");

        layout.post(new Runnable() {
            @Override
            public void run() {
                x = layout.getWidth();
                y = layout.getHeight();
                draw();
            }
        });
        random1();
    }

    public void draw(){

        layout.addView(view);
        //通知view组件重绘
        view.invalidate();
        handler.post(runnable);
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (x > 0){
                GameActivity.this.setSpeed(new Speed() {
                    @Override
                    public void changeSpeed() {
                        reduceLength = reduceLength + 5f;
                    }
                });
                Log.i("len", reduceLength + "");
                x -= reduceLength;
                handler.postDelayed(this, 100);
                view.invalidate();
            }else{
                right.setClickable(false);
                error.setClickable(false);
                reStart.setClickable(true);
                reStart.setBackground(getResources().getDrawable(R.drawable.rect));
                Toast.makeText(GameActivity.this, "游戏结束！！！", Toast.LENGTH_LONG).show();
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("grade", count + "");
                MobclickAgent.onEvent(GameActivity.this, "grade", map);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.right:clickRight();break;
            case R.id.error:clickError();break;
            case R.id.restart:reStart();break;
        }
    }

    private void reStart() {
        MobclickAgent.onEvent(this, "restart");
        count = 0;
        changeSpeed = 0;
        reduceLength = 10f;
        x = layout.getWidth();
        handler.post(runnable);
        reStart.setClickable(false);
        reStart.setBackground(getResources().getDrawable(R.drawable.stoprect));
        grade.setText("当前分数：" + count);
        right.setClickable(true);
        error.setClickable(true);
        random1();
    }

    /**
     * 产生表达式
     * 1 +， 2 -， 3 *
     */
    public void random1(){
        operator = 1 + (int)(Math.random() * 3);
        num1 = -50 + (int)(Math.random() * 100);
        num2 = -50 + (int)(Math.random() * 100);
        do {
            num4 = -10 + (int) (Math.random() * 20);
        }while (num4 == 0);
        text1.setText(num1 + "");
        text3.setText(num2 + "");
        if (operator == 1){
            text2.setText("+");
            num3 = num1 + num2;
        }else if(operator == 2){
            text2.setText("-");
            num3 = num1 - num2;
        }else if(operator == 3){
            text2.setText("x");
            num3 = num1 * num2;
        }
        int flag = (int) (Math.random() * 10);
        if (flag > 5){
            text4.setText((num4 + num3) + "");
            isRight = false;
        }else{
            text4.setText(num3 + "");
            isRight = true;
        }
    }


    public void clickRight(){
        if (isRight){
            rightAnswer();
        }else{
            errorAnswer();
        }
    }
    public void clickError(){
        if (isRight){
            errorAnswer();
        }else{
            rightAnswer();
        }
    }


    public void rightAnswer(){
        random1();
        count++;
        if (count > bestCount){
            bestGrade.setText(count + "");
        }
        x = layout.getWidth();
        if ( (count / 5) > changeSpeed){
            changeSpeed = count / 5;
            hard.setText(((count / 5) + 1) + "");
            if (speed != null){
                speed.changeSpeed();
                Toast.makeText(GameActivity.this, "难度升级了！！！", Toast.LENGTH_LONG).show();
            }
        }
        grade.setText("当前分数：" + count);
    }
    public void errorAnswer(){
//        if (count < 5){
//            Toast.makeText(this, "赵澜莘,你这智商貌似有点着急的很呀", Toast.LENGTH_LONG).show();
//        }else if (count < 10){
//            Toast.makeText(this, "赵澜莘,以后不要出去说你的数学是我教的", Toast.LENGTH_LONG).show();
//        }else if (count < 15){
//            Toast.makeText(this, "赵澜莘,难度才3呀，好需要好好努力", Toast.LENGTH_LONG).show();
//        }else if (count < 20){
//            Toast.makeText(this, "赵澜莘,难度到4了，不错，有超过我的潜质", Toast.LENGTH_LONG).show();
//        }else if (count < 25){
//            Toast.makeText(this, "我靠，超过我的记录了", Toast.LENGTH_LONG).show();
//        }else if (count < 30){
//            Toast.makeText(this, "666, 我服了，继续加油", Toast.LENGTH_LONG).show();
//        }else if (count < 35){
//            Toast.makeText(this, "到达下一个难度有惊喜哟！！！", Toast.LENGTH_LONG).show();
//        }else {
//            Toast.makeText(this, "惊喜，惊喜！！！", Toast.LENGTH_LONG).show();
//        }
        Toast.makeText(this, "game over", Toast.LENGTH_LONG).show();
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("grade", count + "");
        MobclickAgent.onEvent(this, "grade", map);
        if (count > bestCount){
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putInt("bestGrade", count);
            editor.commit();
            count = 0;
        }
        right.setClickable(false);
        error.setClickable(false);
        reStart.setClickable(true);
        reStart.setBackground(getResources().getDrawable(R.drawable.rect));
        handler.removeCallbacks(runnable);
    }
    class  DrawView extends View{
        Paint p = new Paint();
        public DrawView(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            p.setColor(Color.RED);// 设置红色
            p.setStyle(Paint.Style.FILL);//设置填满
            canvas.drawRect(0, 0, x , y, p);// 长方形
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
