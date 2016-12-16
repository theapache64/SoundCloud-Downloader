package com.theah64.soundclouddownloader.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by theapache64 on 12/12/16.
 */
public class Playlist implements ITSNode, Serializable {

    public static final String KEY = "playlist";

    private final String id, title, url, artworkUrl;
    private final int totalTracks, tracksDownloaded;
    private List<Track> tracks;


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
    public String getSubtitle1() {
        //TODO
        return "The Chainsmokers";
    }

    @Override
    public String getSubtitle2() {
        //TODO
        return "59:62";
    }

    @Override
    public String getSubtitle3() {
        return tracksDownloaded + " (saved) /" + totalTracks + " (total)";
    }


    public String getSoundCloudUrl() {
        return url;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public boolean isDownloaded() {
        return tracksDownloaded == totalTracks;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
