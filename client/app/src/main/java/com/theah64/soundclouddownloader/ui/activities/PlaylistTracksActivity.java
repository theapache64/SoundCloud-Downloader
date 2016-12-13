package com.theah64.soundclouddownloader.ui.activities;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.ui.fragments.TracksFragment;

public class PlaylistTracksActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks);

        final Playlist playlist = (Playlist) getSerializableOrThrow(Playlist.KEY);
        enableBackNavigation(playlist.getTitle());

        final TracksFragment tracksFragment = TracksFragment.getNewInstance(playlist.getId());

    }

}
