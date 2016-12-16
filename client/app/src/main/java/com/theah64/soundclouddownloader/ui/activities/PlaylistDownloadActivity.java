package com.theah64.soundclouddownloader.ui.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.PlaylistDownloadAdapter;
import com.theah64.soundclouddownloader.database.Playlists;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.utils.App;
import com.theah64.soundclouddownloader.utils.DownloadUtils;
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
    private PlaylistDownloadAdapter adapter;
    private Tracks tracksTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_download);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String playlistName = getStringOrThrow(KEY_PLAYLIST_NAME);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(playlistName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final String soundCloudUrl = getStringOrThrow(Playlists.COLUMN_SOUNDCLOUD_URL);

        final String artworkUrl = getIntent().getStringExtra(Playlists.COLUMN_ARTWORK_URL);

        final Playlists playlistsTable = Playlists.getInstance(this);

        String playlistId = playlistsTable.get(Playlists.COLUMN_SOUNDCLOUD_URL, soundCloudUrl, Playlists.COLUMN_ID);

        if (playlistId == null) {
            playlistId = String.valueOf(Playlists.getInstance(this).add(new Playlist(null, playlistName, soundCloudUrl, artworkUrl, -1, -1)));
        }


        trackList = new ArrayList<>();
        try {
            final JSONArray jaTracks = new JSONArray(getStringOrThrow(KEY_TRACKS));

            tracksTable = Tracks.getInstance(this);

            for (int i = 0; i < jaTracks.length(); i++) {

                final JSONObject joTrack = jaTracks.getJSONObject(i);

                final String title = joTrack.getString(Track.KEY_TITLE);
                final String fileName = joTrack.getString(Track.KEY_FILENAME);
                final String trackSoundCloudUrl = joTrack.getString(Tracks.COLUMN_SOUNDCLOUD_URL);
                final String downloadUrl = joTrack.getString(Track.KEY_DOWNLOAD_URL);
                String trackArtWorkUrl = null;

                if (joTrack.has(Tracks.COLUMN_ARTWORK_URL)) {
                    trackArtWorkUrl = joTrack.getString(Tracks.COLUMN_ARTWORK_URL);
                }

                final String absoluteFilePath = String.format("%s/%s/%s/%s", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), App.FOLDER_NAME, playlistName, fileName);
                final Track newTrack = new Track(null, title, downloadUrl, trackArtWorkUrl, null, trackSoundCloudUrl, playlistId, true, false, new File(absoluteFilePath));
                final String dbTrackId = tracksTable.get(Tracks.COLUMN_SOUNDCLOUD_URL, trackSoundCloudUrl, Tracks.COLUMN_ID);
                final String id = dbTrackId != null ? dbTrackId : String.valueOf(tracksTable.add(newTrack));

                newTrack.setId(id);
                //Uncheck if file exists
                newTrack.setChecked(!newTrack.getFile().exists());

                trackList.add(newTrack);
            }

            actionBar.setSubtitle(getResources().getQuantityString(R.plurals.d_tracks, trackList.size(), trackList.size()));

        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }


        final RecyclerView rvPlaylist = (RecyclerView) findViewById(R.id.rvPlaylists);
        fabDownloadPlaylist = (FloatingActionButton) findViewById(R.id.fabDownloadPlaylist);

        rvPlaylist.setLayoutManager(new LinearLayoutManager(this));

        //noinspection unchecked
        adapter = new PlaylistDownloadAdapter(this, trackList, this);
        rvPlaylist.setAdapter(adapter);

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        apiNotification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.initializing_download))
                .setContentText(getString(R.string.Downloading_playlist))
                .setSmallIcon(R.drawable.ic_stat_logo_white)
                .setProgress(100, 0, true)
                .setAutoCancel(false)
                .setTicker(getString(R.string.initializing_download))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_color_24dp));

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

        refreshDownloadButton();

    }

    private void startDownload() {

        // Single song
        apiNotification.setContentTitle(getString(R.string.Starting_download));
        nm.notify(notifId, apiNotification.build());

        for (final Track track : trackList) {

            Log.e(X, "-------------------------------");
            Log.i(X, "Track : " + track);

            if ((track.getFile() == null || !track.getFile().exists()) && track.isChecked()) {
                //Starting download
                final long downloadId = DownloadUtils.addToDownloadQueue(this, track);
                if (!tracksTable.update(Tracks.COLUMN_ID, track.getId(), Tracks.COLUMN_DOWNLOAD_ID, String.valueOf(downloadId))) {
                    throw new IllegalArgumentException("Failed to set download id");
                }

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

    @Override
    public void onChecked(int position) {
        Log.i(X, "Checked: " + trackList.get(position));
        trackList.get(position).setChecked(true);
        refreshDownloadButton();

        if (menu != null) {

            final MenuItem miUnCheckAllTracks = menu.findItem(R.id.miUnCheckAllTracks);

            boolean isFullTicked = true;
            for (final Track track : trackList) {
                if (!track.isChecked()) {
                    isFullTicked = false;
                }
            }

            if (!miUnCheckAllTracks.isVisible() && isFullTicked) {
                miUnCheckAllTracks.setVisible(true);
                menu.findItem(R.id.miCheckAllTracks).setVisible(false);
            }
        }
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

        if (menu != null) {

            boolean isFullTicked = false;
            for (final Track track : trackList) {
                if (track.isChecked()) {
                    isFullTicked = true;
                }
            }

            final MenuItem miCheckAllTracks = menu.findItem(R.id.miCheckAllTracks);
            if (!miCheckAllTracks.isVisible() || !isFullTicked) {
                miCheckAllTracks.setVisible(true);
                menu.findItem(R.id.miUnCheckAllTracks).setVisible(false);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlist_tracks, menu);
        this.menu = menu;

        boolean isEveryTrackUnChecked = true;
        for (final Track track : trackList) {
            if (track.isChecked()) {
                isEveryTrackUnChecked = false;
                break;
            }
        }

        if (isEveryTrackUnChecked) {
            menu.findItem(R.id.miUnCheckAllTracks).setVisible(false);
            menu.findItem(R.id.miCheckAllTracks).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.miCheckAllTracks:

                for (final Track track : trackList) {
                    track.setChecked(true);
                }
                adapter.notifyDataSetChanged();
                refreshDownloadButton();

                menu.findItem(R.id.miCheckAllTracks).setVisible(false);
                menu.findItem(R.id.miUnCheckAllTracks).setVisible(true);

                return true;

            case R.id.miUnCheckAllTracks:

                for (final Track track : trackList) {
                    track.setChecked(false);
                }

                adapter.notifyDataSetChanged();
                refreshDownloadButton();

                menu.findItem(R.id.miCheckAllTracks).setVisible(true);
                menu.findItem(R.id.miUnCheckAllTracks).setVisible(false);

                return true;

            default:
                return false;
        }
    }
}
