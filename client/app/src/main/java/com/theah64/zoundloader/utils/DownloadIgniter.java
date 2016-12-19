package com.theah64.zoundloader.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.theah64.zoundloader.R;
import com.theah64.zoundloader.database.Tracks;
import com.theah64.zoundloader.models.Track;
import com.theah64.zoundloader.services.DownloaderService;

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
        if (url != null && url.contains("soundcloud.com/")) {

            //Checking if the track contains in the db and track exists
            final Track track = Tracks.getInstance(context).get(Tracks.COLUMN_SOUNDCLOUD_URL, url);

            if (track != null && track.getFile().exists()) {
                //Track exist
                Toast.makeText(context, context.getString(R.string.Existing_track_s, track.getTitle()), Toast.LENGTH_SHORT).show();
            } else {

                final Intent downloadIntent = new Intent(context, DownloaderService.class);
                downloadIntent.putExtra(Tracks.COLUMN_SOUNDCLOUD_URL, url);
                context.startService(downloadIntent);
                Toast.makeText(context, R.string.initializing_download, Toast.LENGTH_SHORT).show();
            }

        } else {
            //Invalid sound cloud url
            Toast.makeText(context, R.string.Invalid_soundcloud_URL, Toast.LENGTH_SHORT).show();
        }
    }

    public static class UrlParser {

        // Pattern for recognizing a URL, based off RFC 3986
        private static final Pattern urlPattern = Pattern.compile(
                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);


        static String parse(final String data) {

            Matcher matcher = urlPattern.matcher(data);
            if (matcher.find()) {
                int matchStart = matcher.start(1);
                int matchEnd = matcher.end();
                return data.substring(matchStart, matchEnd);
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
