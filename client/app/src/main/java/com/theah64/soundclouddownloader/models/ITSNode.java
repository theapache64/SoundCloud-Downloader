package com.theah64.soundclouddownloader.models;

/**
 * Created by theapache64 on 11/12/16.
 */

public interface ITSNode {

    public abstract String getArtworkUrl();

    public abstract String getTitle();

    public abstract String getSubtitle();

    public abstract boolean isDownloadVisible();
}
