package com.theah64.soundclouddownloader.models;

import com.theah64.soundclouddownloader.database.Tracks;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by theapache64 on 8/12/16.
 */
public class ServerTrack {


    public static final String KEY_PLAYLIST_NAME = "playlist_name";
    private static final String KEY_DOWNLOAD_URL = "download_url";

    private final String primaryRequestId;
    private final String soundcloudUrl;
    private final String soundcloudTrackId;
    private final String title;
    private final String username;
    private final String artworkUrl;
    private final String filename;
    private final String originalFormat;
    private final long duration;

    public ServerTrack(String primaryRequestId, String soundcloudUrl, String soundcloudTrackId, String title, String username, String artworkUrl, String filename, String originalFormat, long duration) {
        this.primaryRequestId = primaryRequestId;
        this.soundcloudUrl = soundcloudUrl;
        this.soundcloudTrackId = soundcloudTrackId;
        this.title = title;
        this.username = username;
        this.artworkUrl = artworkUrl;
        this.filename = filename;
        this.originalFormat = originalFormat;
        this.duration = duration;
    }


    public String getPrimaryRequestId() {
        return primaryRequestId;
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

    public JSONObject toJSONObject() throws JSONException {
        final JSONObject joTrack = new JSONObject();
        joTrack.put("title", title);
        joTrack.put("original_format", originalFormat);
        joTrack.put("filename", filename);
        joTrack.put(Tracks.COLUMN_ARTWORK_URL, artworkUrl);
        joTrack.put(Tracks.COLUMN_DURATION, duration);
        joTrack.put(Tracks.COLUMN_USERNAME, username);
        joTrack.put(Tracks.COLUMN_SOUNDCLOUD_URL, soundcloudUrl);
        return joTrack;
    }

}
