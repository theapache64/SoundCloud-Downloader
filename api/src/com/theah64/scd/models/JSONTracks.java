package com.theah64.scd.models;

import org.json.JSONArray;

/**
 * Created by theapache64 on 10/12/16.
 */
public class JSONTracks {

    public static final String KEY_TRACKS = "tracks";

    private final String playlistName, artworkUrl;
    private final JSONArray jaTracks;

    public JSONTracks(String playlistName, String artworkUrl, JSONArray jaTracks) {
        this.playlistName = playlistName;
        this.artworkUrl = artworkUrl;
        this.jaTracks = jaTracks;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public JSONArray getJSONArrayTracks() {
        return jaTracks;
    }
}
