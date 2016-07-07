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
import com.grudus.nativeexamshelper.adapters.SubjectsAdapter;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.pojos.Subject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubjectsListActivity extends AppCompatActivity implements SubjectsAdapter.ItemClickListener {

    private final String TAG = "@@@" + this.getClass().getSimpleName();

    // info for AddNewSubjectActivity - when is 'true' then we are editing the existing subject
    public static final String INTENT_EDITABLE_TAG = "editable";

    // info for AddNewSubjectActivity - when is 'true' we are creating new subject, but we are still in
    // 'edit subjects' mode
    public static final String INTENT_EDIT_MODE_TAG = "editMode";

    @BindView(R.id.subjects_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.floating_button_add_subject) FloatingActionButton floatingActionButton;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private ExamsDbHelper examsDbHelper;
    private SubjectsAdapter adapter;
    private ItemTouchHelper itemTouchHelper;

    /* If true - after click on listview item it is possible to change color and title
     *  otherwise - it's on selected mode (user is choosing exam subject)*/
    private boolean isEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ON CREATE SLACTIVITY");
        setContentView(R.layout.activity_subjects_list);
        ButterKnife.bind(this);

        isEditable = getIntent().getBooleanExtra(INTENT_EDITABLE_TAG, false);
        Log.i(TAG, "onCreate: is editable? " + isEditable);

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
        Cursor c = examsDbHelper.selectAllFromSubjectsSortByTitle();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new SubjectsAdapter(c, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

    }

    @Override
    public void itemClicked(View v, int position) {
        Subject subject = adapter.getItem(position);

        Intent whereToGo = isEditable ? new Intent(this, AddNewSubjectActivity.class)
                : new Intent(this, AddExamActivity.class);

        whereToGo.putExtra("subject", subject);
        startActivity(whereToGo);
    }


    @OnClick(R.id.floating_button_add_subject)
    public void addSubject() {
        Intent openAddNewSubjectActivity = new Intent(getApplicationContext(), AddNewSubjectActivity.class);
        openAddNewSubjectActivity.putExtra(INTENT_EDIT_MODE_TAG, isEditable);
        startActivity(openAddNewSubjectActivity);
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        if (isEditable) {
            this.startActivity(new Intent(this, ExamsMainActivity.class));
            finish();
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (examsDbHelper != null)
            examsDbHelper.closeDB();
        adapter.closeCursor();
    }

}
