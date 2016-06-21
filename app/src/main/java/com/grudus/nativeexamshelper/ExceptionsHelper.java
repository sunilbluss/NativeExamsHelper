package com.grudus.nativeexamshelper;


import android.util.Log;

public class ExceptionsHelper {

    public static final String TAG = "_______-------_______";

    public static void printError(Exception e) {
        for (StackTraceElement s : e.getStackTrace())
            Log.e(TAG, s.toString());
    }

}
