package com.grudus.nativeexamshelper.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.fragments.AddingExamFragment;
import com.grudus.nativeexamshelper.activities.fragments.OldExamsFragment;
import com.grudus.nativeexamshelper.adapters.ViewPagerAdapter;
import com.grudus.nativeexamshelper.activities.sliding.SlidingTabLayout;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.helpers.ThemeHelper;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;

public class ExamsMainActivity extends AppCompatActivity{

    public static final String TAG = "@@@ MAIN @@@";


    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tabs) SlidingTabLayout slidingTabLayout;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.nvView)
    NavigationView navigationView;

    private ExamsDbHelper examsDbHelper;
    private ViewPagerAdapter viewPagerAdapter;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_exams);
        ButterKnife.bind(this);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.list_item_to_toolbar));
//        }

        viewPagerInit();
        setUpToolbar();

        DateHelper.setDateFormat(getResources().getString(R.string.date_format));

        setNavigationViewHeaderSize();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.menu_item_change_theme) {
                    ThemeHelper.changeToTheme(ExamsMainActivity.this, ThemeHelper.nextTheme());
                }
                return true;
            }
        });




    }

    private void viewPagerInit() {
        String[] tabs = getResources().getStringArray(R.array.tab_titles);
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
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger_icon);
    }



    private void setNavigationViewHeaderSize() {
        final View header = navigationView.getHeaderView(0);

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


    private void initDatabase() {
        examsDbHelper = ExamsDbHelper.getInstance(this);
        examsDbHelper.openDB();
        Log.d(TAG, "initDatabase method: ");
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
            ((OldExamsFragment) viewPagerAdapter.getFragment(1)).removeAll();
            examsDbHelper.closeDB();

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
    protected void onPause() {
        super.onPause();
        if (examsDbHelper != null)
            examsDbHelper.closeDB();

    }

}
