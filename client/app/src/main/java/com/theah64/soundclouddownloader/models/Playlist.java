package com.theah64.soundclouddownloader.models;

import java.io.Serializable;

/**
 * Created by theapache64 on 12/12/16.
 */
public class Playlist implements ITSNode, Serializable {

    public static final String KEY = "playlist";

    private final String id, title, url, artworkUrl;
    private final int totalTracks, tracksDownloaded;


    public Playlist(String id, String title, String url, String artworkUrl, int totalTracks, int tracksDownloaded) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.artworkUrl = artworkUrl;
        this.totalTracks = totalTracks;
        this.tracksDownloaded = tracksDownloaded;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getArtworkUrl() {
        return artworkUrl;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getSubtitle() {
        return tracksDownloaded + " (saved) /" + totalTracks + " (total)";
    }

    public String getSoundCloudUrl() {
        return url;
    }

    public int getTotalTracks() {
        return totalTracks;
    }
}
