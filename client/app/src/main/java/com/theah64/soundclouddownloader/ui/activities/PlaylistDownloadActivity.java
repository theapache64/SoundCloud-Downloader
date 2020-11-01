package com.theah64.soundclouddownloader.ui.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.PlaylistDownloadAdapter;
import com.theah64.soundclouddownloader.database.Playlists;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.ui.activities.settings.SettingsActivity;
import com.theah64.soundclouddownloader.utils.APIRequestGateway;
import com.theah64.soundclouddownloader.App;
import com.theah64.soundclouddownloader.utils.DownloadUtils;
import com.theah64.soundclouddownloader.utils.NetworkUtils;
import com.theah64.soundclouddownloader.utils.PrefUtils;
import com.theah64.soundclouddownloader.utils.Random;
import com.theah64.soundclouddownloader.utils.SingletonToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDownloadActivity extends BaseAppCompatActivity implements PlaylistDownloadAdapter.PlaylistListener {

    public static final String KEY_TRACKS = "tracks";
    private static final String X = PlaylistDownloadActivity.class.getSimpleName();
    public static final String KEY_REQUEST_ID = "request_id";
    private List<Track> trackList;
    private FloatingActionButton fabDownloadPlaylist;

    private int notifId;
    private NotificationManager nm;
    private NotificationCompat.Builder apiNotification;
    private PlaylistDownloadAdapter adapter;
    private Tracks tracksTable;
    private Playlist playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_download);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        final String requestId = getStringOrThrow(KEY_REQUEST_ID);
        final String apiKey = PrefUtils.getInstance(this).getPref().getString(APIRequestGateway.KEY_API_KEY, null);

        if (apiKey == null) {
            throw new IllegalArgumentException("No api_key found");
        }

        playlist = (Playlist) getSerializableOrThrow(Playlist.KEY);
        actionBar.setTitle(playlist.getTitle());

        final Playlists playlistsTable = Playlists.getInstance(this);
        String playlistId = playlistsTable.get(Playlists.COLUMN_SOUNDCLOUD_URL, playlist.getSoundCloudUrl(), Playlists.COLUMN_ID);

        if (playlistId == null) {
            playlistId = String.valueOf(playlistsTable.add(playlist, null));
        }
        playlist.setId(playlistId);

        trackList = new ArrayList<>();
        try {
            final JSONArray jaTracks = new JSONArray(getStringOrThrow(KEY_TRACKS));

            tracksTable = Tracks.getInstance(this);
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);


            for (int i = 0; i < jaTracks.length(); i++) {

                final JSONObject joTrack = jaTracks.getJSONObject(i);

                final String trackId = joTrack.getString(Tracks.COLUMN_ID);
                final String title = joTrack.getString(Track.KEY_TITLE);
                final String fileName = joTrack.getString(Track.KEY_FILENAME);
                final String trackSoundCloudUrl = joTrack.getString(Tracks.COLUMN_SOUNDCLOUD_URL);

                final String downloadUrl = String.format(Track.DOWNLOAD_URL_FORMAT, requestId, trackId, apiKey);

                String trackArtWorkUrl = null;

                if (joTrack.has(Tracks.COLUMN_ARTWORK_URL)) {
                    trackArtWorkUrl = joTrack.getString(Tracks.COLUMN_ARTWORK_URL);
                }

                final String username = joTrack.getString(Tracks.COLUMN_USERNAME);
                final long duration = joTrack.getLong(Tracks.COLUMN_DURATION);

                final String baseStorageLocation = pref.getString(SettingsActivity.SettingsFragment.KEY_STORAGE_LOCATION, App.getDefaultStorageLocation());
                final String absoluteFilePath = String.format("%s/%s/%s", baseStorageLocation, playlist.getSanitizedTitle(), fileName);

                final Track dbTrack = tracksTable.get(Tracks.COLUMN_SOUNDCLOUD_URL, trackSoundCloudUrl);

                final Track newTrack = new Track(null, title, username, downloadUrl, trackArtWorkUrl, null, trackSoundCloudUrl, playlist.getId(), true, false, new File(absoluteFilePath), duration) {
                    @Override
                    public String getSubtitle3(final DownloadUtils downloadUtils) {

                        if (isExistInStorage()) {
                            //noinspection ConstantConditions
                            return "#Existing p-track ../" + getFile().getParentFile().getName() + "/" + getFile().getName();
                        } else if (dbTrack != null && dbTrack.isExistInStorage()) {
                            return "#Existing track ../" + dbTrack.getFile().getParentFile().getName() + "/" + dbTrack.getFile().getName();
                        } else if ((isDownloaded() && !isExistInStorage()) || dbTrack != null && dbTrack.isDownloaded() && !dbTrack.isExistInStorage()) {
                            return "(Saved but moved/deleted)";
                        } else {
                            return null;
                        }

                    }
                };

                Log.d(X, "---------------------------");

                Log.d(X, newTrack.getTitle());
                Log.d(X, "!newTrack.isExistInStorage() " + !newTrack.isExistInStorage());
                Log.d(X, "!(dbTrack != null && dbTrack.isExistInStorage())" + !(dbTrack != null && dbTrack.isExistInStorage()));
                Log.d(X, "!dbTrack.isDownloaded()" + (dbTrack != null && !dbTrack.isDownloaded()));

                final boolean isChecked = newTrack.isExistInStorage() || (dbTrack != null && dbTrack.isExistInStorage()) || (dbTrack != null && dbTrack.isDownloaded());
                Log.d(X, "isChecked: " + isChecked);
                newTrack.setChecked(!isChecked);

                Log.d(X, "---------------------------");


                String dbTrackId;
                if (dbTrack != null) {

                    //Track exist in db,so updating playlist id
                    if (!dbTrack.getPlaylistId().equals(playlist.getId()) && !tracksTable.update(Tracks.COLUMN_ID, dbTrack.getId(), Tracks.COLUMN_PLAYLIST_ID, playlist.getId(), null)) {
                        throw new IllegalArgumentException("Failed to update playlist id");
                    }

                    dbTrackId = dbTrack.getId();

                } else {
                    dbTrackId = String.valueOf(tracksTable.add(newTrack, null));
                }

                newTrack.setId(dbTrackId);

                if (newTrack.isExistInStorage() && !newTrack.isDownloaded()) {
                    tracksTable.update(Tracks.COLUMN_ID, newTrack.getId(), Tracks.COLUMN_IS_DOWNLOADED, Tracks.TRUE, null);
                }

                trackList.add(newTrack);
            }

            playlist.setTotalTracks(trackList.size());

            actionBar.setSubtitle(getResources().getQuantityString(R.plurals.d_tracks, trackList.size(), trackList.size()));

        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }


        final RecyclerView rvPlaylist = (RecyclerView) findViewById(R.id.rvPlaylists);
        fabDownloadPlaylist = (FloatingActionButton) findViewById(R.id.fabDownloadPlaylist);

        rvPlaylist.setLayoutManager(new LinearLayoutManager(this));

        //noinspection unchecked
        adapter = new PlaylistDownloadAdapter(trackList, this);
        rvPlaylist.setAdapter(adapter);

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        apiNotification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.initializing_download))
                .setContentText(getString(R.string.Downloading_playlist))
                .setSmallIcon(R.drawable.ic_stat_logo_white)
                .setProgress(100, 0, true)
                .setAutoCancel(false)
                .setTicker(getString(R.string.initializing_download))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_24dp));

        notifId = Random.getRandomInt();

        findViewById(R.id.fabDownloadPlaylist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetworkUtils.hasNetwork(PlaylistDownloadActivity.this)) {
                    nm.notify(notifId, apiNotification.build());
                    startDownload();
                    finish();
                } else {
                    SingletonToast.makeText(PlaylistDownloadActivity.this, R.string.network_error).show();
                }
            }
        });

        refreshDownloadButton();
    }

    @Override
    public boolean isSecureActivity() {
        return true;
    }

    private void startDownload() {

        final DownloadUtils downloadUtils = new DownloadUtils(this);

        // Single song
        apiNotification.setContentTitle(getString(R.string.Starting_download));
        nm.notify(notifId, apiNotification.build());

        for (final Track track : trackList) {

            Log.e(X, "-------------------------------");
            Log.i(X, "Track : " + track);

            if (track.isChecked()) {
                //Starting download
                final long downloadId = downloadUtils.addToDownloadQueue(track);
                if (!tracksTable.update(Tracks.COLUMN_ID, track.getId(), Tracks.COLUMN_DOWNLOAD_ID, String.valueOf(downloadId), null)) {
                    throw new IllegalArgumentException("Failed to set download id");
                }

                Log.i(X, "Added to download queue");
            }

            Log.e(X, "-------------------------------");
        }

        SingletonToast.makeText(this, R.string.download_started).show();
        nm.cancel(notifId);

        if (playlist.getTotalTracks() > 0) {
            //Showing playlist tracks
            final Intent playlistTracksAct = new Intent(this, PlaylistTracksActivity.class);
            playlistTracksAct.putExtra(Playlist.KEY, playlist);
            startActivity(playlistTracksAct);
        }

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
