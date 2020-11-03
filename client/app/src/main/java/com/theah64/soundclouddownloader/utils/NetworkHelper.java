package com.theah64.soundclouddownloader.utils;


import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Shifar Shifz on 10/22/2015.
 */
public class NetworkHelper {

    private final String url;

    public NetworkHelper(final String url) {
        this.url = url;
    }

    @Nullable
    public String getResponse() {

        //System.out.println("->" + url);
        try {

            final URL urlOb = new URL(url);
            final InputStream is = urlOb.openStream();
            final InputStreamReader isr = new InputStreamReader(is);
            final BufferedReader br = new BufferedReader(isr);
            final StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line).append("\n");
            }
            is.close();
            isr.close();
            br.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

