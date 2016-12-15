package com.theah64.soundclouddownloader.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.services.DownloaderService;
import com.theah64.soundclouddownloader.utils.DownloadIgniter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloaderActivity extends AppCompatActivity {


    private static final String X = DownloaderActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        final String data = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        DownloadIgniter.ignite(this,data);

        finish();

    }


}
