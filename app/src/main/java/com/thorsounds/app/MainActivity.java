package com.thorsounds.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
        startForegroundService(new Intent(this, SoundService.class));
        TextView t = new TextView(this);
        t.setText("Thor Sounds is running.\n\nUnlock = hello sound, sleep = bye sound.\nYou can close this screen.");
        t.setTextSize(18);
        t.setGravity(Gravity.CENTER);
        t.setPadding(48, 48, 48, 48);
        setContentView(t);
    }
}
