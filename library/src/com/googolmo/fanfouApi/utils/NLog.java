package com.googolmo.fanfouApi.utils;

import android.util.Log;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午2:42
 */
public class NLog {
    private static boolean isDebug = false;

    public static void d(String tag, String msg) {
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            Log.e(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isDebug)
            Log.w(tag, msg);
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }
}
