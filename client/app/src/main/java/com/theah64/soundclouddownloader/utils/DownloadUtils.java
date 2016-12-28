package com.theah64.soundclouddownloader.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.models.Track;

import java.io.File;

/**
 * Created by theapache64 on 14/12/16.
 */
public class DownloadUtils {

    private static final String X = DownloadUtils.class.getSimpleName();
    private static final String TEMP_SIGNATURE = ".tmp";
    private final DownloadManager dm;
    private final Context context;

    public DownloadUtils(final Context context) {
        this.context = context;
        this.dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static String getSubtitle3(final Track track) {
        if (track.isDownloaded() && track.getFile().exists()) {
            return "(Saved)";
        } else if (track.isDownloaded() && !track.getFile().exists()) {
            return "(Saved but moved/deleted)";
        } else {
            return null;
        }
    }

    public long addToDownloadQueue(final Track track) {

        Log.d(X, "Adding to download queue : " + track.getDownloadUrl() + " track:" + track);

        final DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(track.getDownloadUrl()));

        downloadRequest.setTitle(track.getTitle());
        downloadRequest.setDescription(track.getDownloadUrl());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }

        final File tempFile = new File(track.getFile().getAbsolutePath() + TEMP_SIGNATURE);
        Log.d(X, "Temp file : " + tempFile.getAbsolutePath());
        downloadRequest.setDestinationUri(Uri.fromFile(tempFile));
        return dm.enqueue(downloadRequest);

    }

    public String getVerbalStatus(final Track track) {

        Log.d(X, "Requested for verbal status");


        if (track.getDownloadId() != null) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(Long.parseLong(track.getDownloadId()));

            final Cursor cursor = dm.query(query);
            if (cursor != null) {
                if (cursor.moveToFirst()) {

                    final int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    switch (downloadStatus) {

                        case DownloadManager.STATUS_FAILED:
                            return context.getString(R.string.Download_failed);
                        case DownloadManager.STATUS_PAUSED:
                            return context.getString(R.string.Download_paused);
                        case DownloadManager.STATUS_PENDING:
                            return context.getString(R.string.Download_pending);

                        case DownloadManager.STATUS_RUNNING:
                            return context.getString(R.string.Downloading);

                        default:
                        case DownloadManager.STATUS_SUCCESSFUL:
                            return getSubtitle3(track);

                    }
                }
                cursor.close();
            }
        }

        return null;
    }
}
