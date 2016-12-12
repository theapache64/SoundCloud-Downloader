package com.theah64.soundclouddownloader.models;

/**
 * Created by theapache64 on 11/12/16.
 */

public class ITSNode {
    private final String imageUrl, title, subtitle;

    public ITSNode(String imageUrl, String title, String subtitle) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
