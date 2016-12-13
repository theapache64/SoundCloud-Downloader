package com.theah64.soundclouddownloader.models;

/**
 * Created by theapache64 on 12/12/16.
 */
public class Playlist implements ITSNode {

    public static final String KEY = "playlist";

    private final String id, title, url, artworkUrl;

    public Playlist(String id, String title, String url, String artworkUrl) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.artworkUrl = artworkUrl;
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
        return null;
    }

    @Override
    public boolean isDownloadVisible() {
        return false;
    }

    public String getSoundCloudUrl() {
        return url;
    }
}
