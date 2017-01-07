package com.theah64.scd.models;

/**
 * Created by theapache64 on 29/12/16.
 */
public class SCClient {
    private final String id, name, clientId;
    private final int totalHits;
    private final boolean isActive;

    public SCClient(String id, String name, String clientId, int totalHits, boolean isActive) {
        this.id = id;
        this.name = name;
        this.clientId = clientId;
        this.totalHits = totalHits;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getClientId() {
        return clientId;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "SCClient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", clientId='" + clientId + '\'' +
                ", totalHits=" + totalHits +
                '}';
    }
}
