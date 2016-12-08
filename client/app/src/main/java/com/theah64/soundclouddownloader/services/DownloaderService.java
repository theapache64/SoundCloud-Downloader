package com.theah64.soundclouddownloader.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.theah64.soundclouddownloader.utils.Random;

public class DownloaderService extends Service {

    private int notifId;

    public DownloaderService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Notification apiNotification = new NotificationCompat.Builder(this)
                .setContentTitle("Initialzing soundcloud music download")
                .build();

        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifId = Random.getRandomInt();
        nm.notify(notifId, apiNotification);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
