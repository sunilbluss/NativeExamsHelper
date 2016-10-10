package com.grudus.nativeexamshelper.database.exams;


import android.provider.BaseColumns;

public class ExamsContract {

    public static final String CHANGE_CREATE = "SUBJECT_NEW";
    public static final String CHANGE_UPDATED = "SUBJECT_EDIT";
    public static final String CHANGE_DELETED = "SUBJECT_REMOVED";

    private ExamsContract() {}


    public static abstract class ExamEntry implements BaseColumns {
        public static final String TABLE_NAME = "exams";
        public static final String SUBJECT_ID_COLUMN = "subject_id";
        public static final String INFO_COLUMN = "info";
        public static final String DATE_COLUMN = "date";
        public static final String GRADE_COLUMN = "grade";
        public static final String CHANGE_COLUMN = "change";

        public static final int INDEX_COLUMN_INDEX = 0;
        public static final int SUBJECT_ID_COLUMN_INDEX = 1;
        public static final int INFO_COLUMN_INDEX = 2;
        public static final int DATE_COLUMN_INDEX = 3;
        public static final int GRADE_COLUMN_INDEX = 4;
        public static final int CHANGE_COLUMN_INDEX = 5;

        public static final String[] ALL_COLUMNS = {_ID, SUBJECT_ID_COLUMN, INFO_COLUMN, DATE_COLUMN, GRADE_COLUMN, CHANGE_COLUMN};

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME +
                " ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                SUBJECT_ID_COLUMN + " INTEGER NOT NULL, " +
                INFO_COLUMN + " TEXT, " +
                DATE_COLUMN + " INTEGER NOT NULL, " +
                GRADE_COLUMN + " REAL, " +
                CHANGE_COLUMN + " TEXT "
                + ")";

    }

}
