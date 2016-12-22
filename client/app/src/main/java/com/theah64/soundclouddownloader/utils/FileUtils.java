package com.theah64.soundclouddownloader.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by shifar on 11/5/16.
 */
public class FileUtils {
    /**
     * Used to read text assets in the assets directory.
     */
    public static String readTextualAsset(final Context context, String filename) throws IOException {

        final InputStream is = context.getAssets().open(filename);
        final InputStreamReader isr = new InputStreamReader(is);
        final BufferedReader br = new BufferedReader(isr);
        final StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        is.close();
        br.close();
        isr.close();

        return sb.toString();
    }

}
