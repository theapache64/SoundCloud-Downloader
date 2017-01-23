package com.theah64.soundclouddownloader.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

import java.util.Set;

/**
 * Created by theapache64 on 15/12/16.
 */

public class ClipboardUtils {

    public static final String SOUNDCLOUD_URL_REGEX = ".*(?:http|https):\\/\\/soundcloud\\.com\\/(?:.+)\\/(?:.+).*";
    private static final String X = ClipboardUtils.class.getSimpleName();

    public static String getSoundCloudUrl(final Context context) {
        String clipboardData = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            final ClipData.Item clipData = clipboardManager.getPrimaryClip().getItemAt(0);
            if (clipData != null) {
                clipboardData = clipData.getText().toString();
            }
        } else {
            clipboardData = ((android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).getText().toString();
        }

        if (clipboardData != null) {

            System.out.println("Clipboard: " + clipboardData);

            final Set<String> urls = DownloadIgniter.UrlParser.parseUrls(clipboardData);
            if (urls != null) {
                for (final String url : urls) {
                    if (url.matches(SOUNDCLOUD_URL_REGEX)) {
                        return url;
                    }
                }
            }
        }

        return null;

    }
}
