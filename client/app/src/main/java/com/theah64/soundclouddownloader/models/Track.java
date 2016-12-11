package com.theah64.soundclouddownloader.models;

import java.io.Serializable;

/**
 * Created by theapache64 on 9/12/16.
 */
public class Track implements Serializable, ITSNode {

    public static final String KEY_TITLE = "title";
    public static final String KEY_DOWNLOAD_URL = "download_url";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_PLAYLIST_NAME = "playlist_name";
    public static final String KEY_IMAGE_URL = "image_url";
    private final String title, fileName, downloadUrl, subPath;
    private boolean isChecked;
    private final String imageUrl;
    private final String downloadStatus;

    public Track(String title, String fileName, String downloadUrl, String subPath, boolean isChecked, String imageUrl, String downloadStatus) {
        this.title = title;
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
        this.subPath = subPath;
        this.isChecked = isChecked;
        this.imageUrl = imageUrl;
        this.downloadStatus = downloadStatus;
    }

    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubtitle() {
        return downloadStatus;
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

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
