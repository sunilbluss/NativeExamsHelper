package com.grudus.nativeexamshelper.activities;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.converters.CursorToArrayConverter;
import com.grudus.nativeexamshelper.net.RetrofitMain;

import rx.Subscription;
import rx.schedulers.Schedulers;


public class MyApplication extends Application {

    private static final String TAG = "@@@" + MyApplication.class.getSimpleName();
    private static Context context;

    private Subscription subscription;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = this.getApplicationContext();


        subscription = new CursorToArrayConverter(context)
                .getChangedSubjectsAsJson()
                .flatMap(array -> new RetrofitMain(context).insertSubjects(array))
                .flatMap(response -> ExamsDbHelper.getInstance(context).updateSubjectChangesToNull())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {},
                        error -> Log.e(TAG, "onCreate: " + error.getMessage(), error),
                        () -> Log.d(TAG, "onCreate: completed"));
    }

    public static Context getContext() {
        return context;
    }

}
