package com.theah64.scd.servlets;

import com.theah64.scd.database.tables.BaseTable;
import com.theah64.scd.database.tables.Preference;
import com.theah64.scd.models.Track;
import com.theah64.scd.utils.NetworkHelper;
import com.theah64.scd.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.theah64.scd.core.SoundCloudDownloader.CLIENT_ID;

/**
 * Created by theapache64 on 8/12/16.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/download"})
public class DownloaderServlet extends AdvancedBaseServlet {

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
        return false;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{Track.KEY_ID};
    }

    @Override
    protected void doAdvancedPost() throws BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException, Request.RequestException, IOException {

        final String trackId = getStringParameter(Track.KEY_ID);
        final String downloadUrl = getSoundCloudDownloadUrl(trackId);
        if (downloadUrl != null) {
            final HttpServletResponse response = super.getHttpServletResponse();
            response.sendRedirect(downloadUrl);
        } else {
            throw new Request.RequestException("Invalid track id");
        }

    }

    private static final String STREAM_TRACK_URL_FORMAT = "https://api.soundcloud.com/i1/tracks/%s/streams?client_id=" + CLIENT_ID;

    static String getSoundCloudDownloadUrl(String trackId) {

        if (Preference.getInstance().getString(Preference.KEY_IS_DEBUG_DOWNLOAD).equals(Preference.TRUE)) {
            return AdvancedBaseServlet.getBaseUrl() + "/jaan_kesi.mp3";
        }

        final String trackDownloadUrl = String.format(STREAM_TRACK_URL_FORMAT, trackId);
        final String downloadTrackResp = new NetworkHelper(trackDownloadUrl).getResponse();

        System.out.println("Track download url : " + trackDownloadUrl);

        if (downloadTrackResp != null) {
            try {
                final String trackUrl = new JSONObject(downloadTrackResp).getString("http_mp3_128_url");
                System.out.println("TRACK: " + trackUrl);
                return trackUrl;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
