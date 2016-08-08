package com.grudus.nativeexamshelper.database.exams;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.grudus.nativeexamshelper.database.QueryHelper;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.pojos.OldExam;

public class OldExamsQuery {


    public static Cursor getAllRecordsAndSortBy(SQLiteDatabase db, @Nullable String sort) {
        return QueryHelper.getAllRecordsAndSortBy(db, ExamsContract.OldExamEntry.TABLE_NAME,
                ExamsContract.OldExamEntry.ALL_COLUMNS, sort);
    }


    public static long insert(SQLiteDatabase db, OldExam exam) {
        ContentValues contentValues = new ContentValues(4);
        contentValues.put(ExamsContract.OldExamEntry.SUBJECT_COLUMN, exam.getSubject().getTitle());
        contentValues.put(ExamsContract.OldExamEntry.INFO_COLUMN, exam.getInfo());
        contentValues.put(ExamsContract.OldExamEntry.DATE_COLUMN, DateHelper.getLongFromDate(exam.getDate()));
        contentValues.put(ExamsContract.OldExamEntry.GRADE_COLUMN, exam.getGrade());

        return db.insert(ExamsContract.OldExamEntry.TABLE_NAME, null, contentValues);
    }

    public static void randomInsert(SQLiteDatabase db) {
        ContentValues cv = new ContentValues(4);
        cv.put(ExamsContract.OldExamEntry.SUBJECT_COLUMN, "Matematyka");
        cv.put(ExamsContract.OldExamEntry.INFO_COLUMN, "Pochodne");
        cv.put(ExamsContract.OldExamEntry.DATE_COLUMN, System.currentTimeMillis());
        cv.put(ExamsContract.OldExamEntry.GRADE_COLUMN, "4");
        db.insert(ExamsContract.OldExamEntry.TABLE_NAME, null, cv);
    }


    public static Cursor findGradesAndSortBy(SQLiteDatabase db, @NonNull String subjectTitle, @Nullable String sort) {
        Cursor c = db.query(
                ExamsContract.OldExamEntry.TABLE_NAME,
                ExamsContract.OldExamEntry.ALL_COLUMNS,
                ExamsContract.OldExamEntry.SUBJECT_COLUMN + " = ?",
                new String[] {subjectTitle},
                null,
                null,
                sort
        );

        if (c != null) c.moveToFirst();
        return c;
    }

    public static int removeAll(SQLiteDatabase db) {
        return db.delete(ExamsContract.OldExamEntry.TABLE_NAME,
                null,
                null);
    }

    public static int removeSubjectExams(SQLiteDatabase db, String subjectTitle) {
        return db.delete(ExamsContract.OldExamEntry.TABLE_NAME,
                ExamsContract.OldExamEntry.SUBJECT_COLUMN + " = ?",
                new String[] {subjectTitle});
    }

    public static Object removeExam(SQLiteDatabase db, long timeInMillis) {
        return db.delete(ExamsContract.OldExamEntry.TABLE_NAME,
                ExamsContract.OldExamEntry.DATE_COLUMN + " = ?",
                new String[] {timeInMillis + ""});
    }
}
