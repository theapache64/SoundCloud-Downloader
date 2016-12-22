package com.theah64.soundclouddownloader.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.Track;

public class OnDownloadFinishedReceiver extends BroadcastReceiver {

    private static final String X = OnDownloadFinishedReceiver.class.getSimpleName();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public OnDownloadFinishedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        final String stringDownloadId = String.valueOf(downloadId);
        Log.d(X, "Download finished :  id : " + downloadId);

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final Cursor cursor = dm.query(query);

        Log.d(X, "Cursor : " + cursor);
        if (cursor != null) {

            if (cursor.moveToFirst()) {

                final int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {

                    final Tracks tracksTable = Tracks.getInstance(context);

                    if (!tracksTable.update(Tracks.COLUMN_DOWNLOAD_ID, stringDownloadId, Tracks.COLUMN_IS_DOWNLOADED, Tracks.TRUE, handler)) {
                        throw new IllegalArgumentException("Failed to update download status");
                    }

                    final Track downloadedTrack = tracksTable.get(Tracks.COLUMN_DOWNLOAD_ID, stringDownloadId);

                    if (downloadedTrack != null) {
                        Toast.makeText(context, "Track downloaded -> " + downloadedTrack.getTitle(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e(X, "Download status : " + downloadStatus);
                }

            }

            cursor.close();
        } else {
            Log.e(X, "Couldn't find download with download id " + stringDownloadId);
        }


    }
}
