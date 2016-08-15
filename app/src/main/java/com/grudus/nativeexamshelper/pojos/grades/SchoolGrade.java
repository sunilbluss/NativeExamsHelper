package com.grudus.nativeexamshelper.pojos.grades;

import java.util.Arrays;

public class SchoolGrade extends Grade {

    private static final double[] GRADES = {1, 1.25, 1.75, 2, 2.25, 2.75, 3, 3.25, 3.75, 4, 4.25, 4.75, 5, 5.25, 5.75, 6};


    @Override
    public double getFirstPassedGrade() {
        return GRADES[2];
    }

    @Override
    public double[] getGrades() {
        return GRADES;
    }

    @Override
    public String[] getGradesAsString() {
        String[] grades = new String[getGrades().length];
        for (int i = 0; i < getGrades().length; i++) {
            grades[i] = (int) (getGrades()[i] + 0.5)
                    + (getGrades()[i] % 1 == 0.25 ? "+" : (getGrades()[i] % 1 == 0.75 ? "-" : ""));
        }
        return grades;
    }
}
