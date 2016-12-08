package com.theah64.scd.models;

/**
 * Created by theapache64 on 2/12/16.
 */
public class Request {
    private final String userId, soundCloudUrl;

    public Request(String userId, String soundCloudUrl) {
        this.userId = userId;
        this.soundCloudUrl = soundCloudUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getSoundCloudUrl() {
        return soundCloudUrl;
    }
}
