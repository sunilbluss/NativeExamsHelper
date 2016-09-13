package com.grudus.nativeexamshelper.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.dialogs.reusable.ConfirmDialog;
import com.grudus.nativeexamshelper.dialogs.reusable.EnterTextDialog;
import com.grudus.nativeexamshelper.dialogs.reusable.RadioDialog;
import com.grudus.nativeexamshelper.helpers.ColorHelper;
import com.grudus.nativeexamshelper.helpers.ThemeHelper;
import com.grudus.nativeexamshelper.pojos.grades.Grades;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        private final String FABRIC_EXAMS_KEY = MyApplication.getContext().getString(R.string.key_fabric_exams);
        private final String FABRIC_SUBJECTS_KEY = MyApplication.getContext().getString(R.string.key_fabric_subjects);

        private Subscription subscription;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            setMargins(view);
            getListView().setBackgroundColor(ColorHelper.getThemeColor(getActivity(), R.attr.background));

        }

        private void setMargins(View view) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            TypedValue tv = new TypedValue();
            if (MyApplication.getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            {
                params.topMargin = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            }

            view.setLayoutParams(params);
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

            if (subscription != null && !subscription.isUnsubscribed())
                subscription.unsubscribe();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getKey().equals(USER_NAME_KEY)) {
                new EnterTextDialog()
                        .addTitle(getString(R.string.pref_user_name))
                        .addText(
                                preference.getSharedPreferences().getString(USER_NAME_KEY, "")
                        )
                        .addListener(text -> {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString(USER_NAME_KEY, text);
                            editor.commit();
                        })
                        .show(getFragmentManager(), getString(R.string.tag_dialog_edit));
                return false;
            }

            else if (preference.getKey().equals(GRADE_TYPE_KEY)) {

                new ConfirmDialog()
                        .addTitle(getString(R.string.dialog_confirm_pref_title))
                        .addText(getString(R.string.dialog_confirm_pref_content))
                        .addListener((dialog, which) -> {
                            if (which == DialogInterface.BUTTON_POSITIVE)
                                new RadioDialog()
                                    .addTitle(getString(R.string.pref_grades))
                                    .addDisplayedValues(getResources().getStringArray(R.array.pref_grades_entries))
                                    .addSelectedItemIndex(PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(GRADE_TYPE_KEY, 0))
                                    .addListener(((selectedIndex, selectedValue) -> {
                                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                                        editor.putInt(GRADE_TYPE_KEY, selectedIndex);
                                        editor.commit();
                                        Grades.setGradeMode(selectedIndex);
                                    }))
                                    .show(getFragmentManager(), getString(R.string.tag_dialog_radio));
                        }).show(getFragmentManager(), getString(R.string.tag_dialog_confirm));

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

            else if (key.equals(GRADE_DECIMAL_KEY)) {
                SwitchPreference pref = (SwitchPreference) findPreference(key);
                Grades.enableDecimalsInGrades(!pref.isChecked());
            }

            else if (key.equals(GRADE_TYPE_KEY)) {
                ExamsDbHelper helper = ExamsDbHelper.getInstance(getActivity());
                helper.openDB();

                subscription = helper.removeAllOldExams()
                        .subscribeOn(Schedulers.io())
                        .subscribe((howMany) -> {},
                                error -> helper.closeDB(),
                                helper::closeDB);
            }

            else if (key.equals(FABRIC_EXAMS_KEY)) {
                ExamsDbHelper helper = ExamsDbHelper.getInstance(getActivity());
                subscription =
                        helper.removeAllIncomingExams()
                        .flatMap(integer -> helper.removeAllOldExams())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(i -> {},
                                error -> helper.closeDB(),
                                helper::closeDB);
            }

        }

    }
}
