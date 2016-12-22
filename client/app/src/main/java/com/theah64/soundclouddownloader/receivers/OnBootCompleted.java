package com.theah64.soundclouddownloader.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.theah64.soundclouddownloader.services.ClipboardWatchIgniterService;
import com.theah64.soundclouddownloader.utils.CommonUtils;

public class OnBootCompleted extends BroadcastReceiver {

    private static final String X = OnBootCompleted.class.getSimpleName();

    public OnBootCompleted() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(X, "Boot finished");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !CommonUtils.isMyServiceRunning(context, ClipboardWatchIgniterService.class)) {
            //Supports clipboard listener
            context.startService(new Intent(context, ClipboardWatchIgniterService.class));
        }
    }
}
