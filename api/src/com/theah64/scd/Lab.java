package com.theah64.scd;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;

/**
 * Created by theapache64 on 8/12/16.
 */
public class Lab {

    public static void main(String[] args) {

        final File f = new File(System.getProperty("user.dir") + File.separator + "afreen_afreen.mp3");
        try {
            Mp3File mp3 = new Mp3File(f.getAbsolutePath());
            ID3v2 id3v2 = mp3.getId3v2Tag();

        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            e.printStackTrace();
        }

    }

}
