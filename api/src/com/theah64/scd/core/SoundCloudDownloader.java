package com.theah64.scd.core;

import com.theah64.scd.database.tables.BaseTable;
import com.theah64.scd.database.tables.Preference;
import com.theah64.scd.database.tables.Tracks;
import com.theah64.scd.models.JSONTracks;
import com.theah64.scd.models.Track;
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
    private static final String KEY_ARTWORK_URL = "artwork_url";

    public static JSONTracks getSoundCloudTracks(final String requestId, String soundCloudUrl) throws JSONException {

        System.out.println("Request url : " + soundCloudUrl);
        if (!isPlaylist(soundCloudUrl)) {
            //It's a track , so checking the data availability in db.

            System.out.println("It's not a playlist");

            //It's track
            final Track track = Tracks.getInstance().get(Tracks.COLUMN_SOUNDCLOUD_URL, soundCloudUrl);

            if (track != null) {
                System.out.println("Getting data from cache");
                final JSONArray jaTracks = new JSONArray();
                jaTracks.put(track.toJSONObject());
                return new JSONTracks(null, null, null, jaTracks, requestId);
            }
        }

        //Getting fresh data for playlist
        System.out.println("Getting fresh data");

        final String resolveTrack = String.format(RESOLVE_TRACK_URL_FORMAT, soundCloudUrl);
        final String resolveTrackResp = new NetworkHelper(resolveTrack).getResponse();

        System.out.println("Resolving track : " + resolveTrack);
        System.out.println("ResolveTrackResp: " + resolveTrackResp);

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
                        jaTracks.put(getResolvedTrack(jaResolvedTracks.getJSONObject(i), fileNameFormat, requestId));
                    }

                } else {
                    jaTracks.put(getResolvedTrack(joResolve, fileNameFormat, requestId));
                }

                return new JSONTracks(playlistName, username, playlistArtworkUrl, jaTracks, requestId);

            } catch (JSONException | BaseTable.InsertFailedException e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    private static final String SOUND_CLOUD_PLAYLIST_REGEX = "^(?:https:\\/\\/|http:\\/\\/|www\\.|)soundcloud\\.com\\/(?:.+)\\/sets\\/(?:.+)$";

    private static boolean isPlaylist(String soundCloudUrl) {
        return soundCloudUrl.matches(SOUND_CLOUD_PLAYLIST_REGEX);
    }


    private static JSONObject getResolvedTrack(JSONObject joResolvedTrack, String fileNameFormat, final String requestId) throws JSONException, BaseTable.InsertFailedException {

        //Url is a single track
        final String soundCloudTrackId = String.valueOf(joResolvedTrack.getInt("id"));
        final Tracks tracksTable = Tracks.getInstance();

        Track track = tracksTable.get(Tracks.COLUMN_SOUNDCLOUD_TRACK_ID, soundCloudTrackId);

        if (track == null) {

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

            track = new Track(null, requestId, soundCloudUrl, soundCloudTrackId, title, username, trackArtworkUrl, fileName, originalFormat, duration);

            //New track
            final String trackId = tracksTable.addv3(track);
            track.setId(trackId);
        }

        return track.toJSONObject();
    }
}
