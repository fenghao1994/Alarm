package com.itca.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by fenghao on 2015/10/21.
 */
public class JpushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String jsonStr = bundle.getString(JPushInterface.EXTRA_ALERT);
        if (jsonStr != null){
            Intent t = new Intent(context, RingActivity.class);
            t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            t.putExtra("content", jsonStr);
            context.startActivity(t);
        }
    }
}
