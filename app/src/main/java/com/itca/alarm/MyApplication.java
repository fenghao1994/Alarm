package com.itca.alarm;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by fenghao on 2015/10/21.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);

//        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.setDebugMode(true);
    }
}
