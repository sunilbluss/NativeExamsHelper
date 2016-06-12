package com.grudus.nativeexamshelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_exam_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // TODO: 6/12/16 DATABASE!
        listView = (ListView) findViewById(R.id.list_view_adding_exam_content);
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList("Matematyka", "Fizyka", "Matematyka", "Chemia", "Matematyka"));

        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item_adding_exam_content, list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Klikles w " + position + " numer", Toast.LENGTH_SHORT).show();
            }
        });

        Log.e("eeeeeeeeee", "onCreate: is null? " + (floatingActionButton == null));


    }

    @OnClick(R.id.floating_button_add_exam)
    void addNewExam() {
        Toast.makeText(getApplicationContext(), "Fajnie! Dodajesz egzamin!", Toast.LENGTH_SHORT).show();
        Log.e("##################", "ok weszlo");
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
