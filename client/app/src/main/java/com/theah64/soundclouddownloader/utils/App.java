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
import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.interfaces.PlaylistListener;
import com.theah64.soundclouddownloader.interfaces.TrackListener;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.io.File;

/**
 * Created by theapache64 on 9/12/16.
 * Key:thessoccauldastaisillype
 * Password:a5d37fe917104a40f338d00abf8bc54abc4f2f75
 */

@ReportsCrashes(
        formUri = "https://soundclouddownloader.cloudant.com/acra-soundclouddownloader/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "thessoccauldastaisillype",
        formUriBasicAuthPassword = "a5d37fe917104a40f338d00abf8bc54abc4f2f75",
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.Application_crashed
)
public class App extends Application {

    public static final boolean IS_DEBUG_MODE = false;
    public static final String STORE_URL = "http://play.google.com/store/apps/details?id=com.theah64.soundclouddownloader";
    private static final String FOLDER_NAME = "SoundCloudDownloader";
    private static String DEFAULT_STORAGE_LOCATION;

    private static void initImageLoader(final Context context) {

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();

        final DisplayImageOptions defaultImageOption = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .showImageOnLoading(R.drawable.ic_loading_24dp)
                .showImageForEmptyUri(R.drawable.ic_loading_24dp)
                .showImageOnFail(R.drawable.ic_loading_24dp)
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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(this);
        DEFAULT_STORAGE_LOCATION = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + File.separator + FOLDER_NAME;
    }

    private TrackListener trackListener = null;
    private PlaylistListener playlistListener = null;

    public TrackListener getTrackListener() {
        return trackListener;
    }

    public void setTrackListener(TrackListener trackListener) {
        this.trackListener = trackListener;
    }

    public PlaylistListener getPlaylistListener() {
        return playlistListener;
    }

    public void setPlaylistListener(PlaylistListener playlistListener) {
        this.playlistListener = playlistListener;
    }
}
