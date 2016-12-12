package com.theah64.soundclouddownloader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.models.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 12/12/16.
 */

public class Playlists extends BaseTable<Playlist> {

    public static final String COLUMN_SOUNDCLOUD_URL = "soundcloud_url";
    public static final String COLUMN_ARTWORK_URL = "artwork_url";
    private static final String COLUMN_TITLE = "title";
    private static final String TABLE_NAME_PLAYLISTS = "playlists";
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

        final Cursor c = this.getReadableDatabase().query(TABLE_NAME_PLAYLISTS, new String[]{COLUMN_ARTWORK_URL, COLUMN_TITLE}, null, null, null, null, COLUMN_ID + " DESC");
        if (c != null && c.moveToFirst()) {

            playlists = new ArrayList<>(c.getCount());

            do {
                final String artworkUrl = c.getString(c.getColumnIndex(COLUMN_ARTWORK_URL));
                final String title = c.getString(c.getColumnIndex(COLUMN_TITLE));

                playlists.add(new Playlist(null, title, null, artworkUrl));

            } while (c.moveToNext());
        }

        if (c != null) {
            c.close();
        }

        return playlists;

    }
}
