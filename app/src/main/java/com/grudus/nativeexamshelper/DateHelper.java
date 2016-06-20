package com.grudus.nativeexamshelper;


import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    public static Date getDateFromString(@NonNull String date) throws ParseException {
        if (date == null || date.replaceAll("\\s+", "").isEmpty())
            throw new IllegalStateException("Date cannot be null");
        DateFormat format = new SimpleDateFormat(
                AddingExamMainActivity.getMainApplicationContext().getResources().getString(R.string.date_format), Locale.ENGLISH);
        return format.parse(date);
    }

    public static String getStringFromDate(@NonNull Date date) {
        if (date == null) throw new NullPointerException("Date cannot be null");
        DateFormat format = new SimpleDateFormat(
                AddingExamMainActivity.getMainApplicationContext().getResources().getString(R.string.date_format), Locale.ENGLISH);
        return format.format(date);
    }
}
