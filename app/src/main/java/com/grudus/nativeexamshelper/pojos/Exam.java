package com.grudus.nativeexamshelper.pojos;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.grudus.nativeexamshelper.DateHelper;
import com.grudus.nativeexamshelper.ExceptionsHelper;

import java.text.ParseException;
import java.util.Date;

public class Exam implements Parcelable {

    private String subject;
    private String info;
    private Date date;

    public Exam(@NonNull String subject, String info, @NonNull Date date) {
        ExceptionsHelper.checkStringEmptiness("Subject cannot be empty", subject);
        if (date == null) throw new NullPointerException("Date cannot be null");

        this.subject = subject;
        this.info = info;
        this.date = date;

    }

    @NonNull
    public String getSubject() {
        return subject;
    }

    public void setSubject(@NonNull String subject) {
        ExceptionsHelper.checkStringEmptiness("Subject cannot be null", subject);
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
    public String toString() {
        return  subject + " exam from " + info + " on "
                + DateHelper.getStringFromDate(date);
    }


//  Parcelable stuff

    public Exam(Parcel parcel) {
        String[] data = new String[3];
        parcel.readStringArray(data);
        this.subject = data[0];
        this.info = data[1];

        try {
            this.date = DateHelper.getDateFromString(data[2]);
        } catch (ParseException e) {
            Log.e("@@@Exam", "Exam: cannot parse date ", e);
        }
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
