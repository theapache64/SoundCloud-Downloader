package com.theah64.scd.servlets;

import com.theah64.scd.database.tables.BaseTable;
import com.theah64.scd.database.tables.Tracks;
import com.theah64.scd.models.Track;
import com.theah64.scd.utils.APIResponse;
import com.theah64.scd.utils.Request;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static com.theah64.scd.servlets.DirectDownloaderServlet.ROUTE;
import static com.theah64.scd.servlets.DownloaderServlet.getSoundCloudDownloadUrl;

/**
 * Created by theapache64 on 8/12/16.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + ROUTE})
public class DirectDownloaderServlet extends AdvancedBaseServlet {

    public static final String ROUTE = "/direct_download";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public boolean isBinaryServlet() {
        return true;
    }


    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{Tracks.COLUMN_ID};
    }

    @Override
    protected void doAdvancedPost() throws BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException, Request.RequestException {

        final HttpServletResponse response = super.getHttpServletResponse();

        System.out.println("Track found");

        final String trackId = getStringParameter(Tracks.COLUMN_ID);
        final Tracks tracksTable = Tracks.getInstance();
        final Track track = tracksTable.get(Tracks.COLUMN_ID, trackId);

        if (track != null) {

            final String soundcloudDownloadUrl = getSoundCloudDownloadUrl(track.getSoundcloudTrackId());

            if (soundcloudDownloadUrl != null) {

                BufferedInputStream bis = null;
                ServletOutputStream sos = null;

                try {
                    final URL url = new URL(soundcloudDownloadUrl);
                    final URLConnection con = url.openConnection();
                    response.setContentLength((int) con.getContentLength());

                    bis = new BufferedInputStream(url.openStream());
                    sos = response.getOutputStream();

                    int readBytes = 0;
                    //read from the file; write to the ServletOutputStream
                    while ((readBytes = bis.read()) != -1) {
                        sos.write(readBytes);
                    }

                    response.setContentType("audio/mpeg");
                    response.addHeader("Content-Disposition", "attachment; filename=" + track.getFilename());

                } catch (IOException e) {
                    e.printStackTrace();

                    System.out.println("Couldn't find track");

                    response.setContentType(CONTENT_TYPE_JSON);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    throw new Request.RequestException("Service unavailable");

                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (sos != null) {
                        try {
                            sos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else {
                //Track deleted from soundcluod.com, so set the is_deleted flag to true.
                tracksTable.update(Tracks.COLUMN_ID, track.getId(), Tracks.COLUMN_IS_DELETED, Tracks.TRUE);

                response.setContentType(CONTENT_TYPE_JSON);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                throw new Request.RequestException("Track deleted from soundcloud");
            }


        } else {
            response.setContentType(CONTENT_TYPE_JSON);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new Request.RequestException("Invalid track id");
        }

    }
}