package com.grudus.nativeexamshelper.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ExamsDbHelper extends SQLiteOpenHelper {

    public static final String TAG = "@@@ Main DB HELPER @@@";

    public static final String DATABASE_NAME = "ExamsHelper.db";
    public static final int DATABASE_VERSION = 11;

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

    public Observable<Integer> removeAllExamsRelatedWithSubject(String subjectTitle) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(ExamsQuery.removeSubjectExams(database, subjectTitle));
            subscriber.onNext(OldExamsQuery.removeSubjectExams(database, subjectTitle));
            subscriber.onCompleted();
        });
    }

    public Observable<Integer> removeAllOldSubjectExams(Subject subject) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(OldExamsQuery.removeSubjectExams(database, subject.getTitle()));
            subscriber.onNext(SubjectsQuery.setSubjectHasGrade(database, subject, false));
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
            subscriber.onNext(SubjectsQuery.updateAllChangesToNull(database));
            subscriber.onCompleted();
        });
    }

//    Exams part *********************************

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

    public Observable<Cursor> getSubjectGrades(String subjectTitle) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(OldExamsQuery.findGradesAndSortBy(database, subjectTitle, null));
            subscriber.onCompleted();
        });
    }

    public Observable<Double> getGradesFromOrderedSubjectGrades(String subjectTitle) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            Cursor cursor = OldExamsQuery.findGradesAndSortBy(database, subjectTitle, ExamsContract.OldExamEntry.GRADE_COLUMN);
            cursor.moveToFirst();
            // first returned item is size of the items
            subscriber.onNext((double)cursor.getCount());
            do {
                double temp = cursor.getDouble(ExamsContract.OldExamEntry.GRADE_COLUMN_INDEX);
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

    private Observable<Long> insertOldExam(OldExam exam) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(OldExamsQuery.insert(database, exam));
            subscriber.onCompleted();
        });
    }


    public Observable<Long> examBecomesOld(Exam exam, double grade) {
        final OldExam oldExam = new OldExam(null, exam.getInfo(), grade, exam.getDate());
        if (grade == Grade.EMPTY_GRADE) return Observable.empty();
        openDBIfClosed();

        return findSubjectByTitle(exam.getSubject())
                .flatMap(subject -> {
                    if (subject == null) return Observable.empty();
                    oldExam.setSubject(subject);
                    return setSubjectHasGrade(subject, true);
                })
                .flatMap((updatedRows) -> removeExam(exam))
                .flatMap((success) -> insertOldExam(oldExam));
    }

    public Observable<Object> removeOldExam(long timeInMillis) {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(OldExamsQuery.removeExam(database, timeInMillis));
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

    public Observable<Integer> removeAllOldExams() {
        return Observable.create(subscriber -> {
            openDBIfClosed();
            subscriber.onNext(OldExamsQuery.removeAll(database));
            subscriber.onNext(SubjectsQuery.resetGrades(database));
            subscriber.onCompleted();
        });
    }
}
