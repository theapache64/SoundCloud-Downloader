package com.theah64.scd.core;

import com.theah64.scd.database.tables.Preference;
import com.theah64.scd.database.tables.Requests;
import com.theah64.scd.database.tables.Tracks;
import com.theah64.scd.models.JSONTracks;
import com.theah64.scd.models.Track;
import com.theah64.scd.servlets.AdvancedBaseServlet;
import com.theah64.scd.utils.FileNameUtils;
import com.theah64.scd.utils.NetworkHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by theapache64 on 8/12/16.
 * Track resolve example : https://api.soundcloud.com/resolve.json?url=https://soundcloud.com/theapache64/tomorrowland-2014-ultra-festival-2014-ringtone&client_id=a3e059563d7fd3372b49b37f00a00bcf
 * Playlist resolve example : https://api.soundcloud.com/resolve.json?url=https://soundcloud.com/theapache64/sets/twinkewinkle&client_id=a3e059563d7fd3372b49b37f00a00bcf
 */
public class SoundCloudDownloader {

    public static final String CLIENT_ID = "a3e059563d7fd3372b49b37f00a00bcf";

    private static final String RESOLVE_TRACK_URL_FORMAT = "https://api.soundcloud.com/resolve.json?url=%s&client_id=" + CLIENT_ID;
    private static final String DOWNLOAD_TRACK_URL_FORMAT = String.format("%s%s/download?id=%%s", AdvancedBaseServlet.getBaseUrl(), AdvancedBaseServlet.VERSION_CODE);
    private static final String DIRECT_DOWNLOAD_TRACK_URL_FORMAT = String.format("%s%s/direct_download?id=%%s", AdvancedBaseServlet.getBaseUrl(), AdvancedBaseServlet.VERSION_CODE);
    private static final String KEY_ARTWORK_URL = "artwork_url";

    public static JSONTracks getSoundCloudTracks(final String requestId, String soundCloudUrl) {

        final String resolveTrack = String.format(RESOLVE_TRACK_URL_FORMAT, soundCloudUrl);
        final String resolveTrackResp = new NetworkHelper(resolveTrack).getResponse();

        System.out.println("Resolving track : " + resolveTrack);

        if (resolveTrackResp != null) {

            try {
                final JSONObject joResolve = new JSONObject(resolveTrackResp);
                final JSONArray jaTracks = new JSONArray();

                String playlistName = null, username = null, playlistArtworkUrl = null;

                final String fileNameFormat = Preference.getInstance().getString(Preference.KEY_FILENAME_FORMAT);
                final boolean isDirectDownload = Preference.getInstance().getString(Preference.KEY_IS_DIRECT_DOWNLOAD).equals(Preference.TRUE);

                if (joResolve.has("playlist_type")) {

                    playlistName = joResolve.getString("title");
                    username = joResolve.getJSONObject("user").getString("username");

                    if (joResolve.has(KEY_ARTWORK_URL) && !joResolve.isNull(KEY_ARTWORK_URL)) {
                        playlistArtworkUrl = joResolve.getString(KEY_ARTWORK_URL);
                    }

                    //Url was a playlist
                    final JSONArray jaResolvedTracks = joResolve.getJSONArray(JSONTracks.KEY_TRACKS);

                    for (int i = 0; i < jaResolvedTracks.length(); i++) {
                        jaTracks.put(getResolvedTrack(jaResolvedTracks.getJSONObject(i), fileNameFormat, requestId, isDirectDownload));
                    }

                } else {
                    jaTracks.put(getResolvedTrack(joResolve, fileNameFormat, requestId, isDirectDownload));
                }

                return new JSONTracks(playlistName, username, playlistArtworkUrl, jaTracks);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    private static JSONObject getResolvedTrack(JSONObject joResolvedTrack, String fileNameFormat, final String requestId, final boolean isDirectDownload) throws JSONException {

        //Url is a single track
        final String soundCloudTrackId = String.valueOf(joResolvedTrack.getInt("id"));
        final String title = joResolvedTrack.getString("title");
        final String originalFormat = joResolvedTrack.getString("original_format");

        String trackArtworkUrl = null;
        if (joResolvedTrack.has(Tracks.COLUMN_ARTWORK_URL) && !joResolvedTrack.isNull(Tracks.COLUMN_ARTWORK_URL)) {
            trackArtworkUrl = joResolvedTrack.getString(Tracks.COLUMN_ARTWORK_URL);
        }

        final long duration = joResolvedTrack.getLong("duration");
        final String username = joResolvedTrack.getJSONObject("user").getString("username");

        final String soundCloudUrl = joResolvedTrack.getString("permalink_url");
        final String fileName = String.format(fileNameFormat, FileNameUtils.getSanitizedName(title + "_" + soundCloudTrackId), originalFormat);
        final String downloadUrl = isDirectDownload ? String.format(DIRECT_DOWNLOAD_TRACK_URL_FORMAT, soundCloudTrackId, fileName) : String.format(DOWNLOAD_TRACK_URL_FORMAT, soundCloudTrackId);

        final JSONObject joTrack = new JSONObject();
        joTrack.put(Tracks.COLUMN_TITLE, title);
        joTrack.put(Tracks.COLUMN_ORIGINAL_FORMAT, originalFormat);
        joTrack.put(Tracks.COLUMN_FILENAME, fileName);
        joTrack.put(Tracks.COLUMN_ARTWORK_URL, trackArtworkUrl);
        joTrack.put(Tracks.COLUMN_DOWNLOAD_URL, downloadUrl);
        joTrack.put(Tracks.COLUMN_DURATION, duration);
        joTrack.put(Tracks.COLUMN_USERNAME, username);
        joTrack.put(Tracks.COLUMN_SOUNDCLOUD_URL, soundCloudUrl);

        //Adding the track to table
        final Track track = new Track(null, requestId, soundCloudUrl, soundCloudTrackId, title, username, downloadUrl, trackArtworkUrl, fileName, originalFormat, duration, false);
        return joTrack;
    }

    public static JSONTracks getTracks(String soundCloudUrl) {
        return getSoundCloudTracks(soundCloudUrl);
    }
}
