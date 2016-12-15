package com.theah64.soundclouddownloader.ui.activities;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.TracksAndPlaylistsViewPagerAdapter;
import com.theah64.soundclouddownloader.services.ClipboardWatchIgniterService;
import com.theah64.soundclouddownloader.utils.ClipboardUtils;
import com.theah64.soundclouddownloader.utils.CommonUtils;
import com.theah64.soundclouddownloader.utils.DownloadIgniter;
import com.theah64.soundclouddownloader.utils.InputDialogUtils;

import java.util.List;
import java.util.Set;

/**
 * TRACK MODEL : https://soundcloud.com/moxet-khan/kuch-khaas-khumariyaan-2-0
 * PLAYLIST MODEL : https://soundcloud.com/abdulwahab849/sets/more
 */
public class MainActivity extends AppCompatActivity {


    private static final String X = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //TODO: DEBUG
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !CommonUtils.isMyServiceRunning(this, ClipboardWatchIgniterService.class)) {
            //Supports clipboard listener
            startService(new Intent(this, ClipboardWatchIgniterService.class));
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager vpTracksAndPlaylists = (ViewPager) findViewById(R.id.vpTracksAndPlaylists);
        vpTracksAndPlaylists.setAdapter(new TracksAndPlaylistsViewPagerAdapter(getSupportFragmentManager(), this));

        final TabLayout tlTracksAndPlaylists = (TabLayout) findViewById(R.id.tlTracksAndPlaylists);
        tlTracksAndPlaylists.setupWithViewPager(vpTracksAndPlaylists);

        final InputDialogUtils inputDialogUtils = new InputDialogUtils(this);

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                inputDialogUtils.showInputDialog(R.string.Download_track_playlist, R.string.Enter_soundcloud_url, R.string.Enter_soundcloud_url, R.string.Invalid_soundcloud_URL, new InputDialogUtils.BasicInputCallback() {
                    @Override
                    public void onValidInput(String inputTex) {
                        DownloadIgniter.ignite(MainActivity.this, inputTex);
                    }
                }, InputDialogUtils.MAX_LENGTH_INFINITE, ClipboardUtils.SOUNDCLOUD_URL_REGEX, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, ClipboardUtils.getSoundCloudUrl(MainActivity.this));

            }
        });
    }


}
