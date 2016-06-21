package com.grudus.nativeexamshelper.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grudus.nativeexamshelper.AddingExamMainActivity;
import com.grudus.nativeexamshelper.DateHelper;
import com.grudus.nativeexamshelper.pojos.Exam;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ExamsHelper.db";
    public static final int DATABASE_VERSION = 2;

    private SQLiteDatabase database;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ExamsContract.ExamEntry.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ExamsContract.ExamEntry.TABLE_NAME);
        onCreate(db);
    }


    public void openDB() {
        database = this.getWritableDatabase();
    }

    public void closeDB() {
        if (database != null && database.isOpen())
            database.close();
        this.close();
    }

    public void cleanAllRecords() {
        if (database != null && database.isOpen()) {
            database.delete(ExamsContract.ExamEntry.TABLE_NAME, null, null);
            Log.d(AddingExamMainActivity.TAG, "All records was deleted");
        }
        else Log.e(AddingExamMainActivity.TAG, "Database is not opened");
    }


    public Cursor selectAllFromExams() {
        Cursor c =  database
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

    public long insertExam(Exam exam) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(ExamsContract.ExamEntry.SUBJECT_COLUMN, exam.getSubject());
        contentValues.put(ExamsContract.ExamEntry.INFO_COLUMN, exam.getInfo());
        contentValues.put(ExamsContract.ExamEntry.DATE_COLUMN, DateHelper.getLongFromDate(exam.getDate()));

        return database.insert(ExamsContract.ExamEntry.TABLE_NAME, null, contentValues);
    }
}
