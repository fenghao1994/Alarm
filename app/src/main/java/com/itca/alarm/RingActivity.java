package com.itca.alarm;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * Created by fenghao on 2015/10/21.
 */
public class RingActivity extends Activity implements View.OnClickListener{
    RelativeLayout stop;
    TextView text;
    Ringtone r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        stop = (RelativeLayout) findViewById(R.id.stop);
        text = (TextView) findViewById(R.id.text);
        String content = getIntent().getStringExtra("content");
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(this, notification);
        r.setStreamType(RingtoneManager.TYPE_ALARM);
        r.play();
        text.setText(content);
        stop.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stop:
                r.stop();
                MobclickAgent.onEvent(this,"stop");break;
        }
    }
}
