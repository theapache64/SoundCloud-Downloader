package com.theah64.scd.core;

import com.theah64.scd.models.Track;
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

    public static Track getTrack(String soundCloudUrl) {
        final Track track = getSoundCloudTrack(soundCloudUrl);
        if (track != null) {
            final String downloadUrl = getSoundCloudDownloadUrl(track.getId());
            if (downloadUrl != null) {
                track.setDownloadUrl(downloadUrl);
                return track;
            }
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

    private static Track getSoundCloudTrack(String soundCloudUrl) {

        final String resolveTrackResp = new NetworkHelper(String.format(RESOLVE_TRACK_URL_FORMAT, soundCloudUrl)).getResponse();
        if (resolveTrackResp != null) {
            try {
                final JSONObject joSoundCloudTrack = new JSONObject(resolveTrackResp);
                final String trackId = String.valueOf(joSoundCloudTrack.getInt("id"));
                final String trackName = joSoundCloudTrack.getString("title");

                return new Track(trackId, trackName);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
