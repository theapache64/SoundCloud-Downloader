package com.theah64.scd.models;

import org.json.JSONArray;

/**
 * Created by theapache64 on 10/12/16.
 */
public class JSONTracks {
    private final String playlistName;
    private final JSONArray jaTracks;

    public JSONTracks(String playlistName, JSONArray jaTracks) {
        this.playlistName = playlistName;
        this.jaTracks = jaTracks;
    }


    public String getPlaylistName() {
        return playlistName;
    }

    public JSONArray getJSONArrayTracks() {
        return jaTracks;
    }
}
