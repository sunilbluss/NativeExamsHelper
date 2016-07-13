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
