package com.theah64.soundclouddownloader.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.TracksAndPlaylistsViewPagerAdapter;
import com.theah64.soundclouddownloader.database.Playlists;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.interfaces.MainActivityCallback;
import com.theah64.soundclouddownloader.interfaces.TrackListener;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.services.ClipboardWatchIgniterService;
import com.theah64.soundclouddownloader.ui.activities.settings.SettingsActivity;
import com.theah64.soundclouddownloader.ui.activities.settings.SettingsActivityCompat;
import com.theah64.soundclouddownloader.ui.fragments.PlaylistsFragment;
import com.theah64.soundclouddownloader.ui.fragments.TracksFragment;
import com.theah64.soundclouddownloader.utils.App;
import com.theah64.soundclouddownloader.utils.ClipboardUtils;
import com.theah64.soundclouddownloader.utils.CommonUtils;
import com.theah64.soundclouddownloader.utils.DownloadIgniter;
import com.theah64.soundclouddownloader.utils.InputDialogUtils;

import java.util.Locale;

/**
 * TRACK MODEL : https://soundcloud.com/moxet-khan/kuch-khaas-khumariyaan-2-0
 * PLAYLIST MODEL : https://soundcloud.com/abdulwahab849/sets/more
 */
public class MainActivity extends AppCompatActivity implements MainActivityCallback {


    private TracksFragment tracksFragment = TracksFragment.getNewInstance(null);
    private PlaylistsFragment playlistsFragment = new PlaylistsFragment();

    private static final String X = MainActivity.class.getSimpleName();
    private TabLayout.Tab tracksTab, playlistsTab;

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
        vpTracksAndPlaylists.setAdapter(new TracksAndPlaylistsViewPagerAdapter(getSupportFragmentManager(), this, tracksFragment, playlistsFragment));

        final TabLayout tlTracksAndPlaylists = (TabLayout) findViewById(R.id.tlTracksAndPlaylists);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tlTracksAndPlaylists.setTabTextColors(getColor(R.color.black_800), getColor(R.color.deep_orange_500));
        } else {
            //noinspection deprecation
            tlTracksAndPlaylists.setTabTextColors(getResources().getColor(R.color.black_800), getResources().getColor(R.color.deep_orange_500));
        }

        tlTracksAndPlaylists.setupWithViewPager(vpTracksAndPlaylists);

        final InputDialogUtils inputDialogUtils = new InputDialogUtils(this);

        //Add soundcloud url dialog.
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

        tracksTab = tlTracksAndPlaylists.getTabAt(0);
        playlistsTab = tlTracksAndPlaylists.getTabAt(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //Starting brand new settings activity
            startActivity(new Intent(this, SettingsActivity.class));
        } else {
            //Launching compat settings activity.
            startActivity(new Intent(this, SettingsActivityCompat.class));
        }


        return true;
    }

    @Override
    public void onRemovePlaylistTrack(String playlistId) {
        playlistsFragment.onPlaylistUpdated(playlistId);
    }

    @Override
    public void onRemovePlaylist(String playlistId) {
        tracksFragment.onPlaylistRemoved(playlistId);
    }

    @Override
    public void setTabTracksCount(int count) {
        tracksTab.setText(String.format(Locale.getDefault(), "TRACKS (%d)", count));
    }

    @Override
    public void setTabPlaylistsCount(int count) {
        playlistsTab.setText(String.format(Locale.getDefault(), "PLAYLISTS (%d)", count));
    }
}
