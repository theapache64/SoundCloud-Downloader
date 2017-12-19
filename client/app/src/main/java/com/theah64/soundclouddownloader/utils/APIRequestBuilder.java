package com.theah64.soundclouddownloader.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.theah64.bugmailer.core.BugMailerNode;
import com.theah64.bugmailer.core.NodeBuilder;
import com.theah64.bugmailer.models.Node;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by shifar on 29/7/16.
 * Utility class to create API request object.
 */
public class APIRequestBuilder implements BugMailerNode {

    public static final String BASE_URL = App.IS_DEBUG_MODE ? "http://192.168.0.107:8080/v1" : "http://theapache64.com/scd/v1";

    private static final String X = APIRequestBuilder.class.getSimpleName();
    private static final String CURL_DATA_KEY = " --data \"";
    private static final String KEY_AUTHORIZATION = "Authorization";


    private final Request.Builder requestBuilder = new Request.Builder();
    private final StringBuilder logBuilder = new StringBuilder();
    private final StringBuilder curlBuilder = new StringBuilder();

    private final String url;
    private FormBody.Builder params = new FormBody.Builder();


    public APIRequestBuilder(String route, @Nullable final String apiKey) {

        this.url = BASE_URL + route;
        appendLog("URL", url);

        curlBuilder.append("curl ").append(url);

        if (apiKey != null) {
            requestBuilder.addHeader(KEY_AUTHORIZATION, apiKey);
            appendLog(KEY_AUTHORIZATION, apiKey);
            curlBuilder.append(String.format(" --header \"%s: %s\" ", KEY_AUTHORIZATION, apiKey));
        }

        logBuilder.append("--------------------------------\n");
    }


    private static boolean isReady(String route) {
        /*if (route.equals("/get_latest_version_details")) {
            return false;
        }*/
        //All routes are ready
        return true;
    }

    private void appendLog(String key, String value) {
        logBuilder.append(String.format("%s:%s\n", key, value));
    }

    private APIRequestBuilder addParam(final boolean isAllowNull, final String key, String value) {

        if (isAllowNull) {
            this.params.add(key, value);
            appendLog(key, value);
            addParamToCurlBuilder(key, value);
        } else {

            //value must not be null.
            if (value != null) {
                this.params.add(key, value);
                addParamToCurlBuilder(key, value);
                appendLog(key, value);
            }
        }

        return this;
    }

    private void addParamToCurlBuilder(String key, String value) {
        if (curlBuilder.indexOf(CURL_DATA_KEY) == -1) {
            curlBuilder.append(CURL_DATA_KEY);
        }
        curlBuilder.append(key).append("=").append(value).append("&");
    }

    public APIRequestBuilder addParam(final String key, final String value) {
        return addParam(true, key, value);
    }

    public APIRequestBuilder addParam(final String key, final int value) {
        return addParam(true, key, String.valueOf(value));
    }

    public APIRequestBuilder addParam(final String key, final boolean value) {
        return addParam(true, key, value ? "1" : "0");
    }

    /**
     * Used to build the OkHttpRequest.
     */
    public Request build() {

        logBuilder.append("CURL: ").append(getCompleteCurlCommand());
        logBuilder.append("\nCURL (ubuntu): ").append(getCompleteCurlCommand()).append(" | jq '.'");
        logBuilder.append("\n--------------------------------\n");

        requestBuilder
                .url(url)
                .post(params.build());

        Log.d(X, "Request : " + logBuilder.toString());

        return requestBuilder.build();
    }

    public APIRequestBuilder addOptionalParam(String key, String value) {
        return addParam(false, key, value);
    }

    @Override
    public List<Node> getNodes() {

        return new NodeBuilder()
                .add("API Request", logBuilder.toString())
                .add("cURL command", getCompleteCurlCommand())
                .add("cURL command (ubuntu-jq)", getCompleteCurlCommand() + " | jq '.'")
                .build();
    }

    private String getCompleteCurlCommand() {
        final String curlCommand = curlBuilder.toString();
        return curlCommand.contains(CURL_DATA_KEY) && !curlCommand.endsWith("\"") ? curlBuilder.toString() + "\"" : curlCommand;
    }
}
