package com.theah64.soundclouddownloader.utils;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by theapache64 on 14/12/16.
 */

public class CommonUtils {

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
}
