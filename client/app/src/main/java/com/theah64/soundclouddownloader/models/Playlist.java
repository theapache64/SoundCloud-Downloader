package com.theah64.soundclouddownloader.models;

/**
 * Created by theapache64 on 12/12/16.
 */
public class Playlist {
    private final String id, title, url, artworkUrl;

    public Playlist(String id, String title, String url, String artworkUrl) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.artworkUrl = artworkUrl;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSoundCloudUrl() {
        return url;
    }
}
