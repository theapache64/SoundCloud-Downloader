package com.theah64.soundclouddownloader.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.ui.fragments.PlaylistsFragment;
import com.theah64.soundclouddownloader.ui.fragments.TracksFragment;

/**
 * Created by theapache64 on 12/12/16.
 */

public class TracksAndPlaylistsViewPagerAdapter extends FragmentPagerAdapter {

    private TracksFragment tracksFragment = new TracksFragment();
    private PlaylistsFragment playlistsFragment = new PlaylistsFragment();

    public TracksAndPlaylistsViewPagerAdapter(FragmentManager fm) {
        super(fm);
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
        return position == 0 ? "TRACKS" : "PLAYLISTS";
    }
}
