package com.grudus.nativeexamshelper.database.subjects;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.AddingExamMainActivity;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.pojos.Subject;


public final class SubjectsORMImpl {

    private static final String[] defaultSubjects = AddingExamMainActivity.getMainApplicationContext().getResources()
            .getStringArray(R.array.default_subjects);

    private static final String[] defaultColors = AddingExamMainActivity.getMainApplicationContext().getResources()
            .getStringArray(R.array.defaultSubjectsColors);

    public boolean firstInsert(SQLiteDatabase db) {
        ContentValues[] firstValues = new ContentValues[defaultSubjects.length];
        for (int i = 0; i < firstValues.length; i++) {
            firstValues[i] = new ContentValues(2);
            firstValues[i].put(SubjectsContract.SubjectEntry.TITLE_COLUMN, defaultSubjects[i]);
            firstValues[i].put(SubjectsContract.SubjectEntry.COLOR_COLUMN,
                    defaultColors[i]);

            Log.i(AddingExamMainActivity.TAG, "Color is " + defaultColors[i]);
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

        Log.d(AddingExamMainActivity.TAG, "Inserted " + counter + " values");
        return counter == firstValues.length;
    }

    public Cursor getAllRecords(SQLiteDatabase db) {
        Cursor c =  db
                .query(
                        SubjectsContract.SubjectEntry.TABLE_NAME,
                        SubjectsContract.SubjectEntry.ALL_COLUMNS,
                        null,
                        null,
                        null,
                        null,
                        null
                );
        if (c != null) c.moveToFirst();
        return c;
    }

    @Nullable
    public Subject findByTitle(SQLiteDatabase db, String title) {
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

    public Cursor getAllRecordsAndSortByTitle(SQLiteDatabase db) {
        Cursor c = db
                .query(
                        SubjectsContract.SubjectEntry.TABLE_NAME,
                        SubjectsContract.SubjectEntry.ALL_COLUMNS,
                        null,
                        null,
                        null,
                        null,
                        SubjectsContract.SubjectEntry.TITLE_COLUMN
                );
        if (c != null) c.moveToFirst();
        return c;
    }

    public long insert(SQLiteDatabase db, Subject subject) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(SubjectsContract.SubjectEntry.TITLE_COLUMN, subject.getTitle());
        contentValues.put(SubjectsContract.SubjectEntry.COLOR_COLUMN, subject.getColor());

        return db.insert(SubjectsContract.SubjectEntry.TABLE_NAME, null, contentValues);
    }


    public void update(SQLiteDatabase db, Subject old, Subject _new) {
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
}
