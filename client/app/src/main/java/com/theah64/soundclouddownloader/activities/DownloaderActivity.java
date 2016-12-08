package com.theah64.soundclouddownloader.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.theah64.soundclouddownloader.R;

public class DownloaderActivity extends AppCompatActivity {


    private static final String X = DownloaderActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        Log.d(X, "Intent: " + getIntent());
    }
}
