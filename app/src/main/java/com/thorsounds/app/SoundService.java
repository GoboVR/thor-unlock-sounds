package com.thorsounds.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

public class SoundService extends Service {
    private static final String CHANNEL = "thor_sounds";

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            String a = i.getAction();
            if (Intent.ACTION_USER_PRESENT.equals(a)) play(R.raw.hello);
            else if (Intent.ACTION_SCREEN_OFF.equals(a)) play(R.raw.bye);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(CHANNEL, "Thor Sounds",
                NotificationManager.IMPORTANCE_MIN));
        Notification n = new Notification.Builder(this, CHANNEL)
                .setContentTitle("Thor Sounds active")
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
                .build();
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(1, n);
        }
        IntentFilter f = new IntentFilter();
        f.addAction(Intent.ACTION_USER_PRESENT);
        f.addAction(Intent.ACTION_SCREEN_OFF);
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(receiver, f, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(receiver, f);
        }
    }

    private void play(int res) {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "thorsounds:play");
        wl.acquire(10000);
        MediaPlayer mp = MediaPlayer.create(this, res);
        if (mp == null) {
            if (wl.isHeld()) wl.release();
            return;
        }
        mp.setOnCompletionListener(m -> {
            m.release();
            if (wl.isHeld()) wl.release();
        });
        mp.setOnErrorListener((m, w, e) -> {
            m.release();
            if (wl.isHeld()) wl.release();
            return true;
        });
        mp.start();
    }

    @Override
    public int onStartCommand(Intent i, int flags, int id) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }
}
