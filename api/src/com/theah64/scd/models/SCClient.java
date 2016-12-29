package com.theah64.scd.models;

/**
 * Created by theapache64 on 29/12/16.
 */
public class SCClient {
    private final String id, name, clientId;
    private final int totalHits;

    public SCClient(String id, String name, String clientId, int totalHits) {
        this.id = id;
        this.name = name;
        this.clientId = clientId;
        this.totalHits = totalHits;
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
}
