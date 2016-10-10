package com.grudus.nativeexamshelper.database.converters;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.pojos.JsonExam;
import com.grudus.nativeexamshelper.pojos.JsonSubject;
import com.grudus.nativeexamshelper.pojos.UserPreferences;

import java.util.ArrayList;
import java.util.Date;

import rx.Observable;

import static com.grudus.nativeexamshelper.activities.ExamsMainActivity.TAG;

public class CursorToArrayConverter {

    private final Context context;
    private final ExamsDbHelper examsDbHelper;
    private final Long userId;

    public CursorToArrayConverter(Context context) {
        this.context = context;
        examsDbHelper = ExamsDbHelper.getInstance(context);
        userId = new UserPreferences(context).getLoggedUser().getId();
    }

    public <T> Observable<ArrayList<T>> getObjectsAsJson(Observable<Cursor> cursorObservable, Jsonable<T> converter) {
        return cursorObservable
                .flatMap(cursor -> {
                    ArrayList<T> jsonObjects = new ArrayList<T>(cursor.getCount());

                    Log.d(TAG, "getObjectsAsJson: find " + jsonObjects.size() + " elements");

                    if (cursor.moveToFirst()) {
                        do {
                            T json = converter.getJson(cursor);
                            Log.d(TAG, "getObjectsAsJson: " + json);
                            jsonObjects.add(json);
                        } while (cursor.moveToNext());

                    }

                    Log.d(TAG, "getObjectsAsJson: after cursor loop");
                    cursor.close();
                    return Observable.create(subscriber -> {
                        subscriber.onNext(jsonObjects);
                        subscriber.onCompleted();
                    });
                });
    }

    private Observable<ArrayList<JsonSubject>> getSubjectsAsJson(Observable<Cursor> cursorObservable) {
        return getObjectsAsJson(cursorObservable, getSubject());
    }

    public Observable<ArrayList<JsonExam>> getChangedExamsAsJson() {
        Log.d(TAG, "getChangedExamsAsJson: ");
        return getObjectsAsJson(examsDbHelper.getAllExamsWithChange(), getExam());
    }

    private Jsonable<JsonSubject> getSubject() {
        return cursor ->
            new JsonSubject(
                    cursor.getLong(SubjectsContract.SubjectEntry.INDEX_COLUMN_INDEX),
                    userId,
                    cursor.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX),
                    cursor.getString(SubjectsContract.SubjectEntry.COLOR_COLUMN_INDEX),
                    cursor.getString(SubjectsContract.SubjectEntry.CHANGE_COLUMN_INDEX));
    }


    public Observable<ArrayList<JsonSubject>> getChangedSubjectsAsJson() {
        Log.d(TAG, "getChangedSubjectsAsJson: ");
        return getSubjectsAsJson(examsDbHelper.getAllSubjectsWithChange());
    }

    public Jsonable<JsonExam> getExam() {
        return cursor ->
                new JsonExam(
                        cursor.getLong(ExamsContract.ExamEntry.INDEX_COLUMN_INDEX),
                        // TODO: 10.10.16 CHANGE, PLEASE
                        cursor.getLong(ExamsContract.ExamEntry.SUBJECT_ID_COLUMN_INDEX),
                        userId,
                        cursor.getString(ExamsContract.ExamEntry.INFO_COLUMN_INDEX),
                        new Date(cursor.getLong(ExamsContract.ExamEntry.DATE_COLUMN_INDEX)),
                        cursor.getString(ExamsContract.ExamEntry.CHANGE_COLUMN_INDEX));


    }


    interface Jsonable<T> {
        T getJson(Cursor cursor);
    }


}
