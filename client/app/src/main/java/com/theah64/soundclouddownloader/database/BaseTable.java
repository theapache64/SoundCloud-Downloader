package com.theah64.soundclouddownloader.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.theah64.soundclouddownloader.utils.FileUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shifar on 11/5/16.
 */
public class BaseTable<T> extends SQLiteOpenHelper {

    public static final String FALSE = "0";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ARTWORK_URL = "artwork_url";
    private static final String DATABASE_NAME = "scd.db";
    static final String COLUMN_CREATED_AT = "created_at";

    private static final int DATABASE_VERSION = 1;
    private static final String X = BaseTable.class.getSimpleName();
    private static final String FATAL_ERROR_UNDEFINED_METHOD = "Undefined method";
    public static final String TRUE = "1";

    private final Context context;
    private final String tableName;

    BaseTable(final Context context, String tableName) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.tableName = tableName;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            //Creating database structure
            final String[] createStatements = FileUtils.readTextualAsset(context, "database.sql").split(";");

            for (final String stmt : createStatements) {
                if (!stmt.trim().isEmpty()) {
                    Log.d(X, "Statement : " + stmt);
                    db.execSQL(stmt + ";");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tracks");
        onCreate(db);
    }

    public void cleanDb() {
        onUpgrade(this.getWritableDatabase(), 0, 0);
    }

    public T get(final String column, final String value) {
        throw new IllegalArgumentException(FATAL_ERROR_UNDEFINED_METHOD);
    }

    public boolean add(@Nullable final String userId, final JSONObject jsonObject) {
        throw new IllegalArgumentException(FATAL_ERROR_UNDEFINED_METHOD);
    }


    public long add(T newInstance) {
        throw new IllegalArgumentException(FATAL_ERROR_UNDEFINED_METHOD);
    }

    public void addv2(T newInstance) throws RuntimeException {
        throw new IllegalArgumentException(FATAL_ERROR_UNDEFINED_METHOD);
    }

    public String get(final String whereColumn, final String whereColumnValue, final String columnToReturn) {

        String valueToReturn = null;

        final Cursor cur = this.getWritableDatabase().query(getTableName(), new String[]{columnToReturn}, whereColumn + " = ? ", new String[]{whereColumnValue}, null, null, null, "1");

        if (cur.moveToFirst()) {
            valueToReturn = cur.getString(cur.getColumnIndex(columnToReturn));
        }

        cur.close();

        return valueToReturn;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    public boolean update(String whereColumn, String whereColumnValue, String columnToUpdate, String valueToUpdate) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues cv = new ContentValues(1);
        cv.put(columnToUpdate, valueToUpdate);
        return db.update(tableName, cv, whereColumn + " = ? ", new String[]{whereColumnValue}) > 0;
    }


    public boolean update(T t) {
        throw new IllegalArgumentException(FATAL_ERROR_UNDEFINED_METHOD);
    }

    public T get(final String column1, final String value1, final String column2, final String value2) {
        throw new IllegalArgumentException(FATAL_ERROR_UNDEFINED_METHOD);
    }

    public List<T> getAll() {
        throw new IllegalArgumentException(FATAL_ERROR_UNDEFINED_METHOD);
    }

    public void deleteAll() {
        this.getWritableDatabase().delete(getTableName(), null, null);
    }

    public final boolean delete(final String whereColumn, final String whereColumnValue) {
        return this.getWritableDatabase().delete(getTableName(), whereColumn + " = ?", new String[]{whereColumnValue}) == 1;
    }


    public Context getContext() {
        return context;
    }

    private String getTableName() {
        return tableName;
    }

    public final int getCount() {
        int count = 0;

        final Cursor c = this.getReadableDatabase().rawQuery("SELECT COUNT(id) FROM " + getTableName(), null);

        if (c != null && c.moveToFirst()) {
            count = c.getInt(0);
        }

        if (c != null) {
            c.close();
        }

        return count;
    }
}