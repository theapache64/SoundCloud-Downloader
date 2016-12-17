package com.theah64.soundclouddownloader.interfaces;

import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.models.Track;

/**
 * Created by theapache64 on 17/12/16.
 */

public interface PlaylistListener {
    void onPlaylistUpdated(String playlistId);
    void onNewPlaylist(Playlist playlist);
}
