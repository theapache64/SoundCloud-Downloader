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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.theah64.scd.core.SoundCloudDownloader.CLIENT_ID;

/**
 * Created by theapache64 on 8/12/16.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/download"})
public class DownloaderServlet extends HttpServlet {

    private static final String[] REQUIRED_PARAMS = {Track.KEY_ID};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("------------------------------");

        try {
            final Request request = new Request(req, REQUIRED_PARAMS);

            final String trackId = request.getStringParameter(Track.KEY_ID);
            System.out.println("Handing track : " + trackId);
            final String downloadUrl = getSoundCloudDownloadUrl(trackId);
            System.out.println("Track download url : " + downloadUrl);
            if (downloadUrl != null) {
                resp.sendRedirect(downloadUrl);
                System.out.println("Track redirected to download url");
            } else {
                throw new Request.RequestException("Invalid track id");
            }

        } catch (Request.RequestException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }

        System.out.println("------------------------------");
    }


    private static final String STREAM_TRACK_URL_FORMAT = "https://api.soundcloud.com/i1/tracks/%s/streams?client_id=" + CLIENT_ID;

    static String getSoundCloudDownloadUrl(String trackId) {

        if (Preference.getInstance().getString(Preference.KEY_IS_DEBUG_DOWNLOAD).equals(Preference.TRUE)) {
            return AdvancedBaseServlet.getBaseUrl() + "/jaan_kesi.mp3";
        }

        final String trackDownloadUrl = String.format(STREAM_TRACK_URL_FORMAT, trackId);
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
