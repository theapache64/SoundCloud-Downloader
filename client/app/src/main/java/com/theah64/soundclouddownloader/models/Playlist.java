package com.theah64.soundclouddownloader.models;

import android.support.annotation.Nullable;

import com.theah64.soundclouddownloader.utils.CommonUtils;
import com.theah64.soundclouddownloader.utils.DownloadUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by theapache64 on 12/12/16.
 */
public class Playlist implements ITSNode, Serializable {

    public static final String KEY = "playlist";
    private static final String SOUND_CLOUD_PLAYLIST_REGEX = "^(?:https:\\/\\/|http:\\/\\/|www\\.|)soundcloud\\.com\\/(?:.+)\\/sets\\/(?:.+)$";
    private final String username;
    private final String title;
    private final String sanitizedTitle;
    private final String url;
    private final String artworkUrl;
    private int totalTracks;
    private final int tracksDownloaded;
    private final String totalDurationInHHMMSS;
    private String id;
    private List<Track> tracks;

    public Playlist(String id, String title, String username, String url, String artworkUrl, int totalTracks, int tracksDownloaded, long totalDuration) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.url = url;
        this.artworkUrl = artworkUrl;
        this.totalTracks = totalTracks;
        this.tracksDownloaded = tracksDownloaded;
        this.totalDurationInHHMMSS = Track.calculateHMS(totalDuration);
        this.sanitizedTitle = getSanitizedTitle(title);
    }

    private static String getSanitizedTitle(String title) {
        return CommonUtils.getSanitizedName(title);
    }

    public static boolean isPlaylist(String soundCloudUrl) {
        return soundCloudUrl.matches(SOUND_CLOUD_PLAYLIST_REGEX);
    }

    public String getSanitizedTitle() {
        return sanitizedTitle;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return username;
    }

    @Override
    public String getSubtitle2() {
        return totalDurationInHHMMSS;
    }

    @Override
    public String getSubtitle3(@Nullable DownloadUtils downloadUtils) {
        return tracksDownloaded + " (saved) /" + totalTracks + " (total)";
    }

    @Override
    public boolean isChecked() {
        return false;
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


    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
    }
}
