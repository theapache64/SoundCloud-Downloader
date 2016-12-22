package com.theah64.soundclouddownloader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by shifar on 15/9/16.
 */
public class PrefUtils {
    private static final String X = PrefUtils.class.getSimpleName();
    private static PrefUtils instance;
    private final SharedPreferences pref;

    private PrefUtils(final Context context) {
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PrefUtils getInstance(final Context context) {
        if (instance == null) {
            instance = new PrefUtils(context);
        }
        return instance;
    }

    public SharedPreferences getPref() {
        return pref;
    }

    public SharedPreferences.Editor getEditor() {
        return pref.edit();
    }
}
