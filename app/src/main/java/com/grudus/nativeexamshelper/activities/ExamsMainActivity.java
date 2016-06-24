package com.grudus.nativeexamshelper.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.SubjectsListActivity;
import com.grudus.nativeexamshelper.activities.sliding.AddingExamFragment;
import com.grudus.nativeexamshelper.activities.sliding.ViewPagerAdapter;
import com.grudus.nativeexamshelper.activities.sliding.goohub.SlidingTabLayout;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExamsMainActivity extends AppCompatActivity  {

    public static final String TAG = "@@@ MAIN @@@";


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tabs) SlidingTabLayout slidingTabLayout;

    private static Context mainApplicationContext;
    private ExamsDbHelper examsDbHelper;
    private String[] tabs;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on create main");
        mainApplicationContext = this;
        setContentView(R.layout.activity_main_exams);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        tabs = getResources().getStringArray(R.array.tab_titles);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabs);

        viewPager.setAdapter(viewPagerAdapter);

        slidingTabLayout.setDistributeEvenly(true);

        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getApplicationContext(), R.color.tabsScrollColor);
            }
        });

        slidingTabLayout.setViewPager(viewPager);

        Log.d(TAG, "onCreate: ");

    }


    private void initDatabase() {
        examsDbHelper = new ExamsDbHelper(this);
        examsDbHelper.openDB();
    }

    @Nullable
    public static Context getMainApplicationContext() {
        return mainApplicationContext;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_deleteAll) {
            Toast.makeText(this, "Usunieto wszystko", Toast.LENGTH_SHORT).show();
            initDatabase();

            ((AddingExamFragment) viewPagerAdapter.getFragment(0)).removeAll();

            return true;
        }

        if (item.getItemId() == R.id.menu_item_refresh_subjects) {
            Toast.makeText(this, "Przedmioty sa odswiezone", Toast.LENGTH_SHORT).show();
            initDatabase();
            examsDbHelper.refreshSubjects();
            examsDbHelper.closeDB();
            return true;
        }

        if (item.getItemId() == R.id.menu_item_edit_subjects) {
            Intent intent = new Intent(getApplicationContext(), SubjectsListActivity.class);
            intent.putExtra(SubjectsListActivity.INTENT_EDITABLE_TAG, true);
            startActivity(intent);
        }

        else Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (examsDbHelper != null) {
            examsDbHelper.closeDB();
        }
    }
}
