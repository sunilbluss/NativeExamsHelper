package com.grudus.nativeexamshelper.database.converters;


import android.content.Context;

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

    public Observable<ArrayList<JsonSubject>> getAllSubjectsAsJson() {
        return examsDbHelper.getAllSubjectsSortByTitle()
                .flatMap(cursor -> {
                    ArrayList<JsonSubject> subjects = new ArrayList<>(cursor.getCount());

                    cursor.moveToFirst();

                    do {
                        JsonSubject subject = new JsonSubject(
                                cursor.getLong(SubjectsContract.SubjectEntry.INDEX_COLUMN_INDEX),
                                new UserPreferences(context).getLoggedUser().getId(),
                                cursor.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX),
                                cursor.getString(SubjectsContract.SubjectEntry.COLOR_COLUMN_INDEX)
                        );

                        subjects.add(subject);
                    } while (cursor.moveToNext());
                    return Observable.create(subscriber -> {
                        subscriber.onNext(subjects);
                        subscriber.onCompleted();
                    });
                });
    }


}
