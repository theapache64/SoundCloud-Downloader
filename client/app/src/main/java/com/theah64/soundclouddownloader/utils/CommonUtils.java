package com.theah64.soundclouddownloader.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by theapache64 on 14/12/16.
 */

public class CommonUtils {

    private static final String X = CommonUtils.class.getSimpleName();

    public static String getMIMETypeFromUrl(final File file, final String defaultValue) {

        MimeTypeMap mime = MimeTypeMap.getSingleton();
        int index = file.getName().lastIndexOf('.') + 1;
        String ext = file.getName().substring(index).toLowerCase();
        final String mimeType = mime.getMimeTypeFromExtension(ext);

        if (mimeType != null) {
            return mimeType;
        }

        return defaultValue;
    }

    public static boolean isMyServiceRunning(final Context context, Class<?> serviceClass) {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(X, "Service running : " + serviceClass.getName());
                return true;
            }
        }

        Log.e(X, "Service not running : " + serviceClass.getName());
        return false;
    }

    public static String getSanitizedName(final String fileName) {
        return fileName.replaceAll("[^\\w]", "_").replaceAll("[_]{2,}", "_");
    }
}
