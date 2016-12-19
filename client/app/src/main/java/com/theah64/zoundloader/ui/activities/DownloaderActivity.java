package com.theah64.zoundloader.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.theah64.zoundloader.R;
import com.theah64.zoundloader.utils.DownloadIgniter;

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
