package com.grudus.nativeexamshelper.pojos.grades;


import java.util.Arrays;

public abstract class Grade {


    public static final double EMPTY_GRADE = -1;

    public boolean isInRange(double grade) {
        for (double d : getGrades())
            if (d == grade)
                return true;
        return false;
    }

    @Override
    public String toString() {
        return Arrays.toString(getGrades());
    }

    public abstract double getFirstPassedGrade();
    public abstract double[] getGrades();


}
