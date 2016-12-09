package com.theah64.soundclouddownloader.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.activities.DownloaderActivity;
import com.theah64.soundclouddownloader.utils.Random;

public class DownloaderService extends Service {

    private static final String API_KEY = "android-client-key";
    private static final String X = DownloaderService.class.getSimpleName();
    private int notifId;
    private NotificationManager nm;
    private Notification apiNotification;

    public DownloaderService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String soundCloudUrl = intent.getStringExtra(DownloaderActivity.KEY_SOUNDCLOUD_URL);

        apiNotification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.initializing_download))
                .setContentText(soundCloudUrl)
                .setProgress(100, 0, true)
                .setAutoCancel(false)
                .setTicker(getString(R.string.initializing_download))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .build();

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
