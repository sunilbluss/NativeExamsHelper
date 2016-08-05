package com.grudus.nativeexamshelper.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.grudus.nativeexamshelper.activities.touchhelpers.ItemRemoveCallback;
import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.adapters.ItemClickListener;
import com.grudus.nativeexamshelper.adapters.SubjectsAdapter;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.dialogs.EditSubjectDialog;
import com.grudus.nativeexamshelper.helpers.ThemeHelper;
import com.grudus.nativeexamshelper.pojos.Subject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SubjectsListActivity extends AppCompatActivity implements ItemClickListener {

    private final String TAG = "@@@" + this.getClass().getSimpleName();

    @BindView(R.id.subjects_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.floating_button_add_subject) FloatingActionButton floatingActionButton;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private ExamsDbHelper examsDbHelper;
    private SubjectsAdapter adapter;
    private ItemTouchHelper itemTouchHelper;

    private Subscription subscription;

    /* If true - after click on listview item it is possible to change color and title
     *  otherwise - it's on selected mode (user is choosing exam subject)*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ON CREATE SLACTIVITY");
        setContentView(R.layout.activity_subjects_list);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.subject_list_toolbar_text));
        setSupportActionBar(toolbar);

        initDatabase();
        populateList();
        
        initSwipeListener();
    }

    private void initSwipeListener() {
        ItemRemoveCallback itemRemoveCallback = new ItemRemoveCallback(0, ItemTouchHelper.RIGHT, this);
        itemTouchHelper = new ItemTouchHelper(itemRemoveCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initDatabase() {
        examsDbHelper = ExamsDbHelper.getInstance(this);
        examsDbHelper.openDB();
    }

    private void populateList() {
        subscription =
            examsDbHelper.getAllSubjectsSortByTitle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cursor -> {
                    adapter = new SubjectsAdapter(cursor, SubjectsListActivity.this, SubjectsListActivity.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SubjectsListActivity.this));
                });

    }

    @Override
    public void itemClicked(View v, int position) {
        Subject subject = adapter.getItem(position);
        new EditSubjectDialog()
                .addSubject(subject)
                .addListener(editedSubject -> {
                    examsDbHelper.updateSubject(subject, editedSubject)
                            .flatMap(howMany -> examsDbHelper.getAllSubjectsSortByTitle())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(cursor -> {
                                adapter.changeCursor(cursor);
                                adapter.notifyItemChanged(position);
                            });

                })
                .show(getFragmentManager(), "qqq");

    }


    @OnClick(R.id.floating_button_add_subject)
    public void addSubject() {
        Intent openAddNewSubjectActivity = new Intent(getApplicationContext(), AddNewSubjectActivity.class);
        startActivity(openAddNewSubjectActivity);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (examsDbHelper != null)
            examsDbHelper.closeDB();
        adapter.closeCursor();
        if (!subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

}
