package com.theah64.zoundloader.interfaces;

import com.theah64.zoundloader.models.Playlist;

/**
 * Created by theapache64 on 17/12/16.
 */

public interface PlaylistListener {
    void onPlaylistUpdated(String playlistId);
    void onNewPlaylist(Playlist playlist);
}
