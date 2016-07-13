package com.grudus.nativeexamshelper.activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.AddExamActivity;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.adapters.ExamsCursorAdapter;
import com.grudus.nativeexamshelper.pojos.Exam;

import java.util.ArrayList;
import java.util.Calendar;


public class AddingExamFragment extends Fragment {

    private final String TAG = "@@@" + this.getClass().getSimpleName();

    private FloatingActionButton floatingActionButton;
    private ListView listView;

    private ExamsDbHelper examsDbHelper;
    private ExamsCursorAdapter cursorAdapter;




    public AddingExamFragment() {
        // Required empty public constructor
    }
    
    public void removeAll() {
        if (examsDbHelper == null) return;
        examsDbHelper.openDB();
        examsDbHelper.cleanAllRecords(ExamsContract.ExamEntry.TABLE_NAME);
        cursorAdapter.changeCursor(examsDbHelper.getAllIncomingExamsSortByDate());
        examsDbHelper.closeDB();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_adding_exam, container, false);
//
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button_add_exam);
        listView = (ListView) view.findViewById(R.id.list_view_adding_exam_content);

        setListeners();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDatabase();
//        updateDatabase();
        populateListView();
        closeDatabase();
    }

    @Override
    public void onPause() {
        super.onPause();
        closeDatabase();
        cursorAdapter.changeCursor(null);
    }


    private void setListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openAddExamActivity = new Intent(getActivity(), AddExamActivity.class);
                startActivity(openAddExamActivity);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Klikles w " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initDatabase() {
        if (examsDbHelper == null && getActivity() != null)
            examsDbHelper = ExamsDbHelper.getInstance(getContext());
        examsDbHelper.openDB();
        Log.d(TAG, "initDatabase method ");
    }



    private void populateListView() {
        cursorAdapter = new ExamsCursorAdapter(getActivity(), examsDbHelper.getAllIncomingExamsSortByDate(), 0);
        listView.setAdapter(cursorAdapter);
    }

    private void closeDatabase() {
        examsDbHelper.closeDB();
    }




}
