package com.theah64.scd.servlets;

import com.theah64.scd.core.OldSoundCloudDownloader;
import com.theah64.scd.core.SoundCloudDownloader;
import com.theah64.scd.database.tables.BaseTable;
import com.theah64.scd.database.tables.Preference;
import com.theah64.scd.database.tables.Requests;
import com.theah64.scd.models.JSONTracks;
import com.theah64.scd.models.Track;
import com.theah64.scd.utils.APIResponse;
import com.theah64.scd.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by theapache64 on 8/12/16.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/json"})
public final class TracksServlet extends AdvancedBaseServlet {

    private static final String KEY_TRACKS = "tracks";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public boolean isBinaryServlet() {
        return false;
    }

    @Override
    protected boolean isSecureServlet() {
        //ONE DAY..it'll change to true. Buhahahah
        return false;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{Requests.COLUMN_SOUND_CLOUD_URL};
    }

    @Override
    protected void doAdvancedPost() throws BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException, Request.RequestException, IOException {

        final JSONTracks jTracks = getTracks();

        if (jTracks != null) {

            final JSONObject joTrack = new JSONObject();

            if (jTracks.getPlaylistName() != null) {
                joTrack.put(Track.KEY_PLAYLIST_NAME, jTracks.getPlaylistName());
                joTrack.put(Track.KEY_USERNAME, jTracks.getUsername());

                //Playlist cover
                joTrack.put(Track.KEY_ARTWORK_URL, jTracks.getArtworkUrl());
            }


            joTrack.put(KEY_TRACKS, jTracks.getJSONArrayTracks());

            getWriter().write(new APIResponse("Request processed", joTrack).getResponse());
        } else {
            getWriter().write(new APIResponse("Invalid soundcloud url").getResponse());
        }
    }

    private JSONTracks getTracks() throws BaseTable.InsertFailedException {
        final Preference prefTable = Preference.getInstance();
        final String userId = isSecureServlet() ? getHeaderSecurity().getUserId() : prefTable.getString(Preference.KEY_DEFAULT_USER_ID);
        final String soundCloudUrl = getStringParameter(Requests.COLUMN_SOUND_CLOUD_URL);
        final com.theah64.scd.models.Request apiRequest = new com.theah64.scd.models.Request(userId, soundCloudUrl);
        Requests.getInstance().add(apiRequest);
        return prefTable.getString(Preference.KEY_IS_NEW_SOUNDCLOUD_DOWNLOADER).equals(Preference.TRUE) ? SoundCloudDownloader.getTracks(soundCloudUrl) : OldSoundCloudDownloader.getTracks(soundCloudUrl);
    }
}