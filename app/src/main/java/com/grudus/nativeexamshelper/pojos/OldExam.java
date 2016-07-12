package com.grudus.nativeexamshelper.pojos;


import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.util.Log;

import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.pojos.grades.GradeException;
import com.grudus.nativeexamshelper.pojos.grades.Grades;

import java.util.Date;

public class OldExam {

    private Subject subject;
    private String info;
    private double grade;
    private Date date;

    public OldExam(@NonNull Subject subject, String info, double grade, @NonNull Date date) {
        if (!Grades.isInRange(grade)) throw new GradeException();
        this.subject = subject;
        this.info = info;
        this.grade = grade;
        this.date = date;
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

    public void setGrade(double grade) {
        if (!Grades.isInRange(grade)) throw new GradeException();
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
