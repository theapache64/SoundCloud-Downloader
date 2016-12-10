package com.theah64.soundclouddownloader.models;

import java.io.Serializable;

/**
 * Created by theapache64 on 9/12/16.
 */
public class Track implements Serializable {

    public static final String KEY_TITLE = "title";
    public static final String KEY_DOWNLOAD_URL = "download_url";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_PLAYLIST_NAME = "playlist_name";
    private final String title, fileName, downloadUrl, subPath;
    private final boolean isChecked;

    public Track(String title, String fileName, String downloadUrl, String subPath, boolean isChecked) {
        this.title = title;
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
        this.subPath = subPath;
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getSubPath() {
        return subPath;
    }

    @Override
    public String toString() {
        return "Track{" +
                "title='" + title + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", subPath='" + subPath + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
