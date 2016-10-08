package com.grudus.nativeexamshelper.database.subjects;


import android.provider.BaseColumns;

public class SubjectsContract {

    public static final String CHANGE_CREATE = "SUBJECT_NEW";
    public static final String CHANGE_UPDATED = "SUBJECT_EDIT";
    public static final String CHANGE_DELETED = "SUBJECT_REMOVED";

    private SubjectsContract() {}

    public static abstract class SubjectEntry implements BaseColumns {

        public static final String TABLE_NAME = "subjects";
        public static final String TITLE_COLUMN = "title";
        public static final String COLOR_COLUMN = "color";
        public static final String HAS_GRADE_COLUMN = "has_grade";
        public static final String CHANGE_COLUMN = "change";

        public static final int INDEX_COLUMN_INDEX = 0;
        public static final int TITLE_COLUMN_INDEX = 1;
        public static final int COLOR_COLUMN_INDEX = 2;
        public static final int HAS_GRADE_COLUMN_INDEX = 3;
        public static final int CHANGE_COLUMN_INDEX = 4;

        public static final String[] ALL_COLUMNS = {_ID, TITLE_COLUMN, COLOR_COLUMN, HAS_GRADE_COLUMN, CHANGE_COLUMN};

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME +
                " ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                TITLE_COLUMN + " TEXT NOT NULL, " +
                COLOR_COLUMN + " TEXT NOT NULL, " +
                HAS_GRADE_COLUMN + " INTEGER, " +
                CHANGE_COLUMN + " TEXT "
                + ")";

    }

}
