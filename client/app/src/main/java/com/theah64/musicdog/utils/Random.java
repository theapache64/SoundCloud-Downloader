package com.theah64.musicdog.utils;

/**
 * Created by theapache64 on 9/12/16.
 */

public class Random {
    private static final int MIN = 0;
    private static final int MAX = 1000;
    private static final java.util.Random random = new java.util.Random();

    public static int getRandomInt() {
        return random.nextInt(MAX - MIN + 1) + MIN;
    }
}
