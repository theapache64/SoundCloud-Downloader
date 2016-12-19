package com.theah64.zoundloader.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.theah64.zoundloader.models.Track;

/**
 * Created by theapache64 on 14/12/16.
 */
public class DownloadUtils {

    private final DownloadManager dm;

    public DownloadUtils(final Context context) {
        this.dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public long addToDownloadQueue(final Track track) {

        final DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(track.getDownloadUrl()));

        downloadRequest.setTitle(track.getTitle());
        downloadRequest.setDescription(track.getDownloadUrl());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            downloadRequest.allowScanningByMediaScanner();
            downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        downloadRequest.setDestinationUri(Uri.fromFile(track.getFile()));
        return dm.enqueue(downloadRequest);

    }
}
