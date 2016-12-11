package com.theah64.soundclouddownloader.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.theah64.soundclouddownloader.BuildConfig;
import com.theah64.soundclouddownloader.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3500;
    private static final int RQ_CODE_RQ_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.GET_ACCOUNTS)) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RQ_CODE_RQ_WRITE_EXTERNAL_STORAGE);
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RQ_CODE_RQ_WRITE_EXTERNAL_STORAGE);
                }

            } else {
                doNormalSplashWork();
            }

        } else {
            doNormalSplashWork();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RQ_CODE_RQ_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doNormalSplashWork();
            } else {
                Toast.makeText(SplashActivity.this, "You must accept the WRITE STORAGE permission.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private void doNormalSplashWork() {

        ((TextView) findViewById(R.id.tvAppVersion)).setText(String.format("v%s", BuildConfig.VERSION_NAME));

        //Checking if the api key exists
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, SPLASH_DELAY);

    }


    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}
