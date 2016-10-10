package com.grudus.nativeexamshelper.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.database.exams.ExamsQuery;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.database.subjects.SubjectsQuery;
import com.grudus.nativeexamshelper.pojos.Exam;
import com.grudus.nativeexamshelper.pojos.Subject;

import rx.Observable;

public class ExamsDbHelper extends SQLiteOpenHelper {

    public static final String TAG = "@@@ Main DB HELPER @@@";

    public static final String DATABASE_NAME = "ExamsHelper.db";
    public static final int DATABASE_VERSION = 12;

    private SQLiteDatabase database;
    private Context context;

    private static ExamsDbHelper instance;


    @Deprecated // test only
    public ExamsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        instance = this;
        Log.d(TAG, "ExamsDbHelper() constructor");
    }

    public static ExamsDbHelper getInstance(Context context) {
        if (instance == null)
            instance = new ExamsDbHelper(context.getApplicationContext());
        return instance;
    }

    @Deprecated //test only
    public static void setInstance(ExamsDbHelper examsDbHelper) {
        instance = examsDbHelper;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate() ExamsDbHelper");
        db.execSQL(ExamsContract.ExamEntry.CREATE_TABLE_QUERY);
        db.execSQL(SubjectsContract.SubjectEntry.CREATE_TABLE_QUERY);

        SubjectsQuery.setDefaultSubjects(context.getResources().getStringArray(R.array.default_subjects));
        SubjectsQuery.setDefaultColors(context.getResources().getStringArray(R.array.defaultSubjectsColors));
        SubjectsQuery.firstInsert(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ExamsContract.ExamEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SubjectsContract.SubjectEntry.TABLE_NAME);

        onCreate(db);
    }

    public void openDBIfClosed() {
        if (database == null || !database.isOpen())
            database = this.getWritableDatabase();
    }

    public void openDB() {
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
        super.close();
//        Log.d(TAG, "Database is closed");
    }


//    Subjects part ******************************

    public Observable<Cursor> getAllSubjectsSortByTitle() {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.getAllRecordsAndSortByTitle(database));
            subscriber.onCompleted();
        });
    }

    public Observable<Cursor> getAllSubjectsWithoutDeleteChangeSortByTitle() {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.getAllRecordsWithoutDeleteChangeSortByTitle(database));
            subscriber.onCompleted();
        });
    }

    public Observable<Long> insertSubject(Subject subject) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.insert(database, subject));
            subscriber.onCompleted();
        });
    }

    public Observable<Integer> refreshSubjects() {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.deleteAll(database));
            SubjectsQuery.setDefaultColors(context.getResources().getStringArray(R.array.defaultSubjectsColors));
            SubjectsQuery.setDefaultSubjects(context.getResources().getStringArray(R.array.default_subjects));
            subscriber.onNext(SubjectsQuery.firstInsert(database));
        });
    }

    public Observable<Integer> updateSubject(Subject old, Subject _new) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.update(database, old, _new));
            subscriber.onCompleted();
        });
    }

    public Observable<Integer> setSubjectHasGrade(Subject subject, boolean hasGrade) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.setSubjectHasGrade(database, subject, hasGrade));
            subscriber.onCompleted();
        });
    }



    public Observable<Subject> findSubjectById(Long id) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.findById(database, id));
            subscriber.onCompleted();
        });
    }

    public Observable<Subject> findSubjectByTitle(String title) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.findByTitle(database, title));
            subscriber.onCompleted();
        });
    }

    public Observable<Integer> updateSubjectSetChangeDelete(Subject subject) {
        return updateSubjectChange(subject, SubjectsContract.CHANGE_DELETED);
    }

    public Observable<Integer> removeSubject(String subjectTitle) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.removeSubject(database, subjectTitle));
            subscriber.onCompleted();
        });
    }

    public Observable<Integer> removeAllExamsRelatedWithSubject(Long subjectId) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(ExamsQuery.removeSubjectExams(database, subjectId));
            subscriber.onCompleted();
        });
    }


    public Observable<Cursor> getAllSubjectsWithChange() {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.findChangedSubjects(database));
            subscriber.onCompleted();
        });
    }

    public Observable<Integer> updateSubjectChange(Subject subject, String change) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.updateChange(database, subject, change));
            subscriber.onCompleted();
        });
    }

    public Observable<Integer> updateSubjectChangesToNull() {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(SubjectsQuery.removeDeletedSubjects(database));
            subscriber.onCompleted();
        });
    }

//    Exams part *********************************

    public Observable<Integer> updateExamsChangeToNull() {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(ExamsQuery.updateChangesToNull(database));
            subscriber.onCompleted();
        });

    }

    public Observable<Long> insertExam(Exam exam) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(ExamsQuery.insert(database, exam));
            subscriber.onCompleted();
        });
    }

    public Observable<Cursor> getExamsOlderThan(long time) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(ExamsQuery.getAllExamsOlderThan(database, time));
            subscriber.onCompleted();
        });
    }



    public Observable<Cursor> getSubjectsWithGrade() {
        return Observable.create(subscriber -> {
            openDBIfClosed();
           subscriber.onNext(SubjectsQuery.findSubjectsWithGradesAndSortBy(database, null));
            subscriber.onCompleted();
        });
    }

    public Observable<Cursor> getSubjectGrades(Long subjectId) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(ExamsQuery.findGradesAndSortBy(database, subjectId, null));
            subscriber.onCompleted();
        });
    }

    public Observable<Double> getGradesFromOrderedSubjectGrades(Long subjectId) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            Cursor cursor = ExamsQuery.findGradesAndSortBy(database, subjectId, ExamsContract.ExamEntry.GRADE_COLUMN);
            cursor.moveToFirst();
            // first returned item is size of the items
            subscriber.onNext((double)cursor.getCount());
            do {
                double temp = cursor.getDouble(ExamsContract.ExamEntry.GRADE_COLUMN_INDEX);
                subscriber.onNext(temp);
                Log.d(TAG, "getGradesFromOrderedSubjectGrades: kursor");
            } while (cursor.moveToNext());
            Log.d(TAG, "getGradesFromOrderedSubjectGrades: koniec kursora");
            cursor.close();
            subscriber.onCompleted();
        });
    }

    private Observable<Boolean> removeExam(Exam exam) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(ExamsQuery.remove(database, exam));
            subscriber.onCompleted();
        });
    }



    public Observable<Integer> updateExamSetGrade(Exam exam, double grade) {
        openDBIfClosed();

        return findSubjectById(exam.getSubjectId())
                .flatMap(subject -> setSubjectHasGrade(subject, true))
                .flatMap((updatedRows) -> Observable.create(subscriber -> {
                    subscriber.onNext(ExamsQuery.updateSetGrade(database, exam, grade));
                    subscriber.onCompleted();
                }));
    }


    public Observable<Cursor> getAllIncomingExamsSortByDate() {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(ExamsQuery.getAllIncomingExamsAndSortByDate(database));
            subscriber.onCompleted();
        });
    }


    public Observable<Boolean> removeExam(long timeInMillis) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(ExamsQuery.remove(database, timeInMillis));
            subscriber.onCompleted();
        });
    }

    public Observable<Integer> removeAllIncomingExams() {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(ExamsQuery.removeAll(database));
            subscriber.onCompleted();
        });
    }


    public Observable<Cursor> getAllExamsWithChange() {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(ExamsQuery.getAllExamsWithChange(database));
            subscriber.onCompleted();
        });
    }


}
