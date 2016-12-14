package com.theah64.soundclouddownloader.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.services.DownloaderService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloaderActivity extends AppCompatActivity {


    private static final String X = DownloaderActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        final String data = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        final String url = UrlParser.parse(data);
        if (url != null && url.contains("soundcloud.com/")) {

            //Checking if the track contains in the db and track exists
            final Track track = Tracks.getInstance(this).get(Tracks.COLUMN_SOUNDCLOUD_URL, url);

            if (track != null && track.getFile() != null && track.getFile().exists()) {
                //Track exist
                Toast.makeText(this, getString(R.string.Existing_track_s, track.getTitle()), Toast.LENGTH_SHORT).show();
            } else {

                final Intent downloadIntent = new Intent(this, DownloaderService.class);
                downloadIntent.putExtra(Tracks.COLUMN_SOUNDCLOUD_URL, url);
                startService(downloadIntent);
                Toast.makeText(this, R.string.initializing_download, Toast.LENGTH_SHORT).show();
            }

            finish();

        } else {
            //Invalid sound cloud url
            Toast.makeText(this, R.string.Invalid_soundcloud_URL, Toast.LENGTH_SHORT).show();
        }

    }

    private static class UrlParser {
        // Pattern for recognizing a URL, based off RFC 3986
        private static final Pattern urlPattern = Pattern.compile(
                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);


        public static String parse(final String data) {

            Matcher matcher = urlPattern.matcher(data);
            if (matcher.find()) {
                int matchStart = matcher.start(1);
                int matchEnd = matcher.end();
                return data.substring(matchStart, matchEnd);
            }

            return null;
        }

    }
}
