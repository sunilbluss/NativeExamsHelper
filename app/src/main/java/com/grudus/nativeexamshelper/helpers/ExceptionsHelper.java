package com.grudus.nativeexamshelper.helpers;


import android.support.annotation.Nullable;
import android.util.Log;

public class ExceptionsHelper {

    private static final String TAG = "@@@ExceptionsHelper";

    public static boolean stringsAreEmpty(String... strings) {
        for (String string : strings) {
            if (string == null) {
                Log.e(TAG, "stringsAreEmpty: ", new EmptyStringException("null"));
                return true;
            }
            if (string.replaceAll("\\s+", "").isEmpty()) {
                Log.e(TAG, "stringsAreEmpty: ", new EmptyStringException("empty"));
                return true;
            }
        }
        return false;
    }


    public static class EmptyStringException extends IllegalArgumentException {

        public static final String DEFAULT_MESSAGE = "String argument cannot be empty";

        public EmptyStringException(String detailMessage) {
            super(detailMessage);
        }

        public EmptyStringException() {
            this(DEFAULT_MESSAGE);
        }
    }

}
