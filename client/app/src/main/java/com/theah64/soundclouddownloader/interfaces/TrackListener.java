package com.theah64.soundclouddownloader.interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.theah64.soundclouddownloader.models.Track;

/**
 * Created by theapache64 on 16/12/16.
 */

public interface TrackListener {
    void onNewTrack(Track newTrack);

    void onTrackRemoved(@NonNull Track removedTrack);

    void onTrackUpdated(@NonNull Track updatedTrack);
}
