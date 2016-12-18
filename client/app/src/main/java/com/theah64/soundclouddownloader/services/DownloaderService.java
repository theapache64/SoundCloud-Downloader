package com.theah64.soundclouddownloader.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.ui.activities.PlaylistDownloadActivity;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.ui.activities.settings.SettingsActivity;
import com.theah64.soundclouddownloader.utils.APIRequestBuilder;
import com.theah64.soundclouddownloader.utils.APIResponse;
import com.theah64.soundclouddownloader.utils.App;
import com.theah64.soundclouddownloader.utils.DownloadUtils;
import com.theah64.soundclouddownloader.utils.OkHttpUtils;
import com.theah64.soundclouddownloader.utils.PrefUtils;
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

/**
 * https://code2flow.com/97OZlU.png
 */
public class DownloaderService extends Service {

    private static final String X = DownloaderService.class.getSimpleName();
    public static final String KEY_NOTIFICATION_ID = "my_notification_id";

    private Tracks tracksTable;

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

    private Notification notification;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notification = new Notification(this);

        /*start;
        Read soundCloudUrl;
        if(soundCloudUrl is a track){
            if(track exist in db and storage){
                show track exists;
            }else if(track exist in db and not in strg){
                add to d-queue and update storage path;
            }else{
                add track to d-queue and add track to db;
            }
        }else{
            start download playlist;
        }
        end;*/
        Log.d(X, "Download service initialized : " + intent);

        if (intent != null) {

            //Hiding clipboard notification if exists.
            final int clipNotifId = intent.getIntExtra(KEY_NOTIFICATION_ID, -1);
            if (clipNotifId != -1) {
                notification.getNotificationManager().cancel(clipNotifId);
            }

            final String soundCloudUrl = intent.getStringExtra(Tracks.COLUMN_SOUNDCLOUD_URL);

            if (!Playlist.isPlaylist(soundCloudUrl)) {

                //It's a track
                tracksTable = Tracks.getInstance(this);
                final Track track = tracksTable.get(Tracks.COLUMN_SOUNDCLOUD_URL, soundCloudUrl);
                if (track != null && track.isExistInStorage()) {
                    //track exist in db and storage

                    //noinspection ConstantConditions - already checked with track.isExistInStorage;
                    notification.showNotification(getString(R.string.Track_exists), track.getTitle(), track.getFile().getAbsolutePath(), false);

                } else {
                    fireApi(track, soundCloudUrl);
                }
            } else {
                fireApi(null, soundCloudUrl);
            }


        } else {
            Log.e(X, "Intent can't be null");
        }


        return START_STICKY;
    }

    private void fireApi(final Track track, final String soundCloudUrl) {

        notification.showNotification(getString(R.string.initializing_download), track == null ? soundCloudUrl : track.getTitle(), track == null ? null : track.getTitle(), true);

        //Building json download request
        final Request scdRequest = new APIRequestBuilder("/scd/json")
                .addParam("soundcloud_url", soundCloudUrl)
                .build();

        //Processing request
        OkHttpUtils.getInstance().getClient().newCall(scdRequest).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                showToast("ERROR: " + e.getMessage());

                notification.showNotification(
                        getString(R.string.Network_error),
                        getString(R.string.network_error),
                        e.getMessage(),
                        false
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final APIResponse apiResponse = new APIResponse(OkHttpUtils.logAndGetStringBody(response));

                    final JSONObject joData = apiResponse.getJSONObjectData();
                    final JSONArray jaTracks = joData.getJSONArray("tracks");

                    if (!joData.has(Track.KEY_PLAYLIST_NAME)) {

                        final JSONObject joTrack = jaTracks.getJSONObject(0);

                        final String downloadUrl = joTrack.getString("download_url");

                        //Single track
                        if (track != null && !track.isExistInStorage()) {


                            //Track exist in db but in storage so download
                            track.setDownloadUrl(downloadUrl);
                            final long downloadId = DownloadUtils.addToDownloadQueue(DownloaderService.this, track);
                            //Updating
                            track.setDownloadId(String.valueOf(downloadId));

                            //Track exist so just updating the download id.
                            tracksTable.update(track, handler);
                            showToast(getString(R.string.s_added_to_download_queue, track.getTitle()));

                        } else {

                            //New track
                            final String title = joTrack.getString("title");

                            String artworkUrl = null;
                            if (joTrack.has(Tracks.COLUMN_ARTWORK_URL)) {
                                artworkUrl = joTrack.getString(Tracks.COLUMN_ARTWORK_URL);
                                Log.d(X, title + " has artwork " + artworkUrl);
                            } else {
                                Log.e(X, title + " hasn't artwork url ");
                            }

                            final String username = joTrack.getString(Tracks.COLUMN_USERNAME);
                            final long duration = joTrack.getLong(Tracks.COLUMN_DURATION);

                            final String fileName = joTrack.getString("filename");
                            final String baseStorageLocation = PrefUtils.getInstance(DownloaderService.this).getPref().getString(SettingsActivity.SettingsFragment.KEY_STORAGE_LOCATION, App.getDefaultStorageLocation());
                            final String absFilePath = String.format("%s/%s", baseStorageLocation, fileName);

                            final Track newtrack = new Track(null, title, username, downloadUrl, artworkUrl, null, soundCloudUrl, null, false, false, new File(absFilePath), duration);
                            final long downloadId = DownloadUtils.addToDownloadQueue(DownloaderService.this, newtrack);

                            //Starting download
                            newtrack.setDownloadId(String.valueOf(downloadId));

                            //Adding track to database -
                            tracksTable.add(newtrack, handler);
                            showToast(getString(R.string.s_added_to_download_queue, newtrack.getTitle()));
                        }


                    } else {

                        //It's a playlist
                        showToast("Playlist ready!");

                        final String playlistName = joData.getString("playlist_name");
                        final String username = joData.getString("username");

                        String artworkUrl = null;
                        if (joData.has(Playlists.COLUMN_ARTWORK_URL)) {
                            artworkUrl = joData.getString(Playlists.COLUMN_ARTWORK_URL);
                        }

                        final Intent playListDownloadIntent = new Intent(DownloaderService.this, PlaylistDownloadActivity.class);

                        playListDownloadIntent.putExtra(Playlist.KEY, new Playlist(null, playlistName, username, soundCloudUrl, artworkUrl, -1, -1, -1));
                        playListDownloadIntent.putExtra(PlaylistDownloadActivity.KEY_TRACKS, jaTracks.toString());


                        playListDownloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(playListDownloadIntent);

                    }


                    notification.dismiss();

                } catch (APIResponse.APIException | JSONException e) {
                    e.printStackTrace();
                    notification.showNotification(
                            getString(R.string.Server_error),
                            getString(R.string.Server_error_occurred), e.getMessage(), false);
                    showToast("ERROR: " + e.getMessage());
                }
            }

        });


    }

    private static class Notification {

        private int notifId;
        private NotificationManager nm;
        private NotificationCompat.Builder notBuilder;

        public Notification(final Context context) {
            notifId = Random.getRandomInt();
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            this.notBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_logo_white)
                    .setAutoCancel(false)
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo_color_24dp));

        }

        private void showNotification(final String title, final String message, final String info, final boolean showProgress) {

            if (showProgress) {
                notBuilder.setProgress(100, 0, true);
            } else {
                notBuilder.setProgress(0, 0, false);
            }

            notBuilder
                    .setTicker(title)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentInfo(info);

            nm.notify(notifId, notBuilder.build());
        }

        NotificationManager getNotificationManager() {
            return nm;
        }

        void dismiss() {
            nm.cancel(notifId);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static PendingIntent getDismissIntent(int notifId, Context context) {
        final Intent intent = new Intent(context, DownloaderService.class);
        intent.putExtra(KEY_NOTIFICATION_ID, notifId);
        return PendingIntent.getService(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
