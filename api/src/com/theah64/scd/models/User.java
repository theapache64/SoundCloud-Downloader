package com.theah64.scd.models;

/**
 * Created by theapache64 on 15/10/16.
 */
public class User {

    private final String id, name, email, imei, apiKey, deviceHash, lastHit;
    private final boolean isActive;
    private final long totalRequests, totalDownloads, totalTracks;

    public User(String id, String name, String email, String imei, String apiKey, String deviceHash, String lastHit, boolean isActive, long totalRequests, long totalDownloads, long totalTracks) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imei = imei;
        this.apiKey = apiKey;
        this.deviceHash = deviceHash;
        this.lastHit = lastHit;
        this.totalRequests = totalRequests;
        this.isActive = isActive;
        this.totalDownloads = totalDownloads;
        this.totalTracks = totalTracks;
    }

    public String getLastHit() {
        return lastHit;
    }

    public long getTotalDownloads() {
        return totalDownloads;
    }

    public long getTotalTracks() {
        return totalTracks;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getDeviceHash() {
        return deviceHash;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getIMEI() {
        return imei;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", imei='" + imei + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", deviceHash='" + deviceHash + '\'' +
                ", lastHit='" + lastHit + '\'' +
                ", isActive=" + isActive +
                ", totalRequests=" + totalRequests +
                ", totalDownloads=" + totalDownloads +
                ", totalTracks=" + totalTracks +
                '}';
    }
}
