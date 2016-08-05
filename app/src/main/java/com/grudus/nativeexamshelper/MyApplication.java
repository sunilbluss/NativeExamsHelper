package com.grudus.nativeexamshelper;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MyApplication extends Application {

//    private RefWatcher refWatcher;
//
//    public static RefWatcher getRefWatcher(Context context) {
//        MyApplication app = (MyApplication) context.getApplicationContext();
//        return app.refWatcher;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("@@@@@@@@@", "onCreate: APPLICATION");
//        refWatcher = LeakCanary.install(this);
    }



}
