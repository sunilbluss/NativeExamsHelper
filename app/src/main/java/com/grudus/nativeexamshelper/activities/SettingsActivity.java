package com.grudus.nativeexamshelper.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.dialogs.EnterTextDialog;
import com.grudus.nativeexamshelper.helpers.ThemeHelper;
import com.grudus.nativeexamshelper.pojos.grades.Grade;
import com.grudus.nativeexamshelper.pojos.grades.Grades;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.onActivityCreateSetTheme(this);

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
        private final String GRADE_TYPE_KEY = MyApplication.getContext().getString(R.string.key_grades_type);
        private final String GRADE_DECIMAL_KEY = MyApplication.getContext().getString(R.string.key_grades_decimal);

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

            TypedValue typedValue = new TypedValue();
            getActivity().getTheme().resolveAttribute(R.attr.background, typedValue, true);

            getListView().setBackgroundColor(typedValue.data);

        }

        private ListView getListView() {
            View view = getView();
            if (view == null) {
                throw new IllegalStateException("Content view not yet created");
            }

            View listView = view.findViewById(android.R.id.list);
            if (!(listView instanceof ListView)) {
                throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class");
            }
            return (ListView) listView;
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
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getKey().equals(USER_NAME_KEY)) {
                new EnterTextDialog()
                        .addTitle(getString(R.string.pref_user_name))
                        .addListener(text -> {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString(USER_NAME_KEY, text);
                            editor.commit();
                        })
                        .show(getFragmentManager(), "qqqa");
                return false;
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
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


            else if (key.equals(GRADE_TYPE_KEY)) {
                ListPreference pref = (ListPreference) findPreference(key);
                Grades.setGradeMode(pref.getValue());
            }

            else if (key.equals(GRADE_DECIMAL_KEY)) {
                SwitchPreference pref = (SwitchPreference) findPreference(key);
                Grades.enableDecimalsInGrades(!pref.isChecked());
            }

        }

    }
}
