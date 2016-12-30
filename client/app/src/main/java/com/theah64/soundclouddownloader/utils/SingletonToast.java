package com.theah64.soundclouddownloader.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by theapache64 on 29/12/16.
 */

public class SingletonToast {

    private static Toast toast;

    public static Toast makeText(final Context context, @StringRes final int message) {
        return makeText(context, context.getString(message));
    }

    public static Toast makeText(final Context context, final String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        }

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(message);
        return toast;
    }


}
