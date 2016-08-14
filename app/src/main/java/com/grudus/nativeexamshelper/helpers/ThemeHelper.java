package com.grudus.nativeexamshelper.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.MyApplication;


public class ThemeHelper {

    private static int sTheme;
    private static final int NUMBER_OF_THEMES = 2;

    public final static int THEME_DEFAULT = 0;
    public final static int THEME_DARK = 1;

    static {
        Context context = MyApplication.getContext();
        String key = context.getString(R.string.key_night_mode);
        boolean darkTheme = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, false);
        sTheme = darkTheme ? THEME_DARK : THEME_DEFAULT;
    }

    public static int getTheme() {
        return sTheme;
    }

    public static int nextTheme() {
        return (sTheme + 1) % NUMBER_OF_THEMES;
    }

    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }


    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (sTheme)
        {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.AppTheme_Dark);
                break;
        }
    }
}
