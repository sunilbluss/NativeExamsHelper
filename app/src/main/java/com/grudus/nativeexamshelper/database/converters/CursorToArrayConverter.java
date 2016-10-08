package com.grudus.nativeexamshelper.database.converters;


import android.content.Context;
import android.database.Cursor;

import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.pojos.JsonSubject;
import com.grudus.nativeexamshelper.pojos.UserPreferences;

import java.util.ArrayList;

import rx.Observable;

public class CursorToArrayConverter {

    private final Context context;
    private final ExamsDbHelper examsDbHelper;

    public CursorToArrayConverter(Context context) {
        this.context = context;
        examsDbHelper = ExamsDbHelper.getInstance(context);
    }

    private Observable<ArrayList<JsonSubject>> getSubjectsAsJson(Observable<Cursor> cursorObservable) {
        return cursorObservable
                .flatMap(cursor -> {
                    ArrayList<JsonSubject> subjects = new ArrayList<>(cursor.getCount());

                    if (cursor.moveToFirst()) {
                        do {
                            JsonSubject subject = getSubject(cursor);
                            subjects.add(subject);
                        } while (cursor.moveToNext());
                    }

                    cursor.close();
                    return Observable.create(subscriber -> {
                        subscriber.onNext(subjects);
                        subscriber.onCompleted();
                    });
                });
    }

    private JsonSubject getSubject(Cursor cursor) {
        return new JsonSubject(
                cursor.getLong(SubjectsContract.SubjectEntry.INDEX_COLUMN_INDEX),
                new UserPreferences(context).getLoggedUser().getId(),
                cursor.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX),
                cursor.getString(SubjectsContract.SubjectEntry.COLOR_COLUMN_INDEX),
                cursor.getString(SubjectsContract.SubjectEntry.CHANGE_COLUMN_INDEX));
    }


    public Observable<ArrayList<JsonSubject>> getChangedSubjectsAsJson() {
        return getSubjectsAsJson(examsDbHelper.getAllSubjectsWithChange());
    }


}
