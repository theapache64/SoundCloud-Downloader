package com.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 9/12/16.
 */

public class Lab {

    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f) tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public static void main(String[] args) {
        final String data = "Listen to Stuck In Between by Tre Coast #np on #SoundCloud https://soundcloud.com/trecoast/stuck-in-between";
        final Matcher matcher = urlPattern.matcher(data);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }
}
