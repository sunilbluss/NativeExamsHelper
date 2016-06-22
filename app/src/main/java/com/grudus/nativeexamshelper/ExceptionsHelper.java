package com.grudus.nativeexamshelper;


import android.support.annotation.Nullable;
import android.util.Log;

public class ExceptionsHelper {

    public static void checkStringEmptiness(@Nullable String message, String... strings) throws EmptyStringException {
        message = message == null ? EmptyStringException.DEFAULT_MESSAGE : message;
        for (String string : strings) {
            if (string == null) throw new EmptyStringException(message);
            if (string.replaceAll("\\s+", "").isEmpty()) throw new EmptyStringException(message);
        }
    }


    private static class EmptyStringException extends IllegalArgumentException {

        public static final String DEFAULT_MESSAGE = "String argument cannot be empty";

        public EmptyStringException(String detailMessage) {
            super(detailMessage);
        }

        public EmptyStringException() {
            this(DEFAULT_MESSAGE);
        }
    }

}
