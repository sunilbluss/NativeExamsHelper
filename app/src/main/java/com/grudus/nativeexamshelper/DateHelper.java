package com.grudus.nativeexamshelper;


import android.support.annotation.NonNull;

import com.grudus.nativeexamshelper.activities.AddingExamMainActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    private static final String dateFormat = AddingExamMainActivity.getMainApplicationContext() != null
            ? AddingExamMainActivity.getMainApplicationContext().getResources().getString(R.string.date_format)
            : DEFAULT_DATE_FORMAT;

    public static Date getDateFromString(@NonNull String date) throws ParseException {
        ExceptionsHelper.checkStringEmptiness("Date cannot be null", date);
        DateFormat format = new SimpleDateFormat(
                dateFormat, Locale.ENGLISH);
        return format.parse(date);
    }

    public static String getStringFromDate(@NonNull Date date) {
        if (date == null) throw new NullPointerException("Date cannot be null");
        DateFormat format = new SimpleDateFormat(
                dateFormat, Locale.ENGLISH);
        return format.format(date);
    }

    public static long getLongFromDate(@NonNull Date date) {
        if (date == null) throw new NullPointerException("Date cannot be null");
        return date.getTime();
    }

    public static String getReadableDataFromLong(long milliseconds) {
        Date date = new Date(milliseconds);
        return new SimpleDateFormat(dateFormat, Locale.ENGLISH).format(date);
    }
}
