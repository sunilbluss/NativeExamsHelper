package com.grudus.nativeexamshelper.pojos.grades;


import java.util.Random;

public class Grades {

    private static Grade currentGrade;

    // TODO: 12.07.16 change to user's choice
    static {
        currentGrade = GradeFactory.getGrade(GradeFactory.SCHOOL_MODE);
    }

    private Grades() {}

    public static Grade getCurrentGrade() {
        return currentGrade;
    }

    public static void setGradeMode(int type) {
        currentGrade = GradeFactory.getGrade(type);
    }

    public static double getFirstPassedGrade() {
        return currentGrade.getFirstPassedGrade();
    }

    public static double[] getAllPossibleGrades() {
        return currentGrade.getGrades();
    }

    public static boolean isInRange(double grade) {
        return currentGrade.isInRange(grade);
    }


    //// TODO: 12.07.16 debug only
    public static double getRandomGrade() {
        return currentGrade.getGrades()[new Random().nextInt(currentGrade.getGrades().length)];
    }
}
