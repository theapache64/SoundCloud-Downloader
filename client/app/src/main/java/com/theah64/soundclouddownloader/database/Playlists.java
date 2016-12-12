package com.theah64.soundclouddownloader.database;

import android.content.Context;

import com.theah64.soundclouddownloader.models.Playlist;

/**
 * Created by theapache64 on 12/12/16.
 */

public class Playlists extends BaseTable<Playlist> {

    private static Playlists instance;

    private Playlists(Context context) {
        super(context, "playlists");
    }

    public static Playlists getInstance(Context context) {
        if (instance == null) {
            instance = new Playlists(context);
        }

        return instance;
    }
    
}
