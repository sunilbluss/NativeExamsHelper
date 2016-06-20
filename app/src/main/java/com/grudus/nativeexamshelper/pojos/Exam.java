package com.grudus.nativeexamshelper.pojos;


import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.grudus.nativeexamshelper.DateHelper;
import com.grudus.nativeexamshelper.R;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Exam implements Parcelable {

    private String subject;
    private String info;
    private Date date;

    public Exam(@NonNull String subject, String info, @NonNull Date date) {
        if (subject == null || date == null || subject.isEmpty())
            throw new IllegalStateException("Subject and date cannot be null");

        this.subject = subject;
        this.info = info;
        this.date = date;
    }

    public Exam(Parcel parcel) {
        String[] data = new String[3];
        parcel.readStringArray(data);
        this.subject = data[0];
        this.info = data[1];

        try {
            this.date = DateHelper.getDateFromString(data[2]);
        } catch (ParseException e) {
            for (StackTraceElement s : e.getStackTrace()) Log.e("______--------______", s.toString());
        }
    }

    @NonNull
    public String getSubject() {
        return subject;
    }

    public void setSubject(@NonNull String subject) {
        if (subject == null || subject.isEmpty())
            throw new IllegalStateException("Subject cannot be empty");
        this.subject = subject;
    }

    @Nullable
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        if (date == null)
            throw new IllegalStateException("Date cannot be null");
        this.date = date;
    }


    @Override
    // TODO: 6/20/16 change date format
    public String toString() {
        return  subject + " exam from " + info + " on "
                + date;
    }





    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {subject, info,
                DateHelper.getStringFromDate(date)});
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new Exam(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Exam[size];
        }
    };
}
