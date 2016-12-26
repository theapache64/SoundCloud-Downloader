package com.theah64.scd.database.tables;

import com.theah64.scd.models.DownloadRequest;

/**
 * Created by theapache64 on 26/12/16.
 */
public class DownloadRequests extends BaseTable<DownloadRequest> {
    private static DownloadRequests instance;

    private DownloadRequests() {
        super("download_requests");
    }

    public static DownloadRequests getInstance() {
        if (instance == null) {
            instance = new DownloadRequests();
        }
        return instance;
    }


}
