package com.theah64.soundclouddownloader.server;

import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.JSONTracks;
import com.theah64.soundclouddownloader.models.ServerAPIResponse;
import com.theah64.soundclouddownloader.models.ServerTrack;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.utils.FileNameUtils;
import com.theah64.soundclouddownloader.utils.NetworkHelper;
import com.theah64.soundclouddownloader.utils.SecretConstants;
import com.theah64.soundclouddownloader.utils.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by theapache64 on 8/12/16.
 * Track resolve example : https://api.soundcloud.com/resolve.json?url=https://soundcloud.com/theapache64/tomorrowland-2014-ultra-festival-2014-ringtone&client_id=a3e059563d7fd3372b49b37f00a00bcf
 * Playlist resolve example : https://api.soundcloud.com/resolve.json?url=https://soundcloud.com/theapache64/sets/twinkewinkle&client_id=a3e059563d7fd3372b49b37f00a00bcf
 */
public class SoundCloudDownloader {


    private static final String RESOLVE_TRACK_URL_FORMAT = "https://api.soundcloud.com/resolve.json?url=%s&client_id=%s";
    private static final String KEY_ARTWORK_URL = "artwork_url";
    private static final String FILENAME_FORMAT = "%s_scd.%s";

    private static String getResolveTrackUrl(String url, final String clientId) {
        if (url.contains("soundcloud.app.goo.gl")) {
            // redirection url
            url = UrlHelper.INSTANCE.getFinalUrl(url);
        }
        return String.format(RESOLVE_TRACK_URL_FORMAT, url, clientId);
    }

    public static JSONTracks getSoundCloudTracks(final String requestId, String soundCloudUrl) throws JSONException {

        System.out.println("Request url : " + soundCloudUrl);

        //Getting fresh data for playlist
        System.out.println("Getting fresh data");

        final String resolveTrack = getResolveTrackUrl(soundCloudUrl, SecretConstants.API_KEY);
        final String resolveTrackResp = new NetworkHelper(resolveTrack).getResponse();

        System.out.println("Resolving track : " + resolveTrack);
        System.out.println("ResolveTrackResp: " + resolveTrackResp);

        if (resolveTrackResp != null) {

            try {
                final JSONObject joResolve = new JSONObject(resolveTrackResp);
                final JSONArray jaTracks = new JSONArray();

                String playlistName = null, username = null, playlistArtworkUrl = null;


                if (joResolve.has("playlist_type")) {

                    playlistName = joResolve.getString("title");
                    username = joResolve.getJSONObject("user").getString("username");

                    if (joResolve.has(KEY_ARTWORK_URL) && !joResolve.isNull(KEY_ARTWORK_URL)) {
                        playlistArtworkUrl = joResolve.getString(KEY_ARTWORK_URL);
                    }

                    //Url was a playlist
                    final JSONArray jaResolvedTracks = joResolve.getJSONArray(JSONTracks.KEY_TRACKS);

                    for (int i = 0; i < jaResolvedTracks.length(); i++) {
                        jaTracks.put(getResolvedTrack(jaResolvedTracks.getJSONObject(i), FILENAME_FORMAT, requestId));
                    }

                } else {
                    jaTracks.put(getResolvedTrack(joResolve, FILENAME_FORMAT, requestId));
                }

                return new JSONTracks(playlistName, username, playlistArtworkUrl, jaTracks, requestId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    private static final String SOUND_CLOUD_PLAYLIST_REGEX = "^(?:https:\\/\\/|http:\\/\\/|www\\.|)soundcloud\\.com\\/(?:.+)\\/sets\\/(?:.+)$";

    private static boolean isPlaylist(String soundCloudUrl) {
        return soundCloudUrl.matches(SOUND_CLOUD_PLAYLIST_REGEX);
    }


    private static JSONObject getResolvedTrack(JSONObject joResolvedTrack, String fileNameFormat, final String requestId) throws JSONException {

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

        final String soundCloudUrl = joResolvedTrack.getString("permalink_url").replaceAll("^https", "http");
        final String fileName = String.format(fileNameFormat, FileNameUtils.getSanitizedName(title + "_" + soundCloudTrackId), originalFormat);

        final ServerTrack track = new ServerTrack(requestId, soundCloudUrl, soundCloudTrackId, title, username, trackArtworkUrl, fileName, originalFormat, duration);

        return track.toJSONObject();
    }

    public static String toJsonResponse(JSONTracks jTracks) throws JSONException {
        if (jTracks != null) {

            final JSONObject joTrack = new JSONObject();

            if (jTracks.getPlaylistName() != null) {
                joTrack.put(Track.KEY_PLAYLIST_NAME, jTracks.getPlaylistName());
                joTrack.put(Tracks.COLUMN_USERNAME, jTracks.getUsername());

                //Playlist cover
                joTrack.put(Tracks.COLUMN_ARTWORK_URL, jTracks.getArtworkUrl());
            }


            joTrack.put("tracks", jTracks.getJSONArrayTracks());
            joTrack.put("request_id", jTracks.getRequestId());

            return new ServerAPIResponse("Request processed", joTrack).getResponse();
        } else {
            return new ServerAPIResponse("Track unavailable to download.Please try another one.").getResponse();
        }

    }

    private static final String STREAM_TRACK_URL_FORMAT = "https://api.soundcloud.com/i1/tracks/%s/streams?client_id=%s";

    private static String getStreamTrackUrl(final String scTrackId) {
        return String.format(STREAM_TRACK_URL_FORMAT, scTrackId, SecretConstants.API_KEY);
    }

    static String getSoundCloudDownloadUrl(String trackId) {

        final String trackDownloadUrl = getStreamTrackUrl(trackId);
        final String downloadTrackResp = new NetworkHelper(trackDownloadUrl).getResponse();

        if (downloadTrackResp != null) {
            try {
                return new JSONObject(downloadTrackResp).getString("http_mp3_128_url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
