package com.theah64.soundclouddownloader.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.TracksAndPlaylistsViewPagerAdapter;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager vpTracksAndPlaylists = (ViewPager) findViewById(R.id.vpTracksAndPlaylists);
        vpTracksAndPlaylists.setAdapter(new TracksAndPlaylistsViewPagerAdapter(getSupportFragmentManager(), this));

        final TabLayout tlTracksAndPlaylists = (TabLayout) findViewById(R.id.tlTracksAndPlaylists);
        tlTracksAndPlaylists.setupWithViewPager(vpTracksAndPlaylists);

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Show a dialogue to enter soundcloud url
            }
        });
    }


}
