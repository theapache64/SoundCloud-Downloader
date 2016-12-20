package com.theah64.musicdog.interfaces;

/**
 * Created by theapache64 on 17/12/16.
 */

public interface MainActivityCallback {
    void onRemovePlaylistTrack(String playlistId);

    void onRemovePlaylist(String playlistId);

    void setTabTracksCount(int count);

    void setTabPlaylistsCount(int count);

    void hideFabAdd();

    void showFabAdd();

    boolean isFabAddShown();
}
