package com.theah64.soundclouddownloader.services;

import android.app.DownloadManager;
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
import com.theah64.soundclouddownloader.database.Playlists;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.ui.activities.PlaylistDownloadActivity;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.utils.APIRequestBuilder;
import com.theah64.soundclouddownloader.utils.APIResponse;
import com.theah64.soundclouddownloader.utils.DownloadUtils;
import com.theah64.soundclouddownloader.utils.NetworkUtils;
import com.theah64.soundclouddownloader.utils.OkHttpUtils;
import com.theah64.soundclouddownloader.utils.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class DownloaderService extends Service {

    private static final String X = DownloaderService.class.getSimpleName();


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
        Log.d(X, "Message : " + message);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(X, "Download service initialized : " + intent);

        if (intent == null) {
            throw new IllegalArgumentException("Intent can't be null");
        }

        if (NetworkUtils.isNetwork(this)) {

            final Tracks tracksTable = Tracks.getInstance(this);

            final String soundCloudUrl = intent.getStringExtra(Tracks.COLUMN_SOUNDCLOUD_URL);

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
                    .addParam("soundcloud_url", soundCloudUrl)
                    .build();

            //Processing request
            OkHttpUtils.getInstance().getClient().newCall(scdRequest).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    showToast("ERROR: " + e.getMessage());
                    showErrorNotification(e.getMessage(), null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        final APIResponse apiResponse = new APIResponse(OkHttpUtils.logAndGetStringBody(response));

                        final JSONObject joData = apiResponse.getJSONObjectData();
                        final JSONArray jaTracks = joData.getJSONArray("tracks");


                        if (!joData.has(Track.KEY_PLAYLIST_NAME)) {

                            // Single song
                            final JSONObject joTrack = jaTracks.getJSONObject(0);

                            final String title = joTrack.getString("title");
                            final String downloadUrl = joTrack.getString("download_url");
                            final String fileName = joTrack.getString("filename");

                            String artworkUrl = null;
                            if (joTrack.has(Tracks.COLUMN_ARTWORK_URL)) {
                                artworkUrl = joTrack.getString(Tracks.COLUMN_ARTWORK_URL);
                                Log.d(X, title + " has artwork " + artworkUrl);
                            } else {
                                Log.e(X, title + " hasn't artwork url ");
                            }

                            apiNotification.setContentTitle(getString(R.string.Starting_download));
                            apiNotification.setContentText(downloadUrl);
                            nm.notify(notifId, apiNotification.build());

                            //Checking file existence
                            final String subPath = File.separator + "/SoundCloud Downloader/" + fileName;
                            final String absFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + subPath;
                            final File trackFile = new File(absFilePath);

                            if (!trackFile.exists()) {

                                final Track track = new Track(null, title, null, artworkUrl, null, soundCloudUrl, null, false, false, trackFile);

                                //Starting download
                                final long downloadId = DownloadUtils.addToDownloadQueue(DownloaderService.this, track);
                                track.setDownloadId(String.valueOf(downloadId));

                                final String trackId = tracksTable.get(Tracks.COLUMN_SOUNDCLOUD_URL, soundCloudUrl, Tracks.COLUMN_ID);


                                if (trackId == null) {
                                    //Adding track to database -
                                    tracksTable.add(track);
                                } else {
                                    //Track exist so just updating the download id.
                                    tracksTable.update(Tracks.COLUMN_ID, trackId, Tracks.COLUMN_DOWNLOAD_ID, String.valueOf(downloadId));
                                }

                                nm.cancel(notifId);
                                showToast("Download started");

                            } else {
                                showErrorNotification("Existing song : " + title + ", download skipped.", absFilePath);
                            }

                        } else {
                            //It's a playlist
                            showToast("It's a playlist");

                            String artworkUrl = null;
                            if (joData.has(Playlists.COLUMN_ARTWORK_URL)) {
                                artworkUrl = joData.getString(Playlists.COLUMN_ARTWORK_URL);
                            }

                            final Intent playListDownloadIntent = new Intent(DownloaderService.this, PlaylistDownloadActivity.class);

                            playListDownloadIntent.putExtra(Track.KEY_PLAYLIST_NAME, joData.getString(Track.KEY_PLAYLIST_NAME));
                            playListDownloadIntent.putExtra(PlaylistDownloadActivity.KEY_TRACKS, jaTracks.toString());
                            playListDownloadIntent.putExtra(Playlists.COLUMN_SOUNDCLOUD_URL, soundCloudUrl);
                            playListDownloadIntent.putExtra(Playlists.COLUMN_ARTWORK_URL, artworkUrl);

                            playListDownloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(playListDownloadIntent);

                            nm.cancel(notifId);
                        }


                    } catch (APIResponse.APIException | JSONException e) {
                        e.printStackTrace();
                        showErrorNotification(e.getMessage(), null);
                        showToast("ERROR: " + e.getMessage());
                    }
                }

            });

        } else {
            showToast(R.string.network_error);
        }


        return START_STICKY;
    }

    private void showErrorNotification(String message, String absFilePath) {
        apiNotification.setContentTitle(message)
                .setContentText(absFilePath)
                .setProgress(0, 0, false);
        nm.notify(notifId, apiNotification.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
