package com.theah64.scd.models;

/**
 * Created by theapache64 on 8/12/16.
 */
public class Track {

    public static final String KEY_PLAYLIST_NAME = "playlist_name";

    private final String id, requestId, soundcloudUrl, soundcloudTrackId, title, username, downloadUrl, artworkUrl, filename, originalFormat;
    private final long duration;
    private final boolean isDeleted;

    public Track(String id, String requestId, String soundcloudUrl, String soundcloudTrackId, String title, String username, String downloadUrl, String artworkUrl, String filename, String originalFormat, long duration, boolean isDeleted) {
        this.id = id;
        this.requestId = requestId;
        this.soundcloudUrl = soundcloudUrl;
        this.soundcloudTrackId = soundcloudTrackId;
        this.title = title;
        this.username = username;
        this.downloadUrl = downloadUrl;
        this.artworkUrl = artworkUrl;
        this.filename = filename;
        this.originalFormat = originalFormat;
        this.duration = duration;
        this.isDeleted = isDeleted;
    }

    public String getId() {
        return id;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getSoundcloudUrl() {
        return soundcloudUrl;
    }

    public String getSoundcloudTrackId() {
        return soundcloudTrackId;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public String getFilename() {
        return filename;
    }

    public String getOriginalFormat() {
        return originalFormat;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
