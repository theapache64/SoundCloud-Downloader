package com.theah64.scd.servlets;

import com.theah64.scd.database.tables.BaseTable;
import com.theah64.scd.utils.APIResponse;
import com.theah64.scd.utils.HeaderSecurity;
import com.theah64.scd.utils.Request;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by shifar on 16/9/16.
 */
public abstract class AdvancedBaseServlet extends HttpServlet {

    public static final String VERSION_CODE = "/v1";
    protected static final String CONTENT_TYPE_JSON = "application/json";
    private static final String ERROR_GET_NOT_SUPPORTED = "GET method not supported";
    private static final String ERROR_POST_NOT_SUPPORTED = "POST method not supported";
    private Request request;
    private HeaderSecurity hs;
    private PrintWriter out;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;

    protected static void setGETMethodNotSupported(HttpServletResponse response) throws IOException {
        notSupported(ERROR_GET_NOT_SUPPORTED, response);
    }

    protected static void POSTMethodNotSupported(HttpServletResponse response) throws IOException {
        notSupported(ERROR_POST_NOT_SUPPORTED, response);
    }

    private static void notSupported(String methodErrorMessage, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE_JSON);
        final PrintWriter out = response.getWriter();

        //GET Method not supported
        out.write(new APIResponse(methodErrorMessage).getResponse());
    }

    public PrintWriter getWriter() {
        return out;
    }

    public abstract boolean isBinaryServlet();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isBinaryServlet()) {
            resp.setContentType(getContentType());
        }

        this.httpServletRequest = req;
        this.httpServletResponse = resp;

        if (!isBinaryServlet()) {
            out = resp.getWriter();
        }

        try {
            if (isSecureServlet()) {
                hs = new HeaderSecurity(req.getHeader(HeaderSecurity.KEY_AUTHORIZATION));
            }

            if (getRequiredParameters() != null) {
                request = new Request(req, getRequiredParameters());
            }

            doAdvancedPost();

        } catch (Exception e) {
            e.printStackTrace();
            out.write(new APIResponse(e.getMessage()).toString());
        }
    }

    protected String getContentType() {
        return CONTENT_TYPE_JSON;
    }

    protected abstract boolean isSecureServlet();

    protected abstract String[] getRequiredParameters();

    protected abstract void doAdvancedPost() throws BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException, Request.RequestException, IOException;

    public HeaderSecurity getHeaderSecurity() {
        if (!isSecureServlet()) {
            throw new IllegalArgumentException("It's not a secure servlet");
        }
        return hs;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setGETMethodNotSupported(resp);
    }

    public String getStringParameter(String key) {
        return request.getStringParameter(key);
    }


    public boolean getBooleanParameter(String key) {
        return request.getBooleanParameter(key);
    }

    public boolean has(String key) {
        return request.has(key);
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public long getLongParameter(String key) {
        return request.getLongParameter(key);
    }

    public int getIntParameter(String key, int defaultValue) {
        final String value = getStringParameter(key);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }
}
