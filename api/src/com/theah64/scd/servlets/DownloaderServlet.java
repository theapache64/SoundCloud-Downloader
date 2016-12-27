package com.theah64.scd.servlets;

import com.theah64.scd.database.tables.*;
import com.theah64.scd.models.DownloadRequest;
import com.theah64.scd.models.Track;
import com.theah64.scd.utils.HeaderSecurity;
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
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + DownloaderServlet.ROUTE})
public class DownloaderServlet extends HttpServlet {

    private static final String[] REQUIRED_PARAMS = {Users.COLUMN_API_KEY, DownloadRequests.COLUMN_REQUEST_ID, Tracks.COLUMN_ID};
    public static final String ROUTE = "/download";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("------------------------------");

        try {
            final Request request = new Request(req, REQUIRED_PARAMS);
            final String apiKey = request.getStringParameter(Users.COLUMN_API_KEY);
            final HeaderSecurity hs = new HeaderSecurity(apiKey);

            final String id = request.getStringParameter(Tracks.COLUMN_ID);
            final Tracks tracksTable = Tracks.getInstance();
            final Track track = tracksTable.get(Tracks.COLUMN_ID, id);

            if (track != null) {

                System.out.println("Handling track : " + track.getTitle());
                final String downloadUrl = getSoundCloudDownloadUrl(track.getSoundcloudTrackId());

                System.out.println("Track download url : " + downloadUrl);

                if (downloadUrl != null) {
                    final String requestId = request.getStringParameter(DownloadRequests.COLUMN_REQUEST_ID);
                    DownloadRequests.getInstance().add(new DownloadRequest(null, track.getId(), requestId, downloadUrl));
                    resp.sendRedirect(downloadUrl);
                    System.out.println("Track redirected to download url");
                } else {
                    tracksTable.delete(Tracks.COLUMN_ID, track.getId());
                    throw new Request.RequestException("Track deleted from soundcloud");
                }

            } else {
                throw new Request.RequestException("Invalid track id");
            }

        } catch (Request.RequestException | BaseTable.InsertFailedException e) {
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
