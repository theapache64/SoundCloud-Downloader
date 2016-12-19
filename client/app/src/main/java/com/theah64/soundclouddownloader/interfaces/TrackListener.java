package com.theah64.soundclouddownloader.interfaces;

import android.support.annotation.Nullable;

import com.theah64.soundclouddownloader.models.Track;

/**
 * Created by theapache64 on 16/12/16.
 */

public interface TrackListener {
    int TRACK_POSITION_UNKNOWN = -1;

    void onNewTrack(Track newTrack);

    void onTrackRemoved(@Nullable  Track track, int position);

    void onTrackUpdated(Track updatedTrack);
}
