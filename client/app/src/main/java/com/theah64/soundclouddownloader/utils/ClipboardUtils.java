package com.theah64.soundclouddownloader.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.util.Set;

/**
 * Created by theapache64 on 15/12/16.
 */

public class ClipboardUtils {

    public static final String SOUNDCLOUD_URL_REGEX = ".*(?:http|https):\\/\\/soundcloud\\.com\\/(?:.+)\\/(?:.+).*";
    private static final String X = ClipboardUtils.class.getSimpleName();

    public static String getSoundCloudUrl(final Context context) {
        String clipboardData = null;

        final ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager != null) {

            final ClipData primaryClip = clipboardManager.getPrimaryClip();

            if (primaryClip != null) {

                final ClipData.Item clipData = primaryClip.getItemAt(0);
                if (clipData != null) {
                    clipboardData = clipData.getText().toString();
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

            }

        }

        return null;

    }
}
