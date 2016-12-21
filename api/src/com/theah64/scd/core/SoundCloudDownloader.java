package com.theah64.scd.core;

import com.theah64.scd.database.tables.Preference;
import com.theah64.scd.database.tables.Requests;
import com.theah64.scd.models.JSONTracks;
import com.theah64.scd.models.Track;
import com.theah64.scd.servlets.AdvancedBaseServlet;
import com.theah64.scd.utils.FileNameUtils;
import com.theah64.scd.utils.NetworkHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.theah64.scd.models.Track.KEY_ARTWORK_URL;
import static com.theah64.scd.models.Track.KEY_DOWNLOAD_URL;

/**
 * Created by theapache64 on 8/12/16.
 * Track resolve example : https://api.soundcloud.com/resolve.json?url=https://soundcloud.com/theapache64/tomorrowland-2014-ultra-festival-2014-ringtone&client_id=a3e059563d7fd3372b49b37f00a00bcf
 * Playlist resolve example : https://api.soundcloud.com/resolve.json?url=https://soundcloud.com/theapache64/sets/twinkewinkle&client_id=a3e059563d7fd3372b49b37f00a00bcf
 */
public class SoundCloudDownloader {

    public static final String CLIENT_ID = "a3e059563d7fd3372b49b37f00a00bcf";
    private static final String RESOLVE_TRACK_URL_FORMAT = "https://api.soundcloud.com/resolve.json?url=%s&client_id=" + CLIENT_ID;
    private static final String DOWNLOAD_TRACK_URL_FORMAT = String.format("%s%s/download?id=%%s", AdvancedBaseServlet.getBaseUrl(), AdvancedBaseServlet.VERSION_CODE);

    public static JSONTracks getSoundCloudTracks(String soundCloudUrl) {

        final String resolveTrack = String.format(RESOLVE_TRACK_URL_FORMAT, soundCloudUrl);
        final String resolveTrackResp = new NetworkHelper(resolveTrack).getResponse();

        System.out.println("Resolving track : " + resolveTrack);

        if (resolveTrackResp != null) {

            try {
                final JSONObject joResolve = new JSONObject(resolveTrackResp);
                final JSONArray jaTracks = new JSONArray();

                String playlistName = null, username = null, playlistArtworkUrl = null;

                final String fileNameFormat = Preference.getInstance().getString(Preference.KEY_FILENAME_FORMAT);

                if (joResolve.has("playlist_type")) {

                    playlistName = joResolve.getString("title");
                    username = joResolve.getJSONObject("user").getString("username");

                    if (joResolve.has(KEY_ARTWORK_URL) && !joResolve.isNull(KEY_ARTWORK_URL)) {
                        playlistArtworkUrl = joResolve.getString(KEY_ARTWORK_URL);
                    }

                    //Url was a playlist
                    final JSONArray jaResolvedTracks = joResolve.getJSONArray(JSONTracks.KEY_TRACKS);

                    for (int i = 0; i < jaResolvedTracks.length(); i++) {
                        jaTracks.put(getResolvedTrack(jaResolvedTracks.getJSONObject(i), fileNameFormat));
                    }

                } else {
                    jaTracks.put(getResolvedTrack(joResolve, fileNameFormat));
                }

                return new JSONTracks(playlistName, username, playlistArtworkUrl, jaTracks);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    private static JSONObject getResolvedTrack(JSONObject joResolvedTrack, String fileNameFormat) throws JSONException {

        //Url is a single track
        final String id = String.valueOf(joResolvedTrack.getInt("id"));
        final String title = joResolvedTrack.getString("title");
        final String originalFormat = joResolvedTrack.getString("original_format");

        String trackArtworkUrl = null;
        if (joResolvedTrack.has(KEY_ARTWORK_URL) && !joResolvedTrack.isNull(KEY_ARTWORK_URL)) {
            trackArtworkUrl = joResolvedTrack.getString("artwork_url");
        }

        final long duration = joResolvedTrack.getLong("duration");
        final String username = joResolvedTrack.getJSONObject("user").getString("username");

        final String soundCloudUrl = joResolvedTrack.getString("permalink_url");
        final String fileName = String.format(fileNameFormat, FileNameUtils.getSanitizedName(title), username, originalFormat);

        final JSONObject joTrack = new JSONObject();
        joTrack.put(Track.KEY_TITLE, title);
        joTrack.put(Track.KEY_ORIGINAL_FORMAT, originalFormat);
        joTrack.put(Track.KEY_FILENAME, fileName);
        joTrack.put(KEY_ARTWORK_URL, trackArtworkUrl);
        joTrack.put(KEY_DOWNLOAD_URL, String.format(DOWNLOAD_TRACK_URL_FORMAT, id));
        joTrack.put("duration", duration);
        joTrack.put("username", username);
        joTrack.put(Requests.COLUMN_SOUND_CLOUD_URL, soundCloudUrl);

        return joTrack;
    }
}
