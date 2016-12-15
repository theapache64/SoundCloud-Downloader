package com.theah64.scd.utils;

/**
 * Created by theapache64 on 15/12/16.
 */
public class FileNameUtils {
    public static String getSanitizedName(final String fileName) {
        return fileName.replaceAll("[^\\w]", "_").replaceAll("[_]{2,}", "_");
    }
}
