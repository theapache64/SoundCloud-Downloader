package com.theah64.soundclouddownloader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.theah64.soundclouddownloader.models.Playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 12/12/16.
 */

public class Playlists extends BaseTable<Playlist> {

    public static final String COLUMN_SOUNDCLOUD_URL = "soundcloud_url";
    private static final String COLUMN_TITLE = "title";
    private static final String TABLE_NAME_PLAYLISTS = "playlists";
    private static final String COLUMN_AS_PLAYLIST_ID = "playlist_id";
    private static final String COLUMN_AS_TOTAL_TRACKS = "total_tracks";
    private static final String COLUMN_AS_DOWNLOADED_TRACKS = "downloaded_tracks";
    private static Playlists instance;

    private Playlists(Context context) {
        super(context, TABLE_NAME_PLAYLISTS);
    }

    public static Playlists getInstance(Context context) {
        if (instance == null) {
            instance = new Playlists(context);
        }

        return instance;
    }


    //null, playlistName, soundCloudUrl, artworkUrl)
    @Override
    public long add(Playlist playlist) {

        final ContentValues cv = new ContentValues(3);
        cv.put(COLUMN_TITLE, playlist.getTitle());
        cv.put(COLUMN_SOUNDCLOUD_URL, playlist.getSoundCloudUrl());
        cv.put(COLUMN_ARTWORK_URL, playlist.getArtworkUrl());

        final long playlistId = this.getWritableDatabase().insert(TABLE_NAME_PLAYLISTS, null, cv);

        if (playlistId == -1) {
            throw new IllegalArgumentException("Failed to add new playlist");
        }

        return playlistId;
    }

    @Override
    public List<Playlist> getAll() {

        List<Playlist> playlists = null;

        final Cursor c = this.getReadableDatabase().rawQuery("SELECT p.id, p.title, p.artwork_url, COUNT(DISTINCT t.id) AS total_tracks, COUNT(DISTINCT dt.id) AS downloaded_tracks FROM playlists p INNER JOIN tracks t ON t.playlist_id = p.id LEFT JOIN tracks dt ON dt.playlist_id = p.id AND dt.is_downloaded = 1 GROUP BY p.id ORDER BY p.id DESC", null);
        if (c != null && c.moveToFirst()) {

            playlists = new ArrayList<>(c.getCount());

            do {
                final String id = c.getString(c.getColumnIndex(COLUMN_ID));
                final String title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                final String artworkUrl = c.getString(c.getColumnIndex(COLUMN_ARTWORK_URL));
                final int totalTracks = c.getInt(c.getColumnIndex(COLUMN_AS_TOTAL_TRACKS));
                final int tracksDownloaded = c.getInt(c.getColumnIndex(COLUMN_AS_DOWNLOADED_TRACKS));

                playlists.add(new Playlist(id, title, null, artworkUrl, totalTracks, tracksDownloaded));

            } while (c.moveToNext());
        }

        if (c != null) {
            c.close();
        }

        return playlists;

    }
}
