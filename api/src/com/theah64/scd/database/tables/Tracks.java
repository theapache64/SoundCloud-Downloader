package com.theah64.scd.database.tables;

import com.theah64.scd.database.Connection;
import com.theah64.scd.models.Track;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by theapache64 on 23/12/16.
 */
public class Tracks extends BaseTable<Track> {

    public static final String COLUMN_FILENAME = "filename";
    public static final String COLUMN_IS_DELETED = "is_deleted";
    public static final String COLUMN_ARTWORK_URL = "artwork_url";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ORIGINAL_FORMAT = "original_format";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_SOUNDCLOUD_URL = "soundcloud_url";
    public static final String COLUMN_SOUNDCLOUD_TRACK_ID = "soundcloud_track_id";
    private static Tracks instance;

    private Tracks() {
        super("tracks");
    }

    public static Tracks getInstance() {
        if (instance == null) {
            instance = new Tracks();
        }
        return instance;
    }

    @Override
    public String addv3(Track track) throws InsertFailedException {
        String trackId = null;
        final String query = "INSERT INTO tracks (request_id, soundcloud_url, soundcloud_track_id, title, duration, username,artwork_url,filename,original_format) VALUES (?,?,?,?,?,?,?,?,?);";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, track.getRequestId());
            ps.setString(2, track.getSoundcloudUrl());
            ps.setString(3, track.getSoundcloudTrackId());
            ps.setString(4, track.getTitle());
            ps.setLong(5, track.getDuration());
            ps.setString(6, track.getUsername());
            ps.setString(7, track.getArtworkUrl());
            ps.setString(8, track.getFilename());
            ps.setString(9, track.getOriginalFormat());
            ps.executeUpdate();

            final ResultSet rs = ps.getGeneratedKeys();
            if (rs.first()) {
                trackId = rs.getString(1);
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
        if (trackId == null) {
            throw new InsertFailedException("Failed to insert new track");
        }
        return trackId;

    }

    @Override
    public Track get(String column, String value) {
        Track track = null;
        final String query = String.format("SELECT id,soundcloud_url, soundcloud_track_id, title, duration, username,artwork_url,filename,original_format FROM tracks WHERE %s = ?", column);
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, value);
            final ResultSet rs = ps.executeQuery();

            //Getting data
            if (rs.first()) {

                final String id = rs.getString(COLUMN_ID);
                final String soundcloudUrl = rs.getString(COLUMN_SOUNDCLOUD_URL);
                final String soundCloudTrackId = rs.getString(COLUMN_SOUNDCLOUD_TRACK_ID);
                final String title = rs.getString(COLUMN_TITLE);
                final String username = rs.getString(COLUMN_USERNAME);
                final String artworkUrl = rs.getString(COLUMN_ARTWORK_URL);
                final String filename = rs.getString(COLUMN_FILENAME);
                final String originalFormat = rs.getString(COLUMN_ORIGINAL_FORMAT);
                final long duration = rs.getLong(COLUMN_DURATION);

                track = new Track(id, null, soundcloudUrl, soundCloudTrackId, title, username, artworkUrl, filename, originalFormat, duration);
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
        return track;
    }
}
