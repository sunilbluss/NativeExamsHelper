package com.grudus.nativeexamshelper.pojos;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.MyApplication;



public class UserPreferences {

    private Context context;

    private final String KEY_ID;
    private final String KEY_USERNAME;
    private final String KEY_TOKEN;
    private final String KEY_IS_LOGGED;

    public UserPreferences(Context context) {
        this.context = context;

        KEY_ID = context.getString(R.string.key_user_id);
        KEY_USERNAME = context.getString(R.string.key_user_name);
        KEY_TOKEN = context.getString(R.string.key_user_auth_token);
        KEY_IS_LOGGED = context.getString(R.string.key_user_is_logged);
    }

    public User getLoggedUser() {
        if (context == null)
            context = MyApplication.getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return new User(
                preferences.getLong(KEY_ID, -1L),
                preferences.getString(KEY_USERNAME, ""),
                preferences.getString(KEY_TOKEN, ""),
                preferences.getBoolean(KEY_IS_LOGGED, false)
        );
    }


    public void changeUsername(String username) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_USERNAME, username)
                .commit();
    }

    public void changeId(Long id) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(KEY_ID, id)
                .commit();
    }

    public void changeToken(String token) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_TOKEN, token)
                .commit();
    }

    public void changeLoginStatus(boolean isLogged) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        editor.putBoolean(KEY_IS_LOGGED, isLogged);
        if (!isLogged)
            editor.putString(KEY_TOKEN, "");
        editor.apply();
    }




    public static class User {
    
        private final Long id;
        private final String username;
        private final String token;
        private final boolean isLogged;


        private User(Long id, String username, String token, boolean isLogged) {
            this.id = id;
            this.username = username;
            this.token = token;
            this.isLogged = isLogged;
        }

        public String getToken() {
            return token;
        }
    
        public String getUsername() {
            return username;
        }
    
        public Long getId() {
            return id;
        }
        
        public boolean isLogged() {
            return isLogged;
        }
    
    }
}
