package com.theah64.soundclouddownloader.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.server.DirectDownloader;

import java.io.File;

import timber.log.Timber;

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

    public interface Callback {
        void onAddedToDownloadQueue(long downloadId);
    }

    public void addToDownloadQueue(final Track track, final Callback callback) {

        Timber.d("Adding to download queue : " + track.getDownloadUrl() + " track:" + track);

        new Thread(new Runnable() {
            @Override
            public void run() {

                final String finalDownloadUrl = DirectDownloader.INSTANCE.getFinalDownloadLink(track.getDownloadUrl());
                if (finalDownloadUrl != null) {
                    Timber.d("run: finalDownloadUrl is %s", finalDownloadUrl);
                    final DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(finalDownloadUrl));

                    downloadRequest.setTitle(track.getTitle());
                    downloadRequest.setDescription(finalDownloadUrl);

                    downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

                    final File tempFile = new File(track.getFile().getAbsolutePath() + TEMP_SIGNATURE);
                    Timber.d("Temp file : %s", tempFile.getAbsolutePath());
                    downloadRequest.setDestinationUri(Uri.fromFile(tempFile));

                    callback.onAddedToDownloadQueue(dm.enqueue(downloadRequest));
                }
            }
        }).start();

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

        return getSubtitle3(track);
    }
}
