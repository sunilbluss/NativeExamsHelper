package com.grudus.nativeexamshelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.fragments.AddingExamFragment;
import com.grudus.nativeexamshelper.activities.fragments.OldExamsFragment;
import com.grudus.nativeexamshelper.activities.sliding.SlidingTabLayout;
import com.grudus.nativeexamshelper.adapters.ViewPagerAdapter;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.helpers.ThemeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.schedulers.Schedulers;

public class ExamsMainActivity extends AppCompatActivity{

    public static final String TAG = "@@@ MAIN @@@";


    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tabs) SlidingTabLayout slidingTabLayout;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nvView) NavigationView navigationView;

    private ExamsDbHelper examsDbHelper;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_exams);
        ButterKnife.bind(this);

        initDatabase();

        initViewPager();
        setUpToolbar();
        setUpNavigationView();

        DateHelper.setDateFormat(getResources().getString(R.string.date_format));

    }

    private void initDatabase() {
        examsDbHelper = ExamsDbHelper.getInstance(this);
        examsDbHelper.openDB();
    }

    private void initViewPager() {
        String[] tabs = getResources().getStringArray(R.array.tab_titles);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabs);

        viewPager.setAdapter(viewPagerAdapter);

        slidingTabLayout.setDistributeEvenly(true);

        slidingTabLayout.setCustomTabColorizer(position ->
                ContextCompat.getColor(getApplicationContext(), R.color.tabsScrollColor));

        slidingTabLayout.setViewPager(viewPager);
    }


    private void setUpToolbar() {
        toolbar.setTitle(getString(R.string.toolbar_main_title));
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.hamburger_icon);
    }


    private void setUpNavigationView() {
        setNavigationViewHeaderSize();
        setUpNavigationViewListener();
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    private void setNavigationViewHeaderSize() {
        final View header = navigationView.getHeaderView(0);

        TextView title = (TextView) header.findViewById(R.id.hamburger_title);
        title.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(R.string.key_user_name), "Szczęściarz"
        ));

        header.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                header.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = header.getWidth();

                ViewGroup.LayoutParams params = header.getLayoutParams();
                params.width = width;
                params.height = (int) (width * (9f/16f));

                header.setLayoutParams(params);
            }
        });
    }



    private void setUpNavigationViewListener() {
        navigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.menu_item_change_theme:
                    ThemeHelper.changeToTheme(ExamsMainActivity.this, ThemeHelper.nextTheme());
                    break;

                case R.id.menu_item_web:
                    this.startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
                    break;

//                case R.id.menu_item_refresh_subjects:
//                    examsDbHelper.refreshSubjects()
//                            .subscribeOn(Schedulers.io())
//                            .subscribe();
//                    break;

                case R.id.menu_item_edit_subjects:
                    this.startActivity(new Intent(getApplicationContext(), SubjectsListActivity.class));
                    break;

                case R.id.menu_item_settings:
                    this.startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
            drawerLayout.closeDrawers();
            return false;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        examsDbHelper.closeDB();
        ((AddingExamFragment) viewPagerAdapter.getFragment(0)).closeDatabase();
        ((OldExamsFragment) viewPagerAdapter.getFragment(1)).closeDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        examsDbHelper.openDB();
    }
}
