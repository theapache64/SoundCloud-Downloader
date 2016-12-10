package com.theah64.soundclouddownloader.database;

import android.content.Context;

import com.theah64.soundclouddownloader.models.Track;

/**
 * Created by theapache64 on 9/12/16.
 */

public class Tracks extends BaseTable<Track> {

    private static Tracks instance;

    Tracks(Context context) {
        super(context, "tracks");
    }

    public static Tracks getInstance(final Context context) {
        if (instance == null) {
            instance = new Tracks(context);
        }
        return instance;
    }
}
