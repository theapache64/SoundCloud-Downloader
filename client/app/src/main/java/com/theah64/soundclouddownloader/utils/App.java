package com.theah64.soundclouddownloader.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.theah64.bugmailer.core.BugMailer;
import com.theah64.bugmailer.core.BugMailerConfig;
import com.theah64.bugmailer.exceptions.BugMailerException;
import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.interfaces.PlaylistListener;
import com.theah64.soundclouddownloader.interfaces.TrackListener;

import java.io.File;

/**
 * Created by theapache64 on 9/12/16.
 */
public class App extends Application {

    public static final String APK_DOWNLOAD_URL = "http://tinyurl.com/soundclouddownloader-apk";
    private static final String FOLDER_NAME = "SoundCloud Downloader";
    public static final String GITHUB_URL = "https://github.com/theapache64/soundcloud-downloader";
    public static final boolean IS_DEBUG_MODE = false;
    private static String DEFAULT_STORAGE_LOCATION;
    private TrackListener mainTrackListener = null, playlistTrackListener = null;
    private PlaylistListener playlistListener = null;

    private static void initImageLoader(final Context context) {

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();

        final DisplayImageOptions defaultImageOption = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .showImageOnLoading(R.drawable.ic_spinner_grey_24dp)
                .showImageForEmptyUri(R.drawable.ic_av_album_70dp)
                .showImageOnFail(R.drawable.ic_av_album_70dp)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        config.defaultDisplayImageOptions(defaultImageOption);

        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 100 MiB
        config.memoryCacheSize(50 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public static String getDefaultStorageLocation() {
        return DEFAULT_STORAGE_LOCATION;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(this);
        DEFAULT_STORAGE_LOCATION = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + File.separator + FOLDER_NAME;

        try {
            BugMailer.init(this, new BugMailerConfig("theapache64@gmail.com"));
        } catch (BugMailerException e) {
            e.printStackTrace();
        }
    }

    public TrackListener getMainTrackListener() {
        return mainTrackListener;
    }

    public void setMainTrackListener(TrackListener mainTrackListener) {
        this.mainTrackListener = mainTrackListener;
    }

    public PlaylistListener getPlaylistListener() {
        return playlistListener;
    }

    public void setPlaylistListener(PlaylistListener playlistListener) {
        this.playlistListener = playlistListener;
    }

    public TrackListener getPlaylistTrackListener() {
        return playlistTrackListener;
    }

    public void setPlaylistTrackListener(TrackListener playlistTrackListener) {
        this.playlistTrackListener = playlistTrackListener;
    }
}
