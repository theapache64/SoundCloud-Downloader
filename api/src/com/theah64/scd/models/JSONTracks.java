package com.theah64.scd.models;

import org.json.JSONArray;

/**
 * Created by theapache64 on 10/12/16.
 */
public class JSONTracks {

    public static final String KEY_TRACKS = "tracks";

    private final String playlistName, username, artworkUrl;
    private final JSONArray jaTracks;

    public JSONTracks(String playlistName, String username, String artworkUrl, JSONArray jaTracks) {
        this.playlistName = playlistName;
        this.username = username;
        this.artworkUrl = artworkUrl;
        this.jaTracks = jaTracks;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public JSONArray getJSONArrayTracks() {
        return jaTracks;
    }

    public String getUsername() {
        return username;
    }
}
