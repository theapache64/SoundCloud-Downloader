package com.theah64.scd.core;

import com.theah64.scd.utils.NetworkHelper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by theapache64 on 8/12/16.
 */
public class SoundCloudDownloader {

    private static final String CLIENT_ID = "a3e059563d7fd3372b49b37f00a00bcf";

    private static final String RESOLVE_TRACK_URL_FORMAT = "https://api.soundcloud.com/resolve.json?url=%s&client_id=" + CLIENT_ID;
    private static final String STREAM_TRACK_URL_FORMAT = "https://api.soundcloud.com/i1/tracks/%s/streams?client_id=" + CLIENT_ID;

    public static String getDownloadUrl(String soundCloudUrl) {
        final String trackId = getTrackId(soundCloudUrl);
        if (trackId != null) {
            return getSoundCloudDownloadUrl(trackId);
        }
        return null;
    }

    private static String getSoundCloudDownloadUrl(String trackId) {
        final String downloadTrackResp = new NetworkHelper(String.format(STREAM_TRACK_URL_FORMAT, trackId)).getResponse();
        if (downloadTrackResp != null) {
            try {
                return new JSONObject(downloadTrackResp).getString("http_mp3_128_url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getTrackId(String soundCloudUrl) {

        final String resolveTrackResp = new NetworkHelper(String.format(RESOLVE_TRACK_URL_FORMAT, soundCloudUrl)).getResponse();
        if (resolveTrackResp != null) {
            try {
                return String.valueOf(new JSONObject(resolveTrackResp).getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
