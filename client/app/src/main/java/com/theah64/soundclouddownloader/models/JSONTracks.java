package com.theah64.soundclouddownloader.models;

import org.json.JSONArray;

/**
 * Created by theapache64 on 10/12/16.
 */
public class JSONTracks {

    public static final String KEY_TRACKS = "tracks";

    private final String playlistName, username, artworkUrl;
    private final JSONArray jaTracks;
    private final String requestId;

    public JSONTracks(String playlistName, String username, String artworkUrl, JSONArray jaTracks, String requestId) {
        this.playlistName = playlistName;
        this.username = username;
        this.artworkUrl = artworkUrl;
        this.jaTracks = jaTracks;
        this.requestId = requestId;
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

    public String getRequestId() {
        return requestId;
    }
}
