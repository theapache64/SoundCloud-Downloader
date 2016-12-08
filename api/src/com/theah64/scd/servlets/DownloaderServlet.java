package com.theah64.scd.servlets;

import com.theah64.scd.core.SoundCloudDownloader;
import com.theah64.scd.database.tables.BaseTable;
import com.theah64.scd.database.tables.Preference;
import com.theah64.scd.database.tables.Requests;
import com.theah64.scd.database.tables.Users;
import com.theah64.scd.utils.APIResponse;
import com.theah64.scd.utils.Request;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by theapache64 on 8/12/16.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/scd"})
public class DownloaderServlet extends AdvancedBaseServlet {

    private static final String KEY_DOWNLOAD_URL = "download_url";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
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
    protected void doAdvancedPost() throws BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException, Request.RequestException, IOException {

        final String userId = isSecureServlet() ? getHeaderSecurity().getUserId() : Preference.getInstance().getString(Preference.KEY_DEFAULT_USER_ID);
        final String soundCloudUrl = getStringParameter(Requests.COLUMN_SOUND_CLOUD_URL);
        final com.theah64.scd.models.Request apiRequest = new com.theah64.scd.models.Request(userId, soundCloudUrl);
        Requests.getInstance().add(apiRequest);
        final String downloadUrl = SoundCloudDownloader.getDownloadUrl(soundCloudUrl);

        if (downloadUrl != null) {
            getWriter().write(new APIResponse("Request processed", KEY_DOWNLOAD_URL, downloadUrl).getResponse());
        } else {
            getWriter().write(new APIResponse("Invalid soundcloud url").getResponse());
        }
    }
}