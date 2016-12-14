package com.theah64.soundclouddownloader.ui.activities;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.ui.fragments.TracksFragment;

public class PlaylistTracksActivity extends BaseAppCompatActivity {

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
        actionBar.setSubtitle(getResources().getQuantityString(R.plurals.pluralTracks, playlist.getTotalTracks(), playlist.getTotalTracks()));

        final TracksFragment tracksFragment = TracksFragment.getNewInstance(playlist.getId());
        getSupportFragmentManager().beginTransaction().replace(R.id.flPlaylistTracksContainer, tracksFragment).commit();
    }

}
