package com.theah64.zoundloader.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.theah64.zoundloader.R;
import com.theah64.zoundloader.interfaces.MainActivityCallback;
import com.theah64.zoundloader.models.Playlist;
import com.theah64.zoundloader.ui.fragments.TracksFragment;

public class PlaylistTracksActivity extends BaseAppCompatActivity implements MainActivityCallback {

    private static final String X = PlaylistTracksActivity.class.getSimpleName();

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

        final TracksFragment tracksFragment = TracksFragment.getNewInstance(playlist.getId());
        getSupportFragmentManager().beginTransaction().replace(R.id.flPlaylistTracksContainer, tracksFragment).commit();
    }


    @Override
    public void onRemovePlaylistTrack(String playlistId) {

    }

    @Override
    public void onRemovePlaylist(String playlistId) {

    }

    @Override
    public void setTabTracksCount(int count) {

    }

    @Override
    public void setTabPlaylistsCount(int count) {

    }

    @Override
    public void hideFabAdd() {
        
    }

    @Override
    public void showFabAdd() {

    }

    @Override
    public boolean isFabAddShown() {
        return false;
    }
}