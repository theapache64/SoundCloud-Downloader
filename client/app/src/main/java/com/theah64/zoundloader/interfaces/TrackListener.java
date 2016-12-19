package com.theah64.zoundloader.interfaces;

import android.support.annotation.NonNull;

import com.theah64.zoundloader.models.Track;

/**
 * Created by theapache64 on 16/12/16.
 */

public interface TrackListener {
    void onNewTrack(Track newTrack);

    void onTrackRemoved(@NonNull Track removedTrack);

    void onTrackUpdated(@NonNull Track updatedTrack);
}
