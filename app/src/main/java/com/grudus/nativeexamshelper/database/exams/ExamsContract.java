package com.grudus.nativeexamshelper.database.exams;


import android.provider.BaseColumns;

public class ExamsContract {

    private ExamsContract() {}

    public static abstract class OldExamEntry implements BaseColumns {
        public static final String TABLE_NAME = "oldExams";
        public static final String SUBJECT_COLUMN = "subject";
        public static final String INFO_COLUMN = "info";
        public static final String DATE_COLUMN = "date";
        public static final String GRADE_COLUMN = "grade";

        public static final int INDEX_COLUMN_INDEX = 0;
        public static final int SUBJECT_COLUMN_INDEX = 1;
        public static final int INFO_COLUMN_INDEX = 2;
        public static final int DATE_COLUMN_INDEX = 3;
        public static final int GRADE_COLUMN_INDEX = 4;

        public static final String[] ALL_COLUMNS = {_ID, SUBJECT_COLUMN, INFO_COLUMN, DATE_COLUMN, GRADE_COLUMN};

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME +
                " ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                SUBJECT_COLUMN + " TEXT NOT NULL, " +
                INFO_COLUMN + " TEXT, " +
                DATE_COLUMN + " INTEGER NOT NULL, " +
                GRADE_COLUMN + " REAL "
                + ")";

    }


    public static abstract class ExamEntry implements BaseColumns {
        public static final String TABLE_NAME = "exams";
        public static final String SUBJECT_COLUMN = "subject";
        public static final String INFO_COLUMN = "info";
        public static final String DATE_COLUMN = "date";

        public static final int INDEX_COLUMN_INDEX = 0;
        public static final int SUBJECT_COLUMN_INDEX = 1;
        public static final int INFO_COLUMN_INDEX = 2;
        public static final int DATE_COLUMN_INDEX = 3;

        public static final String[] ALL_COLUMNS = {_ID, SUBJECT_COLUMN, INFO_COLUMN, DATE_COLUMN};

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME +
                " ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                SUBJECT_COLUMN + " TEXT NOT NULL, " +
                INFO_COLUMN + " TEXT, " +
                DATE_COLUMN + " INTEGER NOT NULL "
                + ")";

    }

}
