package com.theah64.soundclouddownloader.ui.activities;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.PlaylistDownloadAdapter;
import com.theah64.soundclouddownloader.database.Playlists;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.utils.NetworkUtils;
import com.theah64.soundclouddownloader.utils.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDownloadActivity extends BaseAppCompatActivity implements PlaylistDownloadAdapter.PlaylistListener {

    public static final String KEY_TRACKS = "tracks";
    private static final String X = PlaylistDownloadActivity.class.getSimpleName();
    private static final String KEY_PLAYLIST_NAME = "playlist_name";
    private List<Track> trackList;
    private FloatingActionButton fabDownloadPlaylist;


    private int notifId;
    private NotificationManager nm;
    private NotificationCompat.Builder apiNotification;
    private String playlistName;
    private DownloadManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_download);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String soundCloudUrl = getStringOrThrow(Playlists.COLUMN_SOUNDCLOUD_URL);
        playlistName = getStringOrThrow(KEY_PLAYLIST_NAME);
        final String artworkUrl = getIntent().getStringExtra(Playlists.COLUMN_ARTWORK_URL);

        final String playlistId = String.valueOf(Playlists.getInstance(this).add(new Playlist(null, playlistName, soundCloudUrl, artworkUrl, -1, -1)));
        enableBackNavigation(playlistName);

        trackList = new ArrayList<>();
        try {
            final JSONArray jaTracks = new JSONArray(getStringOrThrow(KEY_TRACKS));

            final Tracks tracksTable = Tracks.getInstance(this);

            for (int i = 0; i < jaTracks.length(); i++) {

                final JSONObject joTrack = jaTracks.getJSONObject(i);

                final String title = joTrack.getString(Track.KEY_TITLE);
                final String fileName = joTrack.getString(Track.KEY_FILENAME);
                final String downloadUrl = joTrack.getString(Track.KEY_DOWNLOAD_URL);
                String trackArtWorkUrl = null;

                if (joTrack.has(Tracks.COLUMN_ARTWORK_URL)) {
                    trackArtWorkUrl = joTrack.getString(Tracks.COLUMN_ARTWORK_URL);
                }

                final String subPath = "/SoundCloud Downloader/" + playlistName + File.separator + fileName;
                final String absoluteFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + subPath;
                final Track newTrack = new Track(null, title, fileName, downloadUrl, subPath, trackArtWorkUrl, null, soundCloudUrl, playlistId, true, false, absoluteFilePath);
                final String dbTrackId = tracksTable.get(Tracks.COLUMN_SOUNDCLOUD_URL, downloadUrl, Tracks.COLUMN_ID);
                final String id = dbTrackId != null ? dbTrackId : String.valueOf(tracksTable.add(newTrack));
                newTrack.setId(id);
                trackList.add(newTrack);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }


        final RecyclerView rvPlaylist = (RecyclerView) findViewById(R.id.rvPlaylists);
        fabDownloadPlaylist = (FloatingActionButton) findViewById(R.id.fabDownloadPlaylist);

        rvPlaylist.setLayoutManager(new LinearLayoutManager(this));

        //noinspection unchecked
        final PlaylistDownloadAdapter adapter = new PlaylistDownloadAdapter(trackList, this);
        rvPlaylist.setAdapter(adapter);

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        apiNotification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.initializing_download))
                .setContentText("Downloading playlist")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setProgress(100, 0, true)
                .setAutoCancel(false)
                .setTicker(getString(R.string.initializing_download))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        notifId = Random.getRandomInt();

        findViewById(R.id.fabDownloadPlaylist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetworkUtils.isNetwork(PlaylistDownloadActivity.this)) {
                    nm.notify(notifId, apiNotification.build());
                    startDownload();
                    finish();
                } else {
                    Toast.makeText(PlaylistDownloadActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startDownload() {

        // Single song
        apiNotification.setContentTitle(getString(R.string.Starting_download));
        nm.notify(notifId, apiNotification.build());

        for (final Track track : trackList) {

            Log.e(X, "-------------------------------");
            Log.i(X, "Track : " + track);

            final String absFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + track.getSubPath();
            final File trackFile = new File(absFilePath);

            if (!trackFile.exists() && track.isChecked()) {
                //Starting download
                addToDownloadQueue(track.getTitle(), track.getDownloadUrl(), track.getSubPath());
                Log.i(X, "Added to download queue");
            } else if (!track.isChecked()) {
                Log.e(X, "Track unchecked");
            } else {
                Log.e(X, "Track exist");
            }

            Log.e(X, "-------------------------------");
        }

        Toast.makeText(this, R.string.download_started, Toast.LENGTH_LONG).show();
        nm.cancel(notifId);
    }

    private void addToDownloadQueue(final String title, final String downloadUrl, final String subPath) {

        final DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadUrl));

        downloadRequest.setTitle(title);
        downloadRequest.setDescription(downloadUrl);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            downloadRequest.allowScanningByMediaScanner();
            downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath);
        dm.enqueue(downloadRequest);
    }

    @Override
    public void onChecked(int position) {
        Log.i(X, "Checked: " + trackList.get(position));
        trackList.get(position).setChecked(true);
        refreshDownloadButton();
    }

    private void refreshDownloadButton() {
        for (final Track track : trackList) {
            if (track.isChecked()) {
                fabDownloadPlaylist.show();
                return;
            }
        }
        fabDownloadPlaylist.hide();
    }

    @Override
    public void onUnChecked(int position) {
        Log.e(X, "Unchecked: " + trackList.get(position));
        trackList.get(position).setChecked(false);
        refreshDownloadButton();
    }
}
