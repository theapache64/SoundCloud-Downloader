package com.theah64.soundclouddownloader.models;

import java.io.File;
import java.io.Serializable;

/**
 * Created by theapache64 on 9/12/16.
 */
public class Track implements Serializable, ITSNode {

    public static final String KEY_TITLE = "title";
    public static final String KEY_DOWNLOAD_URL = "download_url";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_PLAYLIST_NAME = "playlist_name";
    private String id;
    private final String title;
    private final String fileName;
    private final String downloadUrl;
    private final String subPath;
    private final String artWorkUrl;
    private final String downloadId;
    private final String soundCloudUrl;
    private final String playlistId;
    private boolean isChecked;
    private final boolean isDownloaded;
    private final File file;

    public Track(String id, String title, String fileName, String downloadUrl, String subPath, String artWorkUrl, String downloadId, String soundCloudUrl, String playlistId, boolean isChecked, boolean isDownloaded, File file) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
        this.subPath = subPath;
        this.artWorkUrl = artWorkUrl;
        this.downloadId = downloadId;
        this.soundCloudUrl = soundCloudUrl;
        this.playlistId = playlistId;
        this.isChecked = isChecked;
        this.isDownloaded = isDownloaded;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getSoundCloudUrl() {
        return soundCloudUrl;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public String getArtWorkUrl() {
        return artWorkUrl;
    }

    public boolean isChecked() {
        return isChecked;
    }


    @Override
    public String getArtworkUrl() {
        return artWorkUrl;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getSubtitle() {

        if (isDownloaded && file != null && file.exists()) {
            return "(Saved)";
        } else if (isDownloaded && file != null && !file.exists()) {
            return "(Saved but moved/deleted)";
        } else {
            return null;
        }

    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getSubPath() {
        return subPath;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }


    @Override
    public String toString() {
        return "Track{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", subPath='" + subPath + '\'' +
                ", artWorkUrl='" + artWorkUrl + '\'' +
                ", downloadId='" + downloadId + '\'' +
                ", soundCloudUrl='" + soundCloudUrl + '\'' +
                ", playlistId='" + playlistId + '\'' +
                ", isChecked=" + isChecked +
                ", isDownloaded=" + isDownloaded +
                ", file=" + file +
                '}';
    }

    public File getFile() {
        return file;
    }
}
