package com.theah64.musicdog.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.Set;

/**
 * Created by theapache64 on 15/12/16.
 */

public class ClipboardUtils {

    public static final String SOUNDCLOUD_URL_REGEX = ".*(?:http|https):\\/\\/soundcloud\\.com\\/(?:.+)\\/(?:.+).*";
    private static final String X = ClipboardUtils.class.getSimpleName();

    public static String getSoundCloudUrl(final Context context) {
        final String clipboardData;
        String soundCloudUrl = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardData = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
        } else {
            clipboardData = ((android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).getText().toString();
        }

        System.out.println("Clipboard: " + clipboardData);

        final Set<String> urls = DownloadIgniter.UrlParser.parseUrls(clipboardData);
        if (urls != null) {
            for (final String url : urls) {
                if (url.matches(SOUNDCLOUD_URL_REGEX)) {
                    soundCloudUrl = url;
                    Log.d(X, "SoundCloud URL: " + url);
                    return url;
                }
            }
        }

        return null;

    }
}
