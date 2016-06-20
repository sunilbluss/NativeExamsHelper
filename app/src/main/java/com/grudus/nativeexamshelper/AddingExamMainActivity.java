package com.grudus.nativeexamshelper;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

import com.grudus.nativeexamshelper.activities.AddExamActivity;
import com.grudus.nativeexamshelper.pojos.Exam;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddingExamMainActivity extends AppCompatActivity {


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.floating_button_add_exam) FloatingActionButton floatingActionButton;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private static Context mainApplicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainApplicationContext = this;
        setContentView(R.layout.activity_adding_exam_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // TODO: 6/12/16 DATABASE!
        listView = (ListView) findViewById(R.id.list_view_adding_exam_content);
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList("Matematyka", "Fizyka", "Matematyka", "Chemia", "Matematyka", "Alimenty", "No i co teraz"));

        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item_adding_exam_content, list);
        listView.setAdapter(arrayAdapter);

        if (getIntent().getBooleanExtra("reopen", false))
            addExamToDatabase((Exam)getIntent().getParcelableExtra("newExam"));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Klikles w " + position + " numer", Toast.LENGTH_SHORT).show();
            }
        });

        Log.e("eeeeeeeeee", "onCreate: is null? " + (floatingActionButton == null));


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openAddExamActivity = new Intent(getApplicationContext(), AddExamActivity.class);
                startActivity(openAddExamActivity);
            }
        });
    }

    public static Context getMainApplicationContext() {
        return mainApplicationContext;
    }


    private void addExamToDatabase(Exam exam) {
        if (exam == null) {
            Toast.makeText(this, "parcerableExtra is null", Toast.LENGTH_SHORT).show();
            return;
        }
        String subject = exam.getSubject();

        arrayAdapter.add(subject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}
