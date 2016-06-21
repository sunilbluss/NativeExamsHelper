package com.grudus.nativeexamshelper.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.grudus.nativeexamshelper.activities.AddingExamMainActivity;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.database.exams.ExamsORMImpl;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.database.subjects.SubjectsORMImpl;
import com.grudus.nativeexamshelper.pojos.Exam;
import com.grudus.nativeexamshelper.pojos.Subject;

public class ExamsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ExamsHelper.db";
    public static final int DATABASE_VERSION = 5;

    private SQLiteDatabase database;

    private ExamsORMImpl examsORM;
    private SubjectsORMImpl subjectsORM;

    public ExamsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        examsORM = new ExamsORMImpl();
        subjectsORM = new SubjectsORMImpl();
        Log.d(AddingExamMainActivity.TAG, "ExamsDbHelper() constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(AddingExamMainActivity.TAG, "onCreate() ExamsDbHelper");
        db.execSQL(ExamsContract.ExamEntry.CREATE_TABLE_QUERY);
        db.execSQL(SubjectsContract.SubjectEntry.CREATE_TABLE_QUERY);
        subjectsORM.firstInsert(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ExamsContract.ExamEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SubjectsContract.SubjectEntry.TABLE_NAME);
        onCreate(db);
    }


    public void openDB() {
        database = this.getWritableDatabase();
        Log.d(AddingExamMainActivity.TAG, "Database is opened");
    }

    public void closeDB() {
        if (database != null && database.isOpen())
            database.close();
        this.close();
        Log.d(AddingExamMainActivity.TAG, "Database is closed");
    }

//    Subjects part ******************************

    public Cursor selectAllFromSubjects() {
        return subjectsORM.getAllRecords(database);
    }

    public Cursor selectAllFromSubjectsSortByTitle() {
        return subjectsORM.getAllRecordsAndSortByTitle(database);
    }

    public long insertSubject(Subject subject) {
        return subjectsORM.insert(database, subject);
    }

    public void refreshSubjects() {
        database.delete(SubjectsContract.SubjectEntry.TABLE_NAME, null, null);
        subjectsORM.firstInsert(database);
        Log.d(AddingExamMainActivity.TAG, "Subjects are fresh");
    }

    @Nullable
    public Subject findSubjectByTitle(String title) {
        return subjectsORM.findByTitle(database, title);
    }



//    Exams part *********************************

    public Cursor selectAllFromExams() {
        return examsORM.getAllRecords(database);
    }

    public Cursor selectAllFromExamsSortByDate() {
       return examsORM.getAllRecordsAndSortByDate(database);
    }

    public long insertExam(Exam exam) {
        return examsORM.insert(database, exam);
    }

    public void cleanAllExamRecords() {
        if (database != null && database.isOpen()) {
            database.delete(ExamsContract.ExamEntry.TABLE_NAME, null, null);
            Log.d(AddingExamMainActivity.TAG, "All records was deleted");
        }
        else Log.e(AddingExamMainActivity.TAG, "Database is not opened - cannot delete records");
    }

}
