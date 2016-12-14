package com.theah64.scd.database.tables;


import com.theah64.scd.database.Connection;
import com.theah64.scd.models.Request;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by theapache64 on 2/12/16.
 */
public class Requests extends BaseTable<Request> {

    private static final Requests instance = new Requests();
    public static final String COLUMN_SOUND_CLOUD_URL = "soundcloud_url";

    private Requests() {
        super("requests");
    }

    public static Requests getInstance() {
        return instance;
    }

    @Override
    public boolean add(Request request) throws InsertFailedException {
        boolean isAdded = false;

        final String query = "INSERT INTO requests (user_id, soundcloud_url) VALUES (?,?);";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, request.getUserId());
            ps.setString(2, request.getSoundCloudUrl());

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
            throw new InsertFailedException("Failed to add request");
        }

        return true;
    }
}
