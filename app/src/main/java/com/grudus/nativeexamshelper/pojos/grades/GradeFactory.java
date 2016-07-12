package com.grudus.nativeexamshelper.pojos.grades;


public class GradeFactory {

    public static final int SCHOOL_MODE = 0;
    public static final int UNIVERSITY_MODE = 1;
    public static final int CUSTOM_MODE = 2;

    public static Grade getGrade(int mode) {
        switch (mode) {
            default:
            case SCHOOL_MODE: return new SchoolGrade();
            case UNIVERSITY_MODE: return new UniversityGrade();
        }
    }

}
