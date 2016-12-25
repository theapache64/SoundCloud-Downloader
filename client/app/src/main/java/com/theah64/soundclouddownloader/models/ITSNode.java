package com.theah64.soundclouddownloader.models;

import android.support.annotation.Nullable;

import com.theah64.soundclouddownloader.utils.DownloadUtils;

/**
 * Created by theapache64 on 11/12/16.
 */

public interface ITSNode {

    String getArtworkUrl();

    String getTitle();

    String getSubtitle1();

    String getSubtitle2();

    String getSubtitle3(@Nullable DownloadUtils downloadUtils);

    boolean isChecked();
}
