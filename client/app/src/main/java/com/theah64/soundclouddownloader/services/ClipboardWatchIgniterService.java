package com.theah64.soundclouddownloader.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.utils.ClipboardUtils;
import com.theah64.soundclouddownloader.utils.Random;

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class ClipboardWatchIgniterService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {
    private static final String X = ClipboardWatchIgniterService.class.getSimpleName();

    public ClipboardWatchIgniterService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(X, "Registering new clipboard watcher...");
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(this);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onPrimaryClipChanged() {
        Log.d(X, "Clipboard changed...");
        final String soundCloudUrl = ClipboardUtils.getSoundCloudUrl(this);

        if (soundCloudUrl != null) {

            final int notifId = Random.getRandomInt();
            final String title = getString(R.string.Do_you_want_to_download_this_s, Playlist.isPlaylist(soundCloudUrl) ? "playlist" : "track");

            final Intent yesIntent = new Intent(this, DownloaderService.class);
            yesIntent.putExtra(Tracks.COLUMN_SOUNDCLOUD_URL, soundCloudUrl);
            yesIntent.putExtra(DownloaderService.KEY_NOTIFICATION_ID, notifId);

            final PendingIntent downloadIntent = PendingIntent.getService(this, 1, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            final PendingIntent dismissIntent = DownloaderService.getDismissIntent(notifId, this);

            final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setTicker(title)
                    .setSmallIcon(R.drawable.ic_stat_logo_white)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_color_24dp))
                    .setContentTitle(title)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_thumb_up_white_24dp, getString(R.string.YES), downloadIntent)
                    .addAction(R.drawable.ic_thumb_down_white_24dp, getString(R.string.NO), dismissIntent)
                    .setContentText(soundCloudUrl);

            nm.notify(notifId, notBuilder.build());

        }
    }
}
