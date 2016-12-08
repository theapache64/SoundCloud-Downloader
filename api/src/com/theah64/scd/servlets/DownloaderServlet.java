package com.theah64.scd.servlets;

import com.theah64.scd.core.SoundCloudDownloader;
import com.theah64.scd.database.tables.BaseTable;
import com.theah64.scd.database.tables.Preference;
import com.theah64.scd.database.tables.Requests;
import com.theah64.scd.models.Track;
import com.theah64.scd.utils.APIResponse;
import com.theah64.scd.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by theapache64 on 8/12/16.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/scd"})
public class DownloaderServlet extends AdvancedBaseServlet {

    private static final String KEY_DOWNLOAD_URL = "download_url";
    private static final String KEY_NAME = "name";

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
        return new String[]{Requests.COLUMN_SOUND_CLOUD_URL};
    }

    @Override
    protected void doAdvancedPost() throws BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException, Request.RequestException {

        final String userId = isSecureServlet() ? getHeaderSecurity().getUserId() : Preference.getInstance().getString(Preference.KEY_DEFAULT_USER_ID);
        final String soundCloudUrl = getStringParameter(Requests.COLUMN_SOUND_CLOUD_URL);
        final com.theah64.scd.models.Request apiRequest = new com.theah64.scd.models.Request(userId, soundCloudUrl);
        Requests.getInstance().add(apiRequest);
        final Track track = SoundCloudDownloader.getTrack(soundCloudUrl);

        final HttpServletResponse response = super.getHttpServletResponse();

        if (track != null) {

            System.out.println("Track found");

            response.setContentType("audio/mpeg");
            response.addHeader("Content-Disposition", "attachment; filename=" + track.getName() + ".mp3");

            BufferedInputStream bis = null;
            ServletOutputStream sos = null;

            try {
                final URL url = new URL(track.getDownloadUrl());
                final URLConnection con = url.openConnection();
                response.setContentLength((int) con.getContentLength());

                bis = new BufferedInputStream(url.openStream());
                sos = response.getOutputStream();

                int readBytes = 0;
                //read from the file; write to the ServletOutputStream
                while ((readBytes = bis.read()) != -1) {
                    sos.write(readBytes);
                }

            } catch (IOException e) {
                e.printStackTrace();

                System.out.println("Couldn't find track");
                response.setContentType(CONTENT_TYPE_JSON);
                try {
                    response.getWriter().write(new APIResponse("Invalid soundcloud url").getResponse());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

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
            System.out.println("Couldn't find track");
            response.setContentType(CONTENT_TYPE_JSON);
            try {
                response.getWriter().write(new APIResponse("Invalid soundcloud url").getResponse());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
