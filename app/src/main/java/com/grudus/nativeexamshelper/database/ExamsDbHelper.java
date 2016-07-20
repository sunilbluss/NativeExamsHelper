package com.grudus.nativeexamshelper.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.database.exams.ExamsQuery;
import com.grudus.nativeexamshelper.database.exams.OldExamsQuery;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.database.subjects.SubjectsQuery;
import com.grudus.nativeexamshelper.pojos.Exam;
import com.grudus.nativeexamshelper.pojos.OldExam;
import com.grudus.nativeexamshelper.pojos.Subject;
import com.grudus.nativeexamshelper.pojos.grades.Grade;
import com.grudus.nativeexamshelper.pojos.grades.Grades;

import java.util.ArrayList;
import java.util.Random;

public class ExamsDbHelper extends SQLiteOpenHelper {

    public static final String TAG = "@@@ Main DB HELPER @@@";

    public static final String DATABASE_NAME = "ExamsHelper.db";
    public static final int DATABASE_VERSION = 7;

    private SQLiteDatabase database;
    private Context context;

    private static ExamsDbHelper instance;

    private ExamsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d(TAG, "ExamsDbHelper() constructor");
    }

    public static ExamsDbHelper getInstance(Context context) {
        if (instance == null)
            instance = new ExamsDbHelper(context.getApplicationContext());
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate() ExamsDbHelper");
        db.execSQL(ExamsContract.ExamEntry.CREATE_TABLE_QUERY);
        db.execSQL(SubjectsContract.SubjectEntry.CREATE_TABLE_QUERY);
        db.execSQL(ExamsContract.OldExamEntry.CREATE_TABLE_QUERY);
        OldExamsQuery.randomInsert(db);
        SubjectsQuery.setDefaultSubjects(context.getResources().getStringArray(R.array.default_subjects));
        SubjectsQuery.setDefaultColors(context.getResources().getStringArray(R.array.defaultSubjectsColors));
        SubjectsQuery.firstInsert(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ExamsContract.ExamEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SubjectsContract.SubjectEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExamsContract.OldExamEntry.TABLE_NAME);
        onCreate(db);
    }


    public void openDB() {
        if (database == null || !database.isOpen())
            database = this.getWritableDatabase();
//        Log.d(TAG, "Database is opened");
    }

    public void openDBReadOnly() {
        database = this.getReadableDatabase();
//        Log.d(TAG, "openDBReadOnly: opened");
    }

    public void closeDB() {
        if (database != null && database.isOpen())
            database.close();
        this.close();
//        Log.d(TAG, "Database is closed");
    }

    public void cleanAllRecords(String tableName) {
        if (database != null && database.isOpen()) {
            database.delete(tableName, null, null);
            Log.d(TAG, "All records was deleted");
        }
        else Log.e(TAG, "Database is not opened - cannot delete records");
    }

//    Subjects part ******************************

    public Cursor selectAllFromSubjects() {
        return SubjectsQuery.getAllRecords(database);
    }

    public Cursor selectAllFromSubjectsSortByTitle() {
        return SubjectsQuery.getAllRecordsAndSortByTitle(database);
    }

    public long insertSubject(Subject subject) {
        return SubjectsQuery.insert(database, subject);
    }

    public void refreshSubjects() {
        database.delete(SubjectsContract.SubjectEntry.TABLE_NAME, null, null);
        SubjectsQuery.setDefaultColors(context.getResources().getStringArray(R.array.defaultSubjectsColors));
        SubjectsQuery.setDefaultSubjects(context.getResources().getStringArray(R.array.default_subjects));
        SubjectsQuery.firstInsert(database);
        Log.d(TAG, "Subjects are fresh");
    }

    public void updateSubject(Subject old, Subject _new) {
        SubjectsQuery.update(database, old, _new);
    }

    public int setSubjectHasGrade(Subject subject, boolean hasGrade) {
        return SubjectsQuery.setSubjectHasGrade(database, subject, hasGrade);
    }

    @Nullable
    public Subject findSubjectByTitle(String title) {
        return SubjectsQuery.findByTitle(database, title);
    }

    public void resetSubjectGrades() {
        SubjectsQuery.resetGrades(database);
    }

    public void removeSubject(String subjectTitle) {
        SubjectsQuery.removeSubject(database, subjectTitle);
    }

//    Exams part *********************************

    public Cursor selectAllFromExams() {
        return ExamsQuery.getAllRecords(database);
    }

    public Cursor selectAllFromExamsSortByDate() {
       return ExamsQuery.getAllRecordsAndSortByDate(database);
    }

    public Cursor getAllIncomingExamsSortByDate() {
        return ExamsQuery.getAllIncomingExamsAndSortByDate(database);
    }

    public long insertExam(Exam exam) {
        return ExamsQuery.insert(database, exam);
    }

    public Cursor getExamsOlderThan(long time) {
        return ExamsQuery.getAllExamsOlderThan(database, time);
    }

    @Nullable
    public ArrayList<Exam> selectAllFromExamsWhereDateIsSmallerThan(long dateInMillis) {
        return ExamsQuery.getAllExamsOlderThanAsArray(database, dateInMillis);
    }


//    Old exams part *****************************
    public Cursor selectAllFromOldExams() {
        return OldExamsQuery.getAllRecordsAndSortBy(database, null);
    }

    public Cursor selectAllFromOldExamsSortByDate() {
        return OldExamsQuery.getAllRecordsAndSortBy(database, ExamsContract.OldExamEntry.DATE_COLUMN);
    }

    public Cursor selectAllFromOldExamsSortByGrade() {
        return OldExamsQuery.getAllRecordsAndSortBy(database, ExamsContract.OldExamEntry.GRADE_COLUMN);
    }

    public Cursor getSubjectsWithGrade() {
        return SubjectsQuery.findSubjectsWithGradesAndSortBy(database, null);
    }

    public Cursor getSubjectGrades(String subjectTitle) {return OldExamsQuery.findGradesAndSortBy(database, subjectTitle, null);}

    public Cursor getOrderedSubjectGrades(String subjectTitle) {
        return OldExamsQuery.findGradesAndSortBy(database, subjectTitle, ExamsContract.OldExamEntry.GRADE_COLUMN);
    }


    public long examBecomesOld(Exam exam, double grade) {
        Subject subject = findSubjectByTitle(exam.getSubject());
        if (subject == null) return -1L;
        if (grade == Grade.EMPTY_GRADE) return -1L;

        ExamsQuery.remove(database, exam);

        setSubjectHasGrade(subject, true);
        return OldExamsQuery.insert(database, new OldExam(subject, exam.getInfo(), grade, exam.getDate()));


    }


    public void removeExam(long timeInMillis) {
        ExamsQuery.remove(database, timeInMillis);
    }
}
