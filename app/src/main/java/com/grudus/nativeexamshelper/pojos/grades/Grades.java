package com.grudus.nativeexamshelper.pojos.grades;


import android.content.Context;
import android.preference.PreferenceManager;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.MyApplication;

import java.util.Random;

public class Grades {

    private static Grade currentGrade;

    private static boolean decimalsInGradesEnabled;

    // TODO: 12.07.16 change to user's choice
    static {
        Context context = MyApplication.getContext();

        String key = context.getString(R.string.key_grades_type);
        String gradeType = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, "0");
        currentGrade = GradeFactory.getGrade(gradeType);

        key = context.getString(R.string.key_grades_decimal);

        if (currentGrade instanceof UniversityGrade)
            decimalsInGradesEnabled = false;

        else
            decimalsInGradesEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, false);
    }

    private Grades() {}

    public static Grade getCurrentGrade() {
        return currentGrade;
    }

    public static void setGradeMode(int type) {
        currentGrade = GradeFactory.getGrade(type);
    }

    public static boolean areDecimalsInGradesEnabled() {
        return decimalsInGradesEnabled;
    }

    public static void enableDecimalsInGrades(boolean enable) {
        decimalsInGradesEnabled = enable;
    }

    public static void setGradeMode(String entryValue) {
        currentGrade = GradeFactory.getGrade(entryValue);
    }

    public static double getFirstPassedGrade() {
        return currentGrade.getFirstPassedGrade();
    }

    public static double[] getAllPossibleGrades() {
        return currentGrade.getGrades();
    }

    public static String[] getAllPossibleGradesAsStrings() {
        String[] grades = new String[currentGrade.getGrades().length];
        for (int i = 0; i < currentGrade.getGrades().length; i++) {
            grades[i] = (int) (currentGrade.getGrades()[i] + 0.5)
                    + (currentGrade.getGrades()[i] % 1 == 0.25 ? "+" : (currentGrade.getGrades()[i] % 1 == 0.75 ? "-" : ""));
        }
        return grades;
    }

    public static boolean isInRange(double grade) {
        return currentGrade.isInRange(grade);
    }


    //// TODO: 12.07.16 debug only
    public static double getRandomGrade() {
        return currentGrade.getGrades()[new Random().nextInt(currentGrade.getGrades().length)];
    }
}
