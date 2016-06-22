package com.grudus.nativeexamshelper.pojos;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.grudus.nativeexamshelper.ExceptionsHelper;

public class Subject implements Parcelable {

    private String title;
    private String color;

    public Subject(@NonNull String title, @NonNull String color) {
        ExceptionsHelper.checkStringEmptiness("Title and color cannot be empty", title, color);
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        ExceptionsHelper.checkStringEmptiness("Title cannot be empty", title);
        this.title = title;
    }


    public String getColor() {
        return color;
    }

    public void setColor(@NonNull String color) {
        ExceptionsHelper.checkStringEmptiness("Color cannot be empty", color);
        this.color = color;
    }

    @Override
    public String toString() {
        return "Subject (" + title + ") object";
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (object == this) return true;
        if (this.getClass() != object.getClass()) return false;
        Subject that = (Subject) object;
        return that.getTitle().equalsIgnoreCase(this.getTitle());
    }

    @Override
    public int hashCode() {
        return title.hashCode();
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
