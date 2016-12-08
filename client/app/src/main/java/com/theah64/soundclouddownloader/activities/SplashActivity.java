package com.theah64.soundclouddownloader.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.theah64.soundclouddownloader.BuildConfig;
import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.utils.App;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3500;
    private static final int RQ_CODE_RQ_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ((TextView) findViewById(R.id.tvAppVersion)).setText(String.format("v%s", BuildConfig.VERSION_NAME));

        //Checking if the api key exists
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, SPLASH_DELAY);


    }


    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}
