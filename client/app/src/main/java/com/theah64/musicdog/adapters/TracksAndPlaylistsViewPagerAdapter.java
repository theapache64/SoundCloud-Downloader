package com.theah64.musicdog.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.theah64.musicdog.database.Playlists;
import com.theah64.musicdog.database.Tracks;

import java.util.Locale;

/**
 * Created by theapache64 on 12/12/16.
 */

public class TracksAndPlaylistsViewPagerAdapter extends FragmentPagerAdapter {

    private final Context context;
    private final Fragment tracksFragment, playlistsFragment;

    public TracksAndPlaylistsViewPagerAdapter(FragmentManager fm, Context context, Fragment tracksFragment, Fragment playlistsFragment) {
        super(fm);
        this.context = context;
        this.tracksFragment = tracksFragment;
        this.playlistsFragment = playlistsFragment;
    }

    @Override
    public Fragment getItem(int position) {
        return position == 0 ? tracksFragment : playlistsFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.format(Locale.getDefault(), "%s (%d)", position == 0 ? "TRACKS" : "PLAYLISTS", position == 0 ? Tracks.getInstance(context).getCount() : Playlists.getInstance(context).getCount());
    }
}
