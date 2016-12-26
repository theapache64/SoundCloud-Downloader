package com.theah64.scd.servlets;


import com.theah64.scd.database.tables.BaseTable;
import com.theah64.scd.database.tables.Users;
import com.theah64.scd.models.User;
import com.theah64.scd.utils.APIResponse;
import com.theah64.scd.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by Shifar Shifz on 10/18/2015.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/in"})
public class INServlet extends AdvancedBaseServlet {

    //Used to generate a new user
    private static final int API_KEY_LENGTH = 10;
    private static final String apiEngine = "0123456789AaBbCcDdEeFfGgHhIiJjKkLkMmNnOoPpQqRrSsTtUuVvWwXxYyZ";
    private static Random random;

    private static String getNewApiKey() {
        if (random == null) {
            random = new Random();
        }
        final StringBuilder apiKeyBuilder = new StringBuilder();
        for (int i = 0; i < API_KEY_LENGTH; i++) {
            apiKeyBuilder.append(apiEngine.charAt(random.nextInt(apiEngine.length())));
        }
        return apiKeyBuilder.toString();
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
        return new String[]{Users.COLUMN_DEVICE_HASH, Users.COLUMN_IMEI, Users.COLUMN_NAME};
    }

    @Override
    protected void doAdvancedPost() throws BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException, Request.RequestException, IOException {

        final PrintWriter out = getWriter();

        final String deviceHash = getStringParameter(Users.COLUMN_DEVICE_HASH);

        //Checking if any account exist with the requested imei
        final Users users = Users.getInstance();
        User user = users.get(Users.COLUMN_DEVICE_HASH, deviceHash);

        if (user != null && !user.isActive()) {
            throw new Request.RequestException("You're banned from using SoundCloud Downloader API");
        }

        final boolean isAlreadyExist = user != null;

        if (!isAlreadyExist) {
            //Account not exists, so creating new one
            final String name = getStringParameter(Users.COLUMN_NAME);
            final String imei = getStringParameter(Users.COLUMN_IMEI);
            user = new User(name, name, imei, getNewApiKey(), deviceHash, false);
            users.add(user);
        }

        final String message = isAlreadyExist ? "Welcome back!" : "Welcome!";

        //At this point, we've a user - no matter new or old
        out.write(new APIResponse(message, Users.COLUMN_API_KEY, user.getApiKey()).getResponse());
    }
}
