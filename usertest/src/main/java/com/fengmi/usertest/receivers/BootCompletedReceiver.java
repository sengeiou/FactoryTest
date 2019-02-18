package com.fengmi.usertest.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "UserTestReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //Log.d(TAG, "action == " + Intent.ACTION_BOOT_COMPLETED);
        Log.d(TAG, "action == " + Intent.ACTION_BOOT_COMPLETED);

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Log.d(TAG, "action == " + Intent.ACTION_BOOT_COMPLETED);
        }
    }
}
