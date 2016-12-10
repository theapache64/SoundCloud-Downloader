package com.theah64.scd.core;

import com.theah64.scd.database.tables.Preference;
import com.theah64.scd.models.Track;
import com.theah64.scd.utils.NetworkHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 8/12/16.
 * Track resolve example : https://api.soundcloud.com/resolve.json?url=https://soundcloud.com/mr-sidhu/kala-chashma-amar-arshi-neha-kakkar-badsha-by-sidhu&client_id=a3e059563d7fd3372b49b37f00a00bcf
 * Playlist resolve example : https://api.soundcloud.com/resolve.json?url=https://soundcloud.com/theapache64/sets/twinkewinkle&client_id=a3e059563d7fd3372b49b37f00a00bcf
 */
public class SoundCloudDownloader {

    private static final String CLIENT_ID = "a3e059563d7fd3372b49b37f00a00bcf";

    private static final String RESOLVE_TRACK_URL_FORMAT = "https://api.soundcloud.com/resolve.json?url=%s&client_id=" + CLIENT_ID;
    private static final String STREAM_TRACK_URL_FORMAT = "https://api.soundcloud.com/i1/tracks/%s/streams?client_id=" + CLIENT_ID;

    public static JSONArray getTracks(String soundCloudUrl) {

        JSONArray jaTracks = getSoundCloudTracks(soundCloudUrl);

        if (jaTracks != null) {

            try {

                for (int i = 0; i < jaTracks.length(); i++) {

                    final JSONObject joTrack = jaTracks.getJSONObject(i);

                    final String downloadUrl = getSoundCloudDownloadUrl(joTrack.getString(Track.KEY_ID));
                    if (downloadUrl != null) {
                        joTrack.put(Track.KEY_DOWNLOAD_URL, downloadUrl);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                jaTracks = null;
            }

        }

        return jaTracks;
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

    private static JSONArray getSoundCloudTracks(String soundCloudUrl) {

        final String resolveTrackResp = new NetworkHelper(String.format(RESOLVE_TRACK_URL_FORMAT, soundCloudUrl)).getResponse();

        if (resolveTrackResp != null) {
            try {
                final JSONObject joResolve = new JSONObject(resolveTrackResp);
                final JSONArray jaTracks = new JSONArray();

                final String fileNameFormat = Preference.getInstance().getString(Preference.KEY_FILENAME_FORMAT);

                if (joResolve.has("playlist_type")) {
                    //Url was a playlist
                    final JSONArray jaResolvedTracks = joResolve.getJSONArray("tracks");
                    for (int i = 0; i < jaResolvedTracks.length(); i++) {

                        final JSONObject joResolvedTrack = jaResolvedTracks.getJSONObject(i);

                        //Url is a single track
                        final String trackId = String.valueOf(joResolvedTrack.getInt("id"));
                        final String trackName = joResolvedTrack.getString("title");
                        final String originalFormat = joResolvedTrack.getString("original_format");
                        final String fileName = String.format(fileNameFormat, trackName, originalFormat);

                        final JSONObject joTrack = new JSONObject();
                        joTrack.put(Track.KEY_ID, trackId);
                        joTrack.put(Track.KEY_TITLE, trackName);
                        joTrack.put(Track.KEY_ORIGINAL_FORMAT, originalFormat);
                        joTrack.put(Track.KEY_FILENAME, fileName);

                        jaTracks.put(joTrack);
                    }

                } else {

                    //Url is a single track
                    final String trackId = String.valueOf(joResolve.getInt("id"));
                    final String trackName = joResolve.getString("title");

                    final String originalFormat = joResolve.getString("original_format");
                    final String fileName = String.format(fileNameFormat, trackName, originalFormat);

                    final JSONObject joTrack = new JSONObject();
                    joTrack.put(Track.KEY_ID, trackId);
                    joTrack.put(Track.KEY_TITLE, trackName);
                    joTrack.put(Track.KEY_ORIGINAL_FORMAT, originalFormat);
                    joTrack.put(Track.KEY_FILENAME, fileName);

                    jaTracks.put(joTrack);
                }

                return jaTracks;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
