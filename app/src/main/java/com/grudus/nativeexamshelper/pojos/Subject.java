package com.grudus.nativeexamshelper.pojos;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable {

    private String title;
    private String color;

    public Subject(String title, String color) {
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Subject (" + title + ") object";
    }

//    Parcelable stuff


    public Subject(Parcel parcel) {
        String[] data = new String[2];
        parcel.readStringArray(data);
        this.title = data[0];
        this.color = data[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {title, color});
    }


    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

}
