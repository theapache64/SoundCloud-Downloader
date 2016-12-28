package com.theah64.soundclouddownloader.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.theah64.soundclouddownloader.BuildConfig;
import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.utils.PrefUtils;
import com.theah64.soundclouddownloader.utils.SingletonToast;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1500;
    private static final int RQ_CODE_ASK_PERMISSION = 1;

    private static final String[] PERMISSIONS_NEEDED = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS
    };
    private static final String X = SplashActivity.class.getSimpleName();
    public static final String KEY_IS_ALL_PERMISSION_SET = "is_all_permission_set";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        ((TextView) findViewById(R.id.tvAppVersion)).setText(String.format("v%s", BuildConfig.VERSION_NAME));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            boolean isAllPermissionAccepted = true;
            for (final String perm : PERMISSIONS_NEEDED) {
                if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                    isAllPermissionAccepted = false;
                    break;
                }
            }

            if (!isAllPermissionAccepted) {
                requestPermissions(PERMISSIONS_NEEDED, RQ_CODE_ASK_PERMISSION);
            } else {
                doNormalSplashWork();
            }

        } else {
            doNormalSplashWork();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RQ_CODE_ASK_PERMISSION) {

            Log.d(X, "Grant result length: " + grantResults.length);

            boolean isAllPermissionGranted = true;
            for (final int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isAllPermissionGranted = false;
                    break;
                }
            }

            if (isAllPermissionGranted) {
                doNormalSplashWork();
            } else {
                SingletonToast.makeText(SplashActivity.this, R.string.You_must_accept_all_the_permissions, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private void doNormalSplashWork() {

        //Setting permission flag to true
        PrefUtils.getInstance(this).getEditor().putBoolean(KEY_IS_ALL_PERMISSION_SET, true).commit();


        //Checking if the api key exists
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();

            }
        }, SPLASH_DELAY);
    }
}
