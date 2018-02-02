package com.theah64.soundclouddownloader.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by theapache64 on 23/1/17.
 */
public class UriCompat {

    public static Uri fromFile(final Context context, File file, @Nullable Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (intent != null) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        }

        return Uri.fromFile(file);
    }

    public static Uri fromFile(final Context context, File file) {
        return fromFile(context, file, null);
    }
}
