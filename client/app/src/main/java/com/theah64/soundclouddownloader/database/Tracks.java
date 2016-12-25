package com.theah64.soundclouddownloader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.theah64.soundclouddownloader.models.Track;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 9/12/16.
 */

public class Tracks extends BaseTable<Track> {

    public static final String COLUMN_SOUNDCLOUD_URL = "soundcloud_url";
    public static final String COLUMN_DOWNLOAD_ID = "download_id";
    public static final String COLUMN_PLAYLIST_ID = "playlist_id";
    public static final String COLUMN_IS_DOWNLOADED = "is_downloaded";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_TITLE = "title";
    private static final String TABLE_NAME_TRACKS = "tracks";
    private static final String X = Tracks.class.getSimpleName();
    private static final String COLUMN_ABS_FILE_PATH = "abs_file_path";
    private static final String COLUMN_DOWNLOAD_URL = "download_url";
    private static Tracks instance;

    private Tracks(Context context) {
        super(context, TABLE_NAME_TRACKS);
    }

    public static Tracks getInstance(final Context context) {
        if (instance == null) {
            instance = new Tracks(context.getApplicationContext());
        }
        return instance;
    }


    //title, url, download_id, artwork_url
    @Override
    public long add(final Track track, @Nullable Handler handler) {

        Log.d(X, "Adding new track to database : " + track.toString());

        final ContentValues cv = new ContentValues(4);
        cv.put(COLUMN_TITLE, track.getTitle());
        cv.put(COLUMN_SOUNDCLOUD_URL, track.getSoundCloudUrl());
        cv.put(COLUMN_DOWNLOAD_ID, track.getDownloadId());
        cv.put(COLUMN_ARTWORK_URL, track.getArtworkUrl());
        cv.put(COLUMN_DURATION, track.getDuration());
        cv.put(COLUMN_USERNAME, track.getUsername());
        cv.put(COLUMN_PLAYLIST_ID, track.getPlaylistId());
        cv.put(COLUMN_ABS_FILE_PATH, track.getFile().getAbsolutePath());
        cv.put(COLUMN_IS_DOWNLOADED, track.isDownloaded());
        cv.put(COLUMN_DOWNLOAD_URL, track.getDownloadUrl());


        final long trackId = this.getWritableDatabase().insert(TABLE_NAME_TRACKS, null, cv);

        if (trackId == -1) {
            throw new IllegalArgumentException("Failed to insert new track");
        }

        //Setting id to track
        track.setId(String.valueOf(trackId));

        if (handler != null) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    onNewTrack(track);
                }
            });
        } else {
            onNewTrack(track);
        }

        return trackId;
    }

    private void onNewTrack(final Track track) {

        if (getApp().getMainTrackListener() != null) {
            getApp().getMainTrackListener().onNewTrack(track);
        }

        if (track.getPlaylistId() != null && getApp().getPlaylistListener() != null) {
            getApp().getPlaylistListener().onPlaylistUpdated(track.getPlaylistId());
        }

        if (getApp().getPlaylistTrackListener() != null) {
            getApp().getPlaylistTrackListener().onNewTrack(track);
        }

    }


    public List<Track> getAll(@Nullable String playlistId) {
        List<Track> trackList = null;

        final String query = String.format("SELECT id,title, playlist_id,download_url, download_id,artwork_url, abs_file_path,soundcloud_url,is_downloaded, duration,username FROM tracks %s ORDER BY id DESC;",
                playlistId != null ? "WHERE playlist_id = ?" : "");

        Log.d(X, "Query : " + query);

        final Cursor c = this.getReadableDatabase().rawQuery(query, playlistId != null ? new String[]{playlistId} : null);
        if (c != null && c.moveToFirst()) {

            trackList = new ArrayList<>(c.getCount());

            do {
                final String id = c.getString(c.getColumnIndex(COLUMN_ID));
                final String playlistId2 = c.getString(c.getColumnIndex(COLUMN_PLAYLIST_ID));
                final String downloadId = c.getString(c.getColumnIndex(COLUMN_DOWNLOAD_ID));
                final String artworkUrl = c.getString(c.getColumnIndex(COLUMN_ARTWORK_URL));
                final String title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                final String absoluteFilePath = c.getString(c.getColumnIndex(COLUMN_ABS_FILE_PATH));
                final String soundCloudUrl = c.getString(c.getColumnIndex(COLUMN_SOUNDCLOUD_URL));
                final boolean isDownloaded = c.getString(c.getColumnIndex(COLUMN_IS_DOWNLOADED)).equals(TRUE);
                final long duration = c.getLong(c.getColumnIndex(COLUMN_DURATION));
                final String username = c.getString(c.getColumnIndex(COLUMN_USERNAME));
                final String downloadUrl = c.getString(c.getColumnIndex(COLUMN_DOWNLOAD_URL));

                Log.d(X, playlistId2 + "->" + title);

                trackList.add(new Track(id, title, username, downloadUrl, artworkUrl, downloadId, soundCloudUrl, playlistId2, false, isDownloaded, absoluteFilePath != null ? new File(absoluteFilePath) : null, duration));
            } while (c.moveToNext());
        }


        if (c != null) {
            c.close();
        }

        return trackList;
    }

    @Override
    public Track get(String column, String value) {

        Track track = null;

        final Cursor c = this.getReadableDatabase().query(TABLE_NAME_TRACKS, new String[]{COLUMN_ID, COLUMN_DOWNLOAD_URL, COLUMN_DOWNLOAD_ID, COLUMN_USERNAME, COLUMN_DURATION, COLUMN_ARTWORK_URL, COLUMN_TITLE, COLUMN_DOWNLOAD_ID, COLUMN_SOUNDCLOUD_URL, COLUMN_ABS_FILE_PATH, COLUMN_IS_DOWNLOADED, COLUMN_PLAYLIST_ID}, column + " = ?", new String[]{value}, null, null, null);

        if (c != null) {
            if (c.moveToFirst()) {
                //track exists
                final String id = c.getString(c.getColumnIndex(COLUMN_ID));
                final String downloadId = c.getString(c.getColumnIndex(COLUMN_DOWNLOAD_ID));
                final String artworkUrl = c.getString(c.getColumnIndex(COLUMN_ARTWORK_URL));
                final String title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                final String absoluteFilePath = c.getString(c.getColumnIndex(COLUMN_ABS_FILE_PATH));
                final String soundCloudUrl = c.getString(c.getColumnIndex(COLUMN_SOUNDCLOUD_URL));
                final boolean isDownloaded = c.getString(c.getColumnIndex(COLUMN_IS_DOWNLOADED)).equals(TRUE);
                final String playlistId = c.getString(c.getColumnIndex(COLUMN_PLAYLIST_ID));

                final long duration = c.getLong(c.getColumnIndex(COLUMN_DURATION));
                final String username = c.getString(c.getColumnIndex(COLUMN_USERNAME));

                final String downloadUrl = c.getString(c.getColumnIndex(COLUMN_DOWNLOAD_URL));

                track = new Track( id, title, username, downloadUrl, artworkUrl, downloadId, soundCloudUrl, playlistId, false, isDownloaded, absoluteFilePath != null ? new File(absoluteFilePath) : null, duration);
            }
            c.close();
        }

        return track;
    }

    public boolean update(String whereColumn, String whereColumnValue, String columnToUpdate, String valueToUpdate, @Nullable Handler handler) {

        final boolean isUpdated = super.update(whereColumn, whereColumnValue, columnToUpdate, valueToUpdate);

        if (!isUpdated) {
            FirebaseCrash.log(String.format("whereColumn:%s whereColumnValue:%s columnToUpdate:%s valueToUpdate:%s", whereColumn, whereColumnValue, columnToUpdate, valueToUpdate));
        }

        final Track track = get(whereColumn, whereColumnValue);
        if (track != null) {

            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onTrackUpdated(track);
                    }
                });
            } else {
                onTrackUpdated(track);
            }
        } else {
            FirebaseCrash.log("Couldn't find track with column " + whereColumn + ", value " + whereColumnValue);
        }

        return true;
    }

    private void onTrackUpdated(Track track) {

        //Track updated, so alerting the listeners
        if (getApp().getMainTrackListener() != null) {
            getApp().getMainTrackListener().onTrackUpdated(track);
        }

        if (track.getPlaylistId() != null && getApp().getPlaylistListener() != null) {

            //The track is from a playlist and we've playlist track listener
            getApp().getPlaylistListener().onPlaylistUpdated(track.getPlaylistId());

        }

        if (getApp().getPlaylistTrackListener() != null) {
            getApp().getPlaylistTrackListener().onTrackUpdated(track);
        }
    }

    @Override
    public boolean update(final Track track, @Nullable Handler handler) {
        final ContentValues cv = new ContentValues(1);
        cv.put(COLUMN_DOWNLOAD_ID, track.getDownloadId());

        final boolean isUpdated = this.getWritableDatabase().update(TABLE_NAME_TRACKS, cv, COLUMN_ID + " = ?", new String[]{track.getId()}) > 0;

        if (!isUpdated) {
            FirebaseCrash.log("Failed to update the track");
            return false;
        }

        if (handler != null) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    onTrackUpdated(track);
                }
            });
        } else {
            onTrackUpdated(track);
        }

        return true;
    }

    public boolean delete(String whereColumn, String whereColumnValue, @Nullable Handler handler) {

        final Track track = get(whereColumn, whereColumnValue);

        if (track == null) {
            FirebaseCrash.log("failed to find track with " + whereColumn + '=' + whereColumnValue);
            return false;
        }

        if (super.delete(whereColumn, whereColumnValue)) {

            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onTrackRemoved(track);
                    }
                });
            } else {
                onTrackRemoved(track);
            }

        } else {
            throw new IllegalArgumentException("Failed to delete track with " + whereColumn + '=' + whereColumnValue + " : " + track);
        }

        return true;
    }

    private void onTrackRemoved(final Track track) {

        //Track updated, so alerting the listeners
        if (getApp().getMainTrackListener() != null) {
            getApp().getMainTrackListener().onTrackRemoved(track);
        }

        if (getApp().getPlaylistTrackListener() != null) {
            getApp().getPlaylistTrackListener().onTrackRemoved(track);
        }

        if (track.getPlaylistId() != null && getApp().getPlaylistListener() != null) {

            //The track is from a playlist and we've playlist track listener
            getApp().getPlaylistListener().onPlaylistUpdated(track.getPlaylistId());

        }

    }
}
