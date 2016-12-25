package com.theah64.soundclouddownloader.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v22Tag;
import com.mpatric.mp3agic.ID3v23Tag;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.theah64.musicdog.R;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.Track;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class OnDownloadFinishedReceiver extends BroadcastReceiver {

    private static final String X = OnDownloadFinishedReceiver.class.getSimpleName();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private static final String GITHUB_URL = "https://github.com/theapache64/soundcloud-downloader";

    public OnDownloadFinishedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        final String stringDownloadId = String.valueOf(downloadId);
        Log.d(X, "Download finished :  id : " + downloadId);

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final Cursor cursor = dm.query(query);

        Log.d(X, "Cursor : " + cursor);
        if (cursor != null) {

            if (cursor.moveToFirst()) {

                final int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    
                    final Tracks tracksTable = Tracks.getInstance(context);

                    if (!tracksTable.update(Tracks.COLUMN_DOWNLOAD_ID, stringDownloadId, Tracks.COLUMN_IS_DOWNLOADED, Tracks.TRUE, handler)) {
                        FirebaseCrash.log("Failed to update download status");
                    }

                    final Track downloadedTrack = tracksTable.get(Tracks.COLUMN_DOWNLOAD_ID, stringDownloadId);

                    if (downloadedTrack != null) {

                        Toast.makeText(context, "Track downloaded -> " + downloadedTrack.getTitle(), Toast.LENGTH_SHORT).show();

                        //Changing id3 tags
                        if (downloadedTrack.isMP3()) {

                            try {
                                final Mp3File mp3File = new Mp3File(downloadedTrack.getFile().getAbsolutePath());

                                ID3v2 id3v2Tag;
                                if (mp3File.hasId3v2Tag()) {
                                    id3v2Tag = mp3File.getId3v2Tag();
                                } else {
                                    id3v2Tag = new ID3v22Tag();
                                    mp3File.setId3v2Tag(id3v2Tag);
                                }


                                //Setting album art
                                final Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_tinypng);
                                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                logo.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                final byte[] bitmapBytes = baos.toByteArray();
                                id3v2Tag.setAlbumImage(bitmapBytes, "image/png");
                                baos.flush();
                                baos.close();

                                final String watermark = context.getString(R.string.app_name);
                                final String downloadedThrough = context.getString(R.string.Downloaded_through_SoundCloud_Downloader);

                                final ID3v1 id3v1 = new ID3v1Tag();
                                mp3File.setId3v1Tag(id3v1);

                                id3v1.setTitle(downloadedTrack.getTitle());
                                id3v1.setAlbum(watermark);
                                id3v1.setTrack(watermark);
                                id3v1.setArtist(watermark);
                                id3v1.setComment(downloadedThrough);

                                id3v2Tag.setTitle(downloadedTrack.getTitle());
                                id3v2Tag.setAlbumArtist(watermark);
                                id3v2Tag.setAlbum(watermark);
                                id3v2Tag.setComposer(downloadedThrough);
                                id3v2Tag.setOriginalArtist(downloadedThrough);
                                id3v2Tag.setCopyright(watermark);
                                id3v2Tag.setPublisher(watermark);
                                id3v2Tag.setUrl(GITHUB_URL);
                                id3v2Tag.setArtist(watermark);
                                id3v2Tag.setTrack(watermark);

                                final String tempMp3Path = downloadedTrack.getFile().getAbsolutePath() + ".tmp";
                                mp3File.save(tempMp3Path);

                                //Deleting old file and replacing with the id3 version.
                                //noinspection ResultOfMethodCallIgnored
                                new File(tempMp3Path).renameTo(downloadedTrack.getFile());

                            } catch (IOException | UnsupportedTagException | InvalidDataException | NotSupportedException e) {
                                e.printStackTrace();
                            }

                        }

                    }


                } else {
                    Log.e(X, "Download status : " + downloadStatus);
                }

            }

            cursor.close();
        } else {
            Log.e(X, "Couldn't find download with download id " + stringDownloadId);
        }


    }
}
