package com.theah64.soundclouddownloader.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.interfaces.TrackListener;
import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.ui.fragments.TracksFragment;
import com.theah64.soundclouddownloader.utils.App;

public class PlaylistTracksActivity extends BaseAppCompatActivity implements TrackListener {

    private static final String X = PlaylistTracksActivity.class.getSimpleName();
    protected App app;
    private TracksFragment tracksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Playlist playlist = (Playlist) getSerializableOrThrow(Playlist.KEY);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(playlist.getTitle());
        actionBar.setSubtitle(getResources().getQuantityString(R.plurals.d_tracks, playlist.getTotalTracks(), playlist.getTotalTracks()));

        tracksFragment = TracksFragment.getNewInstance(playlist.getId());
        getSupportFragmentManager().beginTransaction().replace(R.id.flPlaylistTracksContainer, tracksFragment).commit();
        app = (App) getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.setPlaylistTrackListener(this);
    }

    @Override
    protected void onDestroy() {
        final TrackListener trackListener = app.getPlaylistTrackListener();
        if (this.equals(trackListener)) {
            app.setPlaylistTrackListener(null);
        }

        super.onDestroy();
    }

    @Override
    public void onTrackUpdated(final Track track) {
        Log.d(X, "MainActivity says: TRACK DOWNLOADED " + track);
        tracksFragment.onTrackUpdated(track);
    }

}
