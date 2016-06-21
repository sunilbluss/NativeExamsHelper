package com.grudus.nativeexamshelper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.grudus.nativeexamshelper.activities.AddExamActivity;
import com.grudus.nativeexamshelper.database.DbHelper;
import com.grudus.nativeexamshelper.database.ExamsContract;
import com.grudus.nativeexamshelper.database.ExamsCursorAdapter;
import com.grudus.nativeexamshelper.pojos.Exam;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddingExamMainActivity extends AppCompatActivity {

    public static final String TAG = "@@@@@@@@@@@@@@@@@@@@@";


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.floating_button_add_exam) FloatingActionButton floatingActionButton;

    private ListView listView;
    private static Context mainApplicationContext;
    private DbHelper dbHelper;

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

        listView = (ListView) findViewById(R.id.list_view_adding_exam_content);

        populateListView();



    }

    public static Context getMainApplicationContext() {
        return mainApplicationContext;
    }

    private void initDatabase() {
        dbHelper = new DbHelper(this);
        dbHelper.openDB();
    }

    private void populateListView() {
        Cursor c = dbHelper.selectAllFromExams();
        cursorAdapter = new ExamsCursorAdapter(this, c, 0);
        listView.setAdapter(cursorAdapter);
    }



    private void addExamToDatabase(Exam exam) {
        if (exam == null) {
            Toast.makeText(this, "parcerableExtra is null", Toast.LENGTH_SHORT).show();
            return;
        }
        long l = dbHelper.insertExam(exam);
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
            dbHelper.cleanAllRecords();
            cursorAdapter.swapCursor(dbHelper.selectAllFromExams());
            return true;
        }

        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
    }
}
