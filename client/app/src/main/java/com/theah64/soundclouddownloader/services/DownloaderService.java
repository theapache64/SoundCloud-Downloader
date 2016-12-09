package com.theah64.soundclouddownloader.services;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.activities.DownloaderActivity;
import com.theah64.soundclouddownloader.utils.APIRequestBuilder;
import com.theah64.soundclouddownloader.utils.APIResponse;
import com.theah64.soundclouddownloader.utils.NetworkUtils;
import com.theah64.soundclouddownloader.utils.OkHttpUtils;
import com.theah64.soundclouddownloader.utils.Random;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class DownloaderService extends Service {

    private static final String X = DownloaderService.class.getSimpleName();
    private static final String KEY_DOWNLOAD_URL = "download_url";
    private static final String KEY_NAME = "name";
    private int notifId;
    private NotificationManager nm;
    private NotificationCompat.Builder apiNotification;

    public DownloaderService() {
    }

    public void showToast(final @StringRes int stringRes) {
        showToast(getString(stringRes));
    }

    private final Handler handler = new Handler(Looper.getMainLooper());

    private void showToast(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (NetworkUtils.isNetwork(this)) {

            final String soundCloudUrl = intent.getStringExtra(DownloaderActivity.KEY_SOUNDCLOUD_URL);

            apiNotification = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.initializing_download))
                    .setContentText(soundCloudUrl)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setProgress(100, 0, true)
                    .setAutoCancel(false)
                    .setTicker(getString(R.string.initializing_download))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

            nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notifId = Random.getRandomInt();
            nm.notify(notifId, apiNotification.build());

            //Building json download request
            final Request scdRequest = new APIRequestBuilder("/scd/json")
                    .addParam(DownloaderActivity.KEY_SOUNDCLOUD_URL, soundCloudUrl)
                    .build();

            //Processing request
            OkHttpUtils.getInstance().getClient().newCall(scdRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    showToast("ERROR: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        final APIResponse apiResponse = new APIResponse(OkHttpUtils.logAndGetStringBody(response));

                        final JSONObject joData = apiResponse.getJSONObjectData();

                        final String name = joData.getString(KEY_NAME);
                        final String downloadUrl = joData.getString(KEY_DOWNLOAD_URL);

                        apiNotification.setContentTitle(getString(R.string.Starting_download));
                        apiNotification.setContentText(downloadUrl);
                        nm.notify(notifId, apiNotification.build());

                        //Starting download
                        final DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadUrl));

                        final String fileName = name + ".mp3";

                        downloadRequest.setTitle(fileName);
                        downloadRequest.setDescription(name + " - downloaded from Github scd");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            downloadRequest.allowScanningByMediaScanner();
                            downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        }

                        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                        ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(downloadRequest);

                        nm.cancel(notifId);
                        showToast("Download started");

                    } catch (APIResponse.APIException | JSONException e) {
                        e.printStackTrace();
                        showToast("ERROR: " + e.getMessage());
                    }
                }
            });

        } else {
            showToast(R.string.network_error);
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
