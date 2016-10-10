package com.grudus.nativeexamshelper.database.exams;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.grudus.nativeexamshelper.database.QueryHelper;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.pojos.Exam;

import java.util.ArrayList;
import java.util.Date;

public class ExamsQuery {

    public static Cursor getAllIncomingExamsAndSortByDate(SQLiteDatabase db) {
        long time = System.currentTimeMillis();
        Cursor c = db.query(
                ExamsContract.ExamEntry.TABLE_NAME,
                ExamsContract.ExamEntry.ALL_COLUMNS,
                ExamsContract.ExamEntry.DATE_COLUMN + ">?",
                new String[] {time + ""},
                null,
                null,
                ExamsContract.ExamEntry.DATE_COLUMN
                );

        c.moveToFirst();
        return c;
    }

    public static Cursor getAllExamsOlderThan(SQLiteDatabase db, long time) {
        Cursor c = db.query(
                ExamsContract.ExamEntry.TABLE_NAME,
                ExamsContract.ExamEntry.ALL_COLUMNS,
                ExamsContract.ExamEntry.DATE_COLUMN + "<?",
                new String[] {time + ""},
                null,
                null,
                null
        );

        c.moveToFirst();
        return c;
    }

    public static Cursor getAllRecordsAndSortByDate(SQLiteDatabase db) {
        return QueryHelper.getAllRecordsAndSortBy(db, ExamsContract.ExamEntry.TABLE_NAME
                , ExamsContract.ExamEntry.ALL_COLUMNS, ExamsContract.ExamEntry.DATE_COLUMN);
    }

    public static Cursor getAllRecords(SQLiteDatabase db) {
        return QueryHelper.getAllRecordsAndSortBy(db, ExamsContract.ExamEntry.TABLE_NAME,
                ExamsContract.ExamEntry.ALL_COLUMNS, null);
    }

    @Nullable
    public static ArrayList<Exam> getAllExamsOlderThanAsArray(SQLiteDatabase db, long time) {
        Cursor c = getAllExamsOlderThan(db, time);

        ArrayList<Exam> exams =  new ArrayList<>();
        do {
            exams.add(new Exam(
                    c.getLong(ExamsContract.ExamEntry.INDEX_COLUMN_INDEX),
                    c.getLong(ExamsContract.ExamEntry.SUBJECT_ID_COLUMN_INDEX),
                    c.getString(ExamsContract.ExamEntry.INDEX_COLUMN_INDEX),
                    new Date(c.getLong(ExamsContract.ExamEntry.DATE_COLUMN_INDEX))
            ));
        } while (c.moveToNext());

        c.close();
        return exams;
    }

    public static Cursor findGradesAndSortBy(SQLiteDatabase db, @NonNull Long subjectId, @Nullable String sort) {
        Cursor c = db.query(
                ExamsContract.ExamEntry.TABLE_NAME,
                ExamsContract.ExamEntry.ALL_COLUMNS,
                ExamsContract.ExamEntry.SUBJECT_ID_COLUMN + " = ?",
                new String[] {subjectId.toString()},
                null,
                null,
                sort
        );

        if (c != null) c.moveToFirst();
        return c;
    }

    public static long insert(SQLiteDatabase db, Exam exam) {
        ContentValues contentValues = new ContentValues(4);
        contentValues.put(ExamsContract.ExamEntry.SUBJECT_ID_COLUMN, exam.getSubjectId());
        contentValues.put(ExamsContract.ExamEntry.INFO_COLUMN, exam.getInfo());
        contentValues.put(ExamsContract.ExamEntry.DATE_COLUMN, DateHelper.getLongFromDate(exam.getDate()));
        contentValues.put(ExamsContract.ExamEntry.CHANGE_COLUMN, ExamsContract.CHANGE_CREATE);

        return db.insert(ExamsContract.ExamEntry.TABLE_NAME, null, contentValues);
    }

    public static boolean remove(SQLiteDatabase db, long timeInMillis) {
        final String WHERE = ExamsContract.ExamEntry.DATE_COLUMN + " = ?";
        return db.delete(ExamsContract.ExamEntry.TABLE_NAME,
                WHERE,
                new String[] {timeInMillis + ""}) > 0;
    }

    public static boolean remove(SQLiteDatabase db, Exam exam) {
        return remove(db, DateHelper.getLongFromDate(exam.getDate()));
    }

    public static int removeAll(SQLiteDatabase db) {
        return db.delete(ExamsContract.ExamEntry.TABLE_NAME,
                null,
                null);
    }

    public static int removeSubjectExams(SQLiteDatabase db, Long subjectId) {
        return db.delete(ExamsContract.ExamEntry.TABLE_NAME,
                ExamsContract.ExamEntry.SUBJECT_ID_COLUMN + " = ?",
                new String[] {subjectId.toString()});
    }

    public static Cursor getAllExamsWithChange(SQLiteDatabase database) {
        return database.query(
                ExamsContract.ExamEntry.TABLE_NAME,
                ExamsContract.ExamEntry.ALL_COLUMNS,
                ExamsContract.ExamEntry.CHANGE_COLUMN + " IS NOT NULL",
                null,
                null,
                null,
                null
        );
    }

    public static Integer updateSetGrade(SQLiteDatabase database, Exam exam, double grade) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(ExamsContract.ExamEntry.GRADE_COLUMN, grade);

        return database.update(
                ExamsContract.ExamEntry.TABLE_NAME,
                contentValues,
                ExamsContract.ExamEntry._ID + " = ?",
                new String[] {exam.getId().toString()}
        );
    }

    public static Integer updateChangesToNull(SQLiteDatabase database) {
        ContentValues values = new ContentValues(1);
        values.put(ExamsContract.ExamEntry.CHANGE_COLUMN, (String) null);

        return database.update(
                ExamsContract.ExamEntry.TABLE_NAME,
                values,
                null,
                null
        );
    }
}
