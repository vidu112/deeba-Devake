package com.deeba.deebadriver;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.deeba.deebadriver.App.CHANNEL_ID;

public class locationService extends Service {
    boolean test;
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input=intent.getStringExtra("input");
//        Intent notificationIntent= new Intent(this,LoginDriver.class);
//        PendingIntent pendingIntent= PendingIntent.getActivities(this,
//                0,notificationIntent,0);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Fleet Driver")
                .setContentText(input)
                .build();
        startForeground(1,notification);
        while(test){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            }, 1000);
        }
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}