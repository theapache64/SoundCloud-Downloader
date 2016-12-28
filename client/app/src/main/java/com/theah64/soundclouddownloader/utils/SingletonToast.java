package com.theah64.soundclouddownloader.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by theapache64 on 29/12/16.
 */

public class SingletonToast {

    private static Toast toast;

    public static Toast makeText(final Context context, @StringRes final int message, final int duration) {
        return makeText(context, context.getString(message), duration);
    }

    public static Toast makeText(final Context context, final String message, final int duration) {

        if (toast == null) {
            toast = SingletonToast.makeText(context.getApplicationContext(), "", Toast.LENGTH_LONG);
        }

        toast.cancel();
        toast.setDuration(duration);
        toast.setText(message);
        return toast;
    }


}
