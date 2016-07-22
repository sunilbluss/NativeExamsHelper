package com.grudus.nativeexamshelper.database.exams;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.QueryHelper;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.pojos.Exam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
                    c.getString(ExamsContract.ExamEntry.SUBJECT_COLUMN_INDEX),
                    c.getString(ExamsContract.ExamEntry.INDEX_COLUMN_INDEX),
                    new Date(c.getLong(ExamsContract.ExamEntry.DATE_COLUMN_INDEX))
            ));
        } while (c.moveToNext());

        c.close();
        return exams;
    }

    public static long insert(SQLiteDatabase db, Exam exam) {
        ContentValues contentValues = new ContentValues(3);
        contentValues.put(ExamsContract.ExamEntry.SUBJECT_COLUMN, exam.getSubject());
        contentValues.put(ExamsContract.ExamEntry.INFO_COLUMN, exam.getInfo());
        contentValues.put(ExamsContract.ExamEntry.DATE_COLUMN, DateHelper.getLongFromDate(exam.getDate()));

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
}
