package com.theah64.soundclouddownloader.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.utils.DownloadIgniter;

import timber.log.Timber;

public class DownloaderActivity extends AppCompatActivity {


    private static final String X = DownloaderActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        final String data = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        DownloadIgniter.ignite(this, data);

        finish();

    }


}
