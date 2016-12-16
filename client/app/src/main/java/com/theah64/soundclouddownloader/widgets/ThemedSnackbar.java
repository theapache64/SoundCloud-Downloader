package com.theah64.soundclouddownloader.widgets;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.theah64.soundclouddownloader.R;

/**
 * Created by theapache64 on 16/12/16.
 */

public class ThemedSnackbar {

    public static Snackbar make(final Context context, View view, int message, int length) {

        Snackbar snackbar = Snackbar.make(view, message, length);
        final View snackLayout = snackbar.getView();
        final TextView textView = (TextView) snackLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackbar.setActionTextColor(context.getColor(R.color.deep_orange_500));
        } else {
            //noinspection deprecation
            snackbar.setActionTextColor(context.getResources().getColor(R.color.deep_orange_500));
        }

        return snackbar;
    }
}
