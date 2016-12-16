package com.theah64.soundclouddownloader.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.interfaces.TrackListener;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.utils.App;

public class OnDownloadFinishedReceiver extends BroadcastReceiver {

    private static final String X = OnDownloadFinishedReceiver.class.getSimpleName();

    public OnDownloadFinishedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String downloadId = String.valueOf(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1));
        Log.d(X, "Download finished :  id : " + downloadId);

/*        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final Cursor cursor = dm.query(query);

        Log.d(X, "Cursor : " + cursor);
        if (cursor != null) {

            if (cursor.moveToFirst()) {
                final String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                final String LocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                Log.d(X, "URI: " + uri + "\nLOCAL URI:" + LocalUri);
            }

            cursor.close();
        }*/

        final Tracks tracksTable = Tracks.getInstance(context);

        if (!tracksTable.update(Tracks.COLUMN_DOWNLOAD_ID, downloadId, Tracks.COLUMN_IS_DOWNLOADED, Tracks.TRUE)) {
            throw new IllegalArgumentException("Failed to update download status");
        }

        final App app = ((App) context.getApplicationContext());
        TrackListener mainTrackListener = app.getMainTrackListener();
        TrackListener playlistTrackListener = app.getPlaylistTrackListener();

        final Track track = tracksTable.get(Tracks.COLUMN_DOWNLOAD_ID, downloadId);

        if (mainTrackListener != null) {
            mainTrackListener.onTrackUpdated(track);
        }

        if (playlistTrackListener != null) {
            playlistTrackListener.onTrackUpdated(track);
        }


    }
}
