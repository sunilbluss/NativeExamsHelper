package com.grudus.nativeexamshelper.pojos.grades;


import android.content.Context;
import android.preference.PreferenceManager;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.MyApplication;

public class Grades {

    public static final double EMPTY = -1D;

    private static Grade currentGrade;

    private static boolean decimalsInGradesEnabled;

    static {
        Context context = MyApplication.getContext();

        String key = context.getString(R.string.key_grades_type);
        int gradeType = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(key, 0);
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

    public static double getFirstPassedGrade() {
        return currentGrade.getFirstPassedGrade();
    }

    public static double[] getAllPossibleGrades() {
        return currentGrade.getGrades();
    }

    public static String[] getAllPossibleGradesAsStrings() {
        return currentGrade.getGradesAsString();
    }

    public static boolean isInRange(double grade) {
        return currentGrade.isInRange(grade) || grade == EMPTY;
    }


    public static String getGradeAsString(double grade) {

        for (int i = 0; i < getAllPossibleGrades().length; i++)
            if (grade == getAllPossibleGrades()[i])
                return getAllPossibleGradesAsStrings()[i];

        return "";
    }
}
