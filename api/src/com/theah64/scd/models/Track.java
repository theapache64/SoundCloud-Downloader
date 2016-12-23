package com.theah64.scd.models;

import com.theah64.scd.database.tables.Tracks;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by theapache64 on 8/12/16.
 */
public class Track {

    public static final String KEY_PLAYLIST_NAME = "playlist_name";

    private final String id, requestId, soundcloudUrl, soundcloudTrackId, title, username, downloadUrl, artworkUrl, filename, originalFormat;
    private final long duration;

    public Track(String id, String requestId, String soundcloudUrl, String soundcloudTrackId, String title, String username, String downloadUrl, String artworkUrl, String filename, String originalFormat, long duration) {
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

    public JSONObject toJSONObject() throws JSONException {
        final JSONObject joTrack = new JSONObject();
        joTrack.put(Tracks.COLUMN_TITLE, title);
        joTrack.put(Tracks.COLUMN_ORIGINAL_FORMAT, originalFormat);
        joTrack.put(Tracks.COLUMN_FILENAME, filename);
        joTrack.put(Tracks.COLUMN_ARTWORK_URL, artworkUrl);
        joTrack.put(Tracks.COLUMN_DOWNLOAD_URL, downloadUrl);
        joTrack.put(Tracks.COLUMN_DURATION, duration);
        joTrack.put(Tracks.COLUMN_USERNAME, username);
        joTrack.put(Tracks.COLUMN_SOUNDCLOUD_URL, soundcloudUrl);
        return joTrack;
    }
}
