package com.theah64.soundclouddownloader.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by theapache64 on 15/12/16.
 */

public class BaseMusicFragment extends Fragment {

    protected void openSoundCloud() {
        final Intent soundCloudIntent = new Intent(Intent.ACTION_VIEW);
        soundCloudIntent.setData(Uri.parse("http://soundcloud.com"));
        startActivity(soundCloudIntent);
    }

}
