package com.grudus.nativeexamshelper.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.helpers.ThemeHelper;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainSettingsFragment())
                .commit();
        setUpToolbar();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null)
            return;
        toolbar.setTitle(getString(R.string.title_activity_settings));
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar == null)
            return;
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public static class MainSettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener  {

        private final String NIGHT_MODE_KEY = MyApplication.getContext().getString(R.string.key_night_mode);
        private final String USER_NAME_KEY = MyApplication.getContext().getString(R.string.key_user_name);

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            TypedValue tv = new TypedValue();
            if (MyApplication.getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            {
                params.topMargin = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            }

            view.setLayoutParams(params);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(NIGHT_MODE_KEY)) {
                CheckBoxPreference pref = (CheckBoxPreference) findPreference(key);
                if (pref.isChecked())
                    ThemeHelper.changeToTheme(getActivity(), ThemeHelper.THEME_DARK);
                else
                    ThemeHelper.changeToTheme(getActivity(), ThemeHelper.THEME_DEFAULT);
            }

            else if (key.equals(USER_NAME_KEY)) {
                EditTextPreference pref = (EditTextPreference) findPreference(key);
                String name = pref.getText();
                pref.setSummary("Hello, " + name);
            }
        }

    }
}
