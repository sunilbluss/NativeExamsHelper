package com.grudus.nativeexamshelper.pojos.grades;


public class GradeException extends RuntimeException {

    public GradeException() {
        super("Grade isn't in range " + Grades.getCurrentGrade().toString());
    }
}
