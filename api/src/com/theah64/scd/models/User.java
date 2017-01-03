package com.theah64.scd.models;

/**
 * Created by theapache64 on 15/10/16.
 */
public class User {

    private final String id, name, email, imei, apiKey, deviceHash;
    private final boolean isActive;
    private final int totalHits;

    public User(String id, String name, String email, String imei, String apiKey, String deviceHash, int totalHits, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imei = imei;
        this.apiKey = apiKey;
        this.deviceHash = deviceHash;
        this.totalHits = totalHits;
        this.isActive = isActive;
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

    public int getTotalHits() {
        return totalHits;
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
                ", isActive=" + isActive +
                ", totalHits=" + totalHits +
                '}';
    }
}
