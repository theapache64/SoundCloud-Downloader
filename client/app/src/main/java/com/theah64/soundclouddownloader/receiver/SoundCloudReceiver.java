package com.theah64.soundclouddownloader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SoundCloudReceiver extends BroadcastReceiver {

    private static final String X = SoundCloudReceiver.class.getSimpleName();

    public SoundCloudReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(X, "Received: " + intent);
    }
}
