package com.theah64.scd.models;

/**
 * Created by theapache64 on 26/12/16.
 */
public class DownloadRequest {
    private final String id, trackId, userId, downloadLink;

    public DownloadRequest(String id, String trackId, String userId, String downloadLink) {
        this.id = id;
        this.trackId = trackId;
        this.userId = userId;
        this.downloadLink = downloadLink;
    }

    public String getId() {
        return id;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getUserId() {
        return userId;
    }

    public String getDownloadLink() {
        return downloadLink;
    }
}
