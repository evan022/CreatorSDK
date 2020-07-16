package com.nibiru.creator.utils;

import android.util.Log;

public class Logger {
    private static final String TAG = "Creator";
    private static final boolean debug = true;

    public static void e(String msg) {
        if (debug) {
            Log.e(TAG, msg);
        }
    }
}
