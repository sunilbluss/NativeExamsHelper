package com.grudus.nativeexamshelper.database.subjects;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.grudus.nativeexamshelper.activities.ExamsMainActivity;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.QueryHelper;
import com.grudus.nativeexamshelper.pojos.Subject;


public final class SubjectsQuery {

    private static String[] defaultSubjects = new String[0];

    private static String[] defaultColors = new String[0];

    public static void setDefaultSubjects(@NonNull String[] defaultSubjects) {
        SubjectsQuery.defaultSubjects = defaultSubjects;
    }

    public static void setDefaultColors(@NonNull String[] defaultColors) {
        SubjectsQuery.defaultColors = defaultColors;
    }

    public static boolean firstInsert(SQLiteDatabase db) {
        ContentValues[] firstValues = new ContentValues[defaultSubjects.length];
        for (int i = 0; i < firstValues.length; i++) {
            firstValues[i] = new ContentValues(2);
            firstValues[i].put(SubjectsContract.SubjectEntry.TITLE_COLUMN, defaultSubjects[i]);
            firstValues[i].put(SubjectsContract.SubjectEntry.COLOR_COLUMN,
                    defaultColors[i]);

            Log.i(ExamsMainActivity.TAG, "Color is " + defaultColors[i]);
        }
        int counter = 0;

        try {
            db.beginTransaction();
            for (ContentValues val : firstValues) {
                long _id = db.insert(SubjectsContract.SubjectEntry.TABLE_NAME, null, val);

                if (_id != -1) counter++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Log.d(ExamsMainActivity.TAG, "Inserted " + counter + " values");
        return counter == firstValues.length;
    }

    public static Cursor getAllRecords(SQLiteDatabase db) {
        return QueryHelper.getAllRecordsAndSortBy(db, SubjectsContract.SubjectEntry.TABLE_NAME,
                SubjectsContract.SubjectEntry.ALL_COLUMNS, null);
    }

    public static Cursor getAllRecordsAndSortByTitle(SQLiteDatabase db) {
        return QueryHelper.getAllRecordsAndSortBy(db, SubjectsContract.SubjectEntry.TABLE_NAME,
                SubjectsContract.SubjectEntry.ALL_COLUMNS, SubjectsContract.SubjectEntry.TITLE_COLUMN);
    }

    @Nullable
    public static Subject findByTitle(SQLiteDatabase db, String title) {
        Cursor c = db
                .query(
                        SubjectsContract.SubjectEntry.TABLE_NAME,
                        SubjectsContract.SubjectEntry.ALL_COLUMNS,
                        SubjectsContract.SubjectEntry.TITLE_COLUMN + "=?",
                        new String[] {title},
                        null,
                        null,
                        null
                );
        if (c == null)
            return null;
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Subject subject = new Subject(c.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX),
                c.getString(SubjectsContract.SubjectEntry.COLOR_COLUMN_INDEX));

        c.close();

        return subject;

    }

    public static long insert(SQLiteDatabase db, Subject subject) {
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(SubjectsContract.SubjectEntry.TITLE_COLUMN, subject.getTitle());
        contentValues.put(SubjectsContract.SubjectEntry.COLOR_COLUMN, subject.getColor());

        return db.insert(SubjectsContract.SubjectEntry.TABLE_NAME, null, contentValues);
    }


    public static void update(SQLiteDatabase db, Subject old, Subject _new) {
        if (findByTitle(db, old.getTitle()) == null) {
            Log.e(ExamsDbHelper.TAG, "update: " + old + " doesn't exists");
            return;
        }

        ContentValues cv = new ContentValues(2);
        cv.put(SubjectsContract.SubjectEntry.TITLE_COLUMN, _new.getTitle());
        cv.put(SubjectsContract.SubjectEntry.COLOR_COLUMN, _new.getColor());

        db.update(SubjectsContract.SubjectEntry.TABLE_NAME,
                cv,
                SubjectsContract.SubjectEntry.TITLE_COLUMN + "=?",
                new String[] {old.getTitle()});

        Log.d(ExamsDbHelper.TAG, "Updated " + old + " to " + _new);
    }

    public static Cursor findSubjectsWithGradesAndSortBy(SQLiteDatabase db, @Nullable String sort) {
        Cursor c = db.query(
                SubjectsContract.SubjectEntry.TABLE_NAME,
                SubjectsContract.SubjectEntry.ALL_COLUMNS,
                SubjectsContract.SubjectEntry.HAS_GRADE_COLUMN + " = ?",
                new String[] {"1"},
                null,
                null,
                sort
        );
        if (c != null) c.moveToFirst();
        return c;
    }

    public static int setSubjectHasGrade(SQLiteDatabase db, Subject subject, boolean hasGrade) {
        ContentValues cv = new ContentValues(3);
        cv.put(SubjectsContract.SubjectEntry.TITLE_COLUMN, subject.getTitle());
        cv.put(SubjectsContract.SubjectEntry.COLOR_COLUMN, subject.getColor());
        cv.put(SubjectsContract.SubjectEntry.HAS_GRADE_COLUMN, hasGrade ? 1 : 0);
        return db.update(
                SubjectsContract.SubjectEntry.TABLE_NAME,
                cv,
                SubjectsContract.SubjectEntry.TITLE_COLUMN + " = ?",
                new String[] {subject.getTitle()}
        );
    }

    public static void resetGrades(SQLiteDatabase db)  {
        ContentValues cv = new ContentValues(1);
        cv.put(SubjectsContract.SubjectEntry.HAS_GRADE_COLUMN, 0);
        db.update(
                SubjectsContract.SubjectEntry.TABLE_NAME,
                cv,
                null,
                null
        );
    }

    public static void removeSubject(SQLiteDatabase db, String subjectTitle) {
        db.delete(
                SubjectsContract.SubjectEntry.TABLE_NAME,
                SubjectsContract.SubjectEntry.TITLE_COLUMN + "=?",
                new String[] {subjectTitle}
        );
    }
}
