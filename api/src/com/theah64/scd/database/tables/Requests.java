package com.theah64.scd.database.tables;


import com.theah64.scd.database.Connection;
import com.theah64.scd.models.Request;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public String addv3(Request request) throws InsertFailedException {
        String requestId = null;

        final String query = "INSERT INTO requests (user_id, soundcloud_url) VALUES (?,?);";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, request.getUserId());
            ps.setString(2, request.getSoundCloudUrl());
            ps.executeUpdate();

            final ResultSet rs = ps.getGeneratedKeys();

            if (rs.first()) {
                requestId = rs.getString(1);
            }

            rs.close();
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

        if (requestId == null) {
            throw new InsertFailedException("Failed to add request");
        }

        System.out.println("Created new request: " + requestId);

        return requestId;
    }
}
