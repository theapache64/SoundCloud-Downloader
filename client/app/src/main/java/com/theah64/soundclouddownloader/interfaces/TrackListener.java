package com.theah64.soundclouddownloader.interfaces;

import com.theah64.soundclouddownloader.models.Track;

/**
 * Created by theapache64 on 16/12/16.
 */

public interface TrackListener {
    void onNewTrack(Track newTrack);

    void onTrackRemoved(Track removedTrack);

    void onTrackUpdated(Track updatedTrack);
}
