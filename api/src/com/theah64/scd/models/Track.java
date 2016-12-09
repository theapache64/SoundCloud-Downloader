package com.theah64.scd.models;

/**
 * Created by theapache64 on 8/12/16.
 */
public class Track {

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ORIGINAL_FORMAT = "original_format";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_DOWNLOAD_URL = "download_url";
    private final String id;
    private final String name;
    private String downloadUrl;

    public Track(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public String getId() {
        return id;
    }


    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getName() {
        return name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
