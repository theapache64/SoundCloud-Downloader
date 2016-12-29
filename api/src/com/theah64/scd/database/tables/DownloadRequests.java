package com.theah64.scd.database.tables;

import com.theah64.scd.database.Connection;
import com.theah64.scd.models.DownloadRequest;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by theapache64 on 26/12/16.
 */
public class DownloadRequests extends BaseTable<DownloadRequest> {
    public static final String COLUMN_REQUEST_ID = "request_id";
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

    @Override
    public boolean add(DownloadRequest request) throws InsertFailedException {
        boolean isAdded = false;
        final String query = "INSERT INTO download_requests (track_id, request_id,client_id, download_url) VALUES (?,?,?,?);";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, request.getTrackId());
            ps.setString(2, request.getRequestId());
            ps.setString(3, request.getClientId());
            ps.setString(4, request.getDownloadLink());
            isAdded = ps.executeUpdate() == 1;
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (!isAdded) {
            throw new InsertFailedException("Failed to add new download request");
        }
        return true;
    }
}
