package com.theah64.scd.servlets;

import com.theah64.scd.database.tables.BaseTable;
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

import static com.theah64.scd.servlets.DownloaderServlet.getSoundCloudDownloadUrl;

/**
 * Created by theapache64 on 8/12/16.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/direct_download"})
public class DirectDownloaderServlet extends AdvancedBaseServlet {

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
        return new String[]{Track.KEY_ID, Track.KEY_FILENAME};
    }

    @Override
    protected void doAdvancedPost() throws BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException, Request.RequestException {

        final HttpServletResponse response = super.getHttpServletResponse();

        System.out.println("Track found");

        final String trackId = getStringParameter(Track.KEY_ID);
        final String downloadUrl = getSoundCloudDownloadUrl(trackId);

        if (downloadUrl != null) {

            final String fileName = getStringParameter(Track.KEY_FILENAME);

            response.setContentType("audio/mpeg");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);

            BufferedInputStream bis = null;
            ServletOutputStream sos = null;

            try {
                final URL url = new URL(downloadUrl);
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

                try {
                    response.setContentType(CONTENT_TYPE_JSON);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write(new APIResponse("Service unavailable").getResponse());
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
            response.setContentType(CONTENT_TYPE_JSON);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new Request.RequestException("Invalid track id");
        }

    }
}