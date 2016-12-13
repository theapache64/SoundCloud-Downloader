package com.theah64.soundclouddownloader.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.Track;

public class OnDownloadFinishedReceiver extends BroadcastReceiver {

    private static final String X = OnDownloadFinishedReceiver.class.getSimpleName();

    public OnDownloadFinishedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String downloadId = String.valueOf(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1));
        Log.d(X, "Download finished :  id : " + downloadId);
        if (!Tracks.getInstance(context).update(Tracks.COLUMN_DOWNLOAD_ID, downloadId, Tracks.COLUMN_IS_DOWNLOADED, Tracks.TRUE)) {
            throw new IllegalArgumentException("Failed to update download status");
        }

    }
}
