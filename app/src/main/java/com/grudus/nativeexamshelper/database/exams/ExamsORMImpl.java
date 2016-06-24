package com.grudus.nativeexamshelper.database.exams;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.pojos.Exam;

public final class ExamsORMImpl {


    public Cursor getAllRecords(SQLiteDatabase db) {
        Cursor c =  db
                .query(
                        ExamsContract.ExamEntry.TABLE_NAME,
                        ExamsContract.ExamEntry.ALL_COLUMNS,
                        null,
                        null,
                        null,
                        null,
                        null
                );
        if (c != null) c.moveToFirst();
        return c;
    }

    public Cursor getAllRecordsAndSortByDate(SQLiteDatabase db) {
        Cursor c = db
                .query(
                        ExamsContract.ExamEntry.TABLE_NAME,
                        ExamsContract.ExamEntry.ALL_COLUMNS,
                        null,
                        null,
                        null,
                        null,
                        ExamsContract.ExamEntry.DATE_COLUMN
                );
        if (c != null) c.moveToFirst();
        return c;
    }

    public long insert(SQLiteDatabase db, Exam exam) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(ExamsContract.ExamEntry.SUBJECT_COLUMN, exam.getSubject());
        contentValues.put(ExamsContract.ExamEntry.INFO_COLUMN, exam.getInfo());
        contentValues.put(ExamsContract.ExamEntry.DATE_COLUMN, DateHelper.getLongFromDate(exam.getDate()));

        return db.insert(ExamsContract.ExamEntry.TABLE_NAME, null, contentValues);
    }
}
