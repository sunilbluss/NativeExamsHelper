package com.grudus.nativeexamshelper.activities;

import android.app.Application;
import android.content.Context;


public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = this.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
