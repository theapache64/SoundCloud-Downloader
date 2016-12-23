package com.theah64.scd.database.tables;

import com.theah64.scd.models.Track;

/**
 * Created by theapache64 on 23/12/16.
 */
public class Tracks extends BaseTable<Track> {

    public static final String COLUMN_FILENAME = "filename";
    public static final String COLUMN_IS_DELETED = "is_deleted";
    public static final String COLUMN_ARTWORK_URL = "artwork_url";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ORIGINAL_FORMAT = "original_format";
    public static final String COLUMN_DOWNLOAD_URL = "download_url";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_SOUNDCLOUD_URL = "soundcloud_url";
    private static Tracks instance;

    private Tracks(String tableName) {
        super(tableName);
    }

    public static Tracks getInstance() {
        return instance;
    }

}
