package com.grudus.nativeexamshelper.pojos;


import android.support.annotation.NonNull;
import android.util.Log;

import com.grudus.nativeexamshelper.helpers.DateHelper;

import java.util.Date;

public class OldExam {

    private Subject subject;
    private String info;
    private double grade;
    private Date date;

    public static final double[] POSSIBLE_GRADES = {0, 1, 1.25, 1.75, 2, 2.25, 2.75, 3, 3.25, 3.75, 4, 4.25, 4.75, 5, 5.25, 5.75, 6};

    public OldExam(@NonNull Subject subject, String info, double grade, @NonNull Date date) {
        if (!isInRange(grade)) {
            Log.e("@@@", "OldExam: " + grade + "isn't in range", new IllegalArgumentException());
            this.grade = POSSIBLE_GRADES[0];
        }
        this.subject = subject;
        this.info = info;
        this.grade = grade;
        this.date = date;
    }


    public OldExam(@NonNull Subject subject, String info, @NonNull Date date) {
        this(subject, info, POSSIBLE_GRADES[0], date);

    }

    private boolean isInRange(double grade) {
        for (double d : POSSIBLE_GRADES)
            if (grade == d)
                return true;
        return false;
    }

    public boolean hasGrade() {
        return grade != POSSIBLE_GRADES[0];
    }


    public Subject getSubject() {
        return subject;
    }

    public void setSubject(@NonNull Subject subject) {
        this.subject = subject;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return subject + " " + grade + " written in " + DateHelper.getStringFromDate(date);
    }
}
