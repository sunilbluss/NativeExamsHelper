package com.grudus.nativeexamshelper.helpers;

import android.app.Activity;
import android.content.Intent;

import com.grudus.nativeexamshelper.R;


public class ThemeHelper {

    private static int sTheme;
    private static final int NUMBER_OF_THEMES = 2;

    public final static int THEME_DEFAULT = 0;
    public final static int THEME_DARK = 1;

    static {
        sTheme = THEME_DEFAULT;
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
