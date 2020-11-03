package com.theah64.soundclouddownloader.utils;

import android.content.Context;
import android.content.Intent;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.services.DownloaderService;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 15/12/16.
 */

public class DownloadIgniter {

    public static void ignite(final Context context, String data) {

        final String url = UrlParser.parse(data);
        if (url != null && url.contains("https://soundcloud")) {

            final Intent downloadIntent = new Intent(context, DownloaderService.class);
            downloadIntent.putExtra(Tracks.COLUMN_SOUNDCLOUD_URL, url);
            context.startService(downloadIntent);

        } else {
            //Invalid sound cloud url
            SingletonToast.makeText(context, R.string.Invalid_soundcloud_URL);
        }
    }

    static class UrlParser {

        // Pattern for recognizing a URL, based off RFC 3986
        private static final Pattern urlPattern = Pattern.compile(
                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);


        static String parse(final String data) {

            if (data != null) {
                Matcher matcher = urlPattern.matcher(data);
                if (matcher.find()) {
                    int matchStart = matcher.start(1);
                    int matchEnd = matcher.end();
                    return data.substring(matchStart, matchEnd);
                }
            }

            return null;
        }

        static Set<String> parseUrls(String data) {
            final Matcher matcher = urlPattern.matcher(data);
            Set<String> urls = null;
            if (matcher.find()) {
                urls = new HashSet<>();
                do {
                    int matchStart = matcher.start(1);
                    int matchEnd = matcher.end();
                    urls.add(data.substring(matchStart, matchEnd));
                } while (matcher.find());
            }

            return urls;
        }
    }
}
