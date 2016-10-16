package com.misakimei.accelerate.tool;


import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Log统一管理类
 */
public class L {

    private static final String TAG = "====>";
    public static boolean isDebug = true;

    private L() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void d(@NonNull Object obj) {
        L.d(TAG, obj.toString());
    }

    public static void d(String tag, @NonNull Object msg) {
        if (isDebug) {
            Log.d(tag, msg.toString());
        }
    }


}