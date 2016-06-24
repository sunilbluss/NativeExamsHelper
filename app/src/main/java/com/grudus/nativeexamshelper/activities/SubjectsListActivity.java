package com.grudus.nativeexamshelper.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.database.subjects.SubjectsCursorAdapter;
import com.grudus.nativeexamshelper.pojos.Subject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class SubjectsListActivity extends AppCompatActivity {

    private final String TAG = "@@@" + this.getClass().getSimpleName();

    // info for AddNewSubjectActivity - when is 'true' then we are editing the existing subject
    public static final String INTENT_EDITABLE_TAG = "editable";

    // info for AddNewSubjectActivity - when is 'true' we are creating new subject, but we are still in
    // 'edit subjects' mode
    public static final String INTENT_EDIT_MODE_TAG = "editMode";

    @BindView(R.id.subjects_list_view) ListView listView;
    @BindView(R.id.floating_button_add_subject) FloatingActionButton floatingActionButton;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private ExamsDbHelper examsDbHelper;
    private CursorAdapter cursorAdapter;

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
    }

    private void initDatabase() {
        Log.d(TAG, "Should init database SLActivity");
        examsDbHelper = new ExamsDbHelper(this);
        examsDbHelper.openDB();
    }

    private void populateList() {
        Cursor c = examsDbHelper.selectAllFromSubjectsSortByTitle();
        cursorAdapter = new SubjectsCursorAdapter(this, c, 0);
        listView.setAdapter(cursorAdapter);
    }

    @OnItemClick(R.id.subjects_list_view)
    public void setSubject(int index) {
        Cursor c = (Cursor) cursorAdapter.getItem(index);
        Subject subject = new Subject(
                c.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX),
                c.getString(SubjectsContract.SubjectEntry.COLOR_COLUMN_INDEX));

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
    protected void onDestroy() {
        super.onDestroy();
        examsDbHelper.closeDB();
    }
}
