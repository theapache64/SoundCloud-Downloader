package com.theah64.soundclouddownloader.models;

import android.support.annotation.Nullable;

import com.theah64.soundclouddownloader.utils.DownloadUtils;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by theapache64 on 9/12/16.
 */
public class Track implements Serializable, ITSNode {

    public static final String KEY_TITLE = "title";
    public static final String KEY_DOWNLOAD_URL = "download_url";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_PLAYLIST_NAME = "playlist_name";
    private final String title, username;
    private final String downloadUrl;
    private final String artWorkUrl;
    private final String soundCloudUrl;
    private final String playlistId;
    private final boolean isDownloaded;
    private final long duration;
    private final String durationInHHMMSS;
    private String id;
    private String downloadId;
    private boolean isChecked;
    private final File file;

    public Track(String id, String title, String username, String downloadUrl, String artWorkUrl, String downloadId, String soundCloudUrl, String playlistId, boolean isChecked, boolean isDownloaded, File file, long duration) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.downloadUrl = downloadUrl;
        this.artWorkUrl = artWorkUrl;
        this.downloadId = downloadId;
        this.soundCloudUrl = soundCloudUrl;
        this.playlistId = playlistId;
        this.isChecked = isChecked;
        this.isDownloaded = isDownloaded;
        this.file = file;
        this.duration = duration;
        this.durationInHHMMSS = calculateHMS(duration);

        if (file == null) {
            throw new IllegalArgumentException("File can't be null");
        }
    }

    static String calculateHMS(long duration) {

        final boolean hasMoreThanHours = TimeUnit.HOURS.toMillis(1) <= duration;

        String hms;

        if (hasMoreThanHours) {
            hms = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                    TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        } else {
            hms = String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                    TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }

        return hms;
    }

    public static int getTrackPosition(List<Track> trackList, final String trackId) {
        for (int i = 0; i < trackList.size(); i++) {
            final Track t = trackList.get(i);
            if (t.getId().equals(trackId)) {
                return i;
            }
        }
        throw new IllegalArgumentException("failed to find track id" + trackId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public String getArtworkUrl() {
        return artWorkUrl;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getSubtitle1() {
        return this.username;
    }

    @Override
    public String getSubtitle2() {
        return this.durationInHHMMSS;
    }

    @Override
    public String getSubtitle3(@Nullable DownloadUtils downloadUtils) {
        if (downloadUtils != null) {
            return downloadUtils.getVerbalStatus(this);
        } else {
            return DownloadUtils.getSubtitle3(this);
        }
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public String getUsername() {
        return username;
    }

    public long getDuration() {
        return duration;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", username='" + username + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", artWorkUrl='" + artWorkUrl + '\'' +
                ", downloadId='" + downloadId + '\'' +
                ", soundCloudUrl='" + soundCloudUrl + '\'' +
                ", playlistId='" + playlistId + '\'' +
                ", isChecked=" + isChecked +
                ", isDownloaded=" + isDownloaded +
                ", file=" + file +
                ", duration=" + duration +
                ", durationInHHMMSS='" + durationInHHMMSS + '\'' +
                '}';
    }

    public boolean isExistInStorage() {
        return file != null && file.exists();
    }

    public boolean isMP3() {
        return file.getAbsolutePath().endsWith(".mp3");
    }
}
