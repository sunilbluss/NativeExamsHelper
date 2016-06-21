package com.grudus.nativeexamshelper.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsCursorAdapter;
import com.grudus.nativeexamshelper.pojos.Exam;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class AddingExamMainActivity extends AppCompatActivity {

    public static final String TAG = "@@@@@@@@@@@@@@@@@@@@@";


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.floating_button_add_exam) FloatingActionButton floatingActionButton;
    @BindView(R.id.list_view_adding_exam_content) ListView listView;

    private static Context mainApplicationContext;
    private ExamsDbHelper examsDbHelper;

    private ExamsCursorAdapter cursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on create main");
        mainApplicationContext = this;
        setContentView(R.layout.activity_adding_exam_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initDatabase();

        if (getIntent().getBooleanExtra("reopen", false))
            addExamToDatabase((Exam)getIntent().getParcelableExtra("newExam"));

        populateListView();
    }

    @OnItemClick(R.id.list_view_adding_exam_content)
    public void goToExamPage(int index) {
        Toast.makeText(this, "Klikles w " + index, Toast.LENGTH_SHORT).show();
        Log.d(TAG, cursorAdapter.getItem(index).toString());
    }

    public static Context getMainApplicationContext() {
        return mainApplicationContext;
    }

    private void initDatabase() {
        examsDbHelper = new ExamsDbHelper(this);
        examsDbHelper.openDB();
    }

    private void populateListView() {
        Cursor c = examsDbHelper.selectAllFromExamsSortByDate();
        cursorAdapter = new ExamsCursorAdapter(this, c, 0);
        listView.setAdapter(cursorAdapter);
    }



    private void addExamToDatabase(Exam exam) {
        if (exam == null) {
            Toast.makeText(this, "parcerableExtra is null", Toast.LENGTH_SHORT).show();
            return;
        }
        long l = examsDbHelper.insertExam(exam);
        Log.d(TAG, exam + " inserted on row " + l);
    }

    @OnClick(R.id.floating_button_add_exam)
    public void addExam() {
        Intent openAddExamActivity = new Intent(getApplicationContext(), AddExamActivity.class);
        startActivity(openAddExamActivity);
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
            examsDbHelper.cleanAllExamRecords();
            cursorAdapter.swapCursor(examsDbHelper.selectAllFromExams());
            return true;
        }

        if (item.getItemId() == R.id.menu_item_refresh_subjects) {
            Toast.makeText(this, "Przedmioty sa odswiezone", Toast.LENGTH_SHORT).show();
            examsDbHelper.refreshSubjects();
            return true;
        }

        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();

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
