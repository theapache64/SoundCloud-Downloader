package com.theah64.soundclouddownloader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.theah64.soundclouddownloader.models.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 9/12/16.
 */

public class Tracks extends BaseTable<Track> {

    private static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SOUNDCLOUD_URL = "soundcloud_url";
    private static final String COLUMN_DOWNLOAD_ID = "download_id";
    private static final String TABLE_NAME_TRACKS = "tracks";
    private static final String COLUMN_PLAYLIST_ID = "playlist_id";
    private static Tracks instance;

    Tracks(Context context) {
        super(context, TABLE_NAME_TRACKS);
    }

    public static Tracks getInstance(final Context context) {
        if (instance == null) {
            instance = new Tracks(context);
        }
        return instance;
    }


    //title, url, download_id, artwork_url
    @Override
    public long add(Track track) {

        final ContentValues cv = new ContentValues(4);
        cv.put(COLUMN_TITLE, track.getTitle());
        cv.put(COLUMN_SOUNDCLOUD_URL, track.getSoundCloudUrl());
        cv.put(COLUMN_DOWNLOAD_ID, track.getDownloadId());
        cv.put(COLUMN_ARTWORK_URL, track.getArtWorkUrl());
        cv.put(COLUMN_PLAYLIST_ID, track.getPlaylistId());

        final long trackId = this.getWritableDatabase().insert(TABLE_NAME_TRACKS, null, cv);

        if (trackId == -1) {
            throw new IllegalArgumentException("Failed to insert new track");
        }

        return trackId;
    }

    //artwork_url, title, download_id
    @Override
    public List<Track> getAll() {
        List<Track> trackList = null;

        final Cursor c = this.getReadableDatabase().query(TABLE_NAME_TRACKS, new String[]{COLUMN_ARTWORK_URL, COLUMN_TITLE, COLUMN_DOWNLOAD_ID}, null, null, null, null, COLUMN_ID + " DESC");
        if (c != null && c.moveToFirst()) {

            trackList = new ArrayList<>(c.getCount());

            do {
                final String artworkUrl = c.getString(c.getColumnIndex(COLUMN_ARTWORK_URL));
                final String title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                final String downloadId = c.getString(c.getColumnIndex(COLUMN_DOWNLOAD_ID));

                trackList.add(new Track(null, title, null, null, null, artworkUrl, downloadId, null, null, false));
            } while (c.moveToNext());
        }


        if (c != null) {
            c.close();
        }

        return trackList;
    }
}
