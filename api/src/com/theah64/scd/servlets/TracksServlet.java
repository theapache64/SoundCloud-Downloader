package com.theah64.scd.servlets;

import com.theah64.scd.core.SoundCloudDownloader;
import com.theah64.scd.database.tables.BaseTable;
import com.theah64.scd.database.tables.Preference;
import com.theah64.scd.database.tables.Requests;
import com.theah64.scd.database.tables.Tracks;
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
    public boolean isBinaryServlet() {
        return false;
    }

    @Override
    protected boolean isSecureServlet() {
        return !Preference.getInstance().getString(Preference.KEY_IS_OPEN_API).equals(Preference.TRUE);
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
                joTrack.put(Tracks.COLUMN_USERNAME, jTracks.getUsername());

                //Playlist cover
                joTrack.put(Tracks.COLUMN_ARTWORK_URL, jTracks.getArtworkUrl());
            }


            joTrack.put(KEY_TRACKS, jTracks.getJSONArrayTracks());

            getWriter().write(new APIResponse("Request processed", joTrack).getResponse());
        } else {
            getWriter().write(new APIResponse("Invalid soundcloud url").getResponse());
        }
    }

    private JSONTracks getTracks() throws BaseTable.InsertFailedException, JSONException {
        final Preference prefTable = Preference.getInstance();
        final String userId = isSecureServlet() ? getHeaderSecurity().getUserId() : prefTable.getString(Preference.KEY_DEFAULT_USER_ID);
        System.out.println("User id is : " + userId);
        String soundCloudUrl = getStringParameter(Tracks.COLUMN_SOUNDCLOUD_URL).replaceAll("^https", "http");
        final com.theah64.scd.models.Request apiRequest = new com.theah64.scd.models.Request(userId, soundCloudUrl);
        final String requestId = Requests.getInstance().addv3(apiRequest);
        return SoundCloudDownloader.getSoundCloudTracks(requestId, soundCloudUrl);
    }
}