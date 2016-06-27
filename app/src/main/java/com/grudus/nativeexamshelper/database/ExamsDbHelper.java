package com.grudus.nativeexamshelper.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.database.exams.ExamsORMImpl;
import com.grudus.nativeexamshelper.database.exams.OldExamsORMImpl;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.database.subjects.SubjectsORMImpl;
import com.grudus.nativeexamshelper.pojos.Exam;
import com.grudus.nativeexamshelper.pojos.OldExam;
import com.grudus.nativeexamshelper.pojos.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExamsDbHelper extends SQLiteOpenHelper {

    public static final String TAG = "@@@ Main DB HELPER @@@";

    public static final String DATABASE_NAME = "ExamsHelper.db";
    public static final int DATABASE_VERSION = 7;

    private SQLiteDatabase database;
    private Context context;

    private static ExamsDbHelper instance;

    private ExamsORMImpl examsORM;
    private SubjectsORMImpl subjectsORM;
    private OldExamsORMImpl oldExamsORM;

    private ExamsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        examsORM = new ExamsORMImpl();
        subjectsORM = new SubjectsORMImpl();
        oldExamsORM = new OldExamsORMImpl();
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
        oldExamsORM.randomInsert(db);
        SubjectsORMImpl.setDefaultSubjects(context.getResources().getStringArray(R.array.default_subjects));
        SubjectsORMImpl.setDefaultColors(context.getResources().getStringArray(R.array.defaultSubjectsColors));
        subjectsORM.firstInsert(db);
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
        Log.d(TAG, "Database is opened");
    }

    public void openDBReadOnly() {
        database = this.getReadableDatabase();
        Log.d(TAG, "openDBReadOnly: opened");
    }

    public void closeDB() {
        if (database != null && database.isOpen())
            database.close();
        this.close();
        Log.d(TAG, "Database is closed");
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
        SubjectsORMImpl.setDefaultColors(context.getResources().getStringArray(R.array.defaultSubjectsColors));
        SubjectsORMImpl.setDefaultSubjects(context.getResources().getStringArray(R.array.default_subjects));
        subjectsORM.firstInsert(database);
        Log.d(TAG, "Subjects are fresh");
    }

    public void updateSubject(Subject old, Subject _new) {
        subjectsORM.update(database, old, _new);
    }

    public int setSubjectHasGrade(Subject subject, boolean hasGrade) {
        return subjectsORM.setSubjectHasGrade(database, subject, hasGrade);
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

    @Nullable
    public ArrayList<Exam> selectAllFromExamsWhereDateIsSmallerThan(long dateInMillis) {
        return examsORM.getAllExamsOlderThan(database, dateInMillis);
    }


//    Old exams part *****************************
    public Cursor selectAllFromOldExams() {
        return oldExamsORM.getAllRecordsAndSortBy(database, null);
    }

    public Cursor selectAllFromOldExamsSortByDate() {
        return oldExamsORM.getAllRecordsAndSortBy(database, ExamsContract.OldExamEntry.DATE_COLUMN);
    }

    public Cursor selectAllFromOldExamsSortByGrade() {
        return oldExamsORM.getAllRecordsAndSortBy(database, ExamsContract.OldExamEntry.GRADE_COLUMN);
    }

    public Cursor getSubjectsWithGrade() {
        return subjectsORM.findSubjectsWithGradesAndSortBy(database, null);
    }

    public Cursor getSubjectGrades(String subjectTitle) {return oldExamsORM.findGradesAndSortBy(database, subjectTitle, null);}

    public Cursor getOrderedSubjectGrades(String subjectTitle) {
        return oldExamsORM.findGradesAndSortBy(database, subjectTitle, ExamsContract.OldExamEntry.GRADE_COLUMN);
    }


    public long examBecomesOld(Exam exam) {
        Subject subject = findSubjectByTitle(exam.getSubject());
        if (subject == null) return -1L;

        examsORM.remove(database, exam);

        // TODO: 25.06.16 debug only
        double grade = OldExam.POSSIBLE_GRADES[new Random().nextInt(OldExam.POSSIBLE_GRADES.length)];
        setSubjectHasGrade(subject, grade != OldExam.POSSIBLE_GRADES[0]);

        return oldExamsORM.insert(database, new OldExam(subject, exam.getInfo(), grade, exam.getDate()));
    }

    public boolean notAssessedSubjectExists() {
        return oldExamsORM.notAssessedSubjectExists(database);
    }


}
