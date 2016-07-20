package com.grudus.nativeexamshelper.activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.AddExamActivity;
import com.grudus.nativeexamshelper.activities.ExamsMainActivity;
import com.grudus.nativeexamshelper.adapters.ExamsAdapter;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;


public class AddingExamFragment extends Fragment {

    private final String TAG = "@@@" + this.getClass().getSimpleName();

    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;

    private ExamsDbHelper examsDbHelper;
    private ExamsAdapter adapter;




    public AddingExamFragment() {
        // Required empty public constructor
    }
    
    public void removeAll() {
        if (examsDbHelper == null) return;
        examsDbHelper.openDB();
        examsDbHelper.cleanAllRecords(ExamsContract.ExamEntry.TABLE_NAME);
        adapter.changeCursor(null);
        examsDbHelper.closeDB();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_adding_exam, container, false);
//
        initViews(view);
        setListeners();

        return view;
    }

    private void initViews(View view) {
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button_add_exam);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_adding_exam_content);
    }


    @Override
    public void onPause() {
        super.onPause();
        closeDatabase();
        adapter.changeCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        initDatabase();
        populateRecyclerView();
        closeDatabase();
    }

    private void setListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openAddExamActivity = new Intent(getActivity(), AddExamActivity.class);
                startActivity(openAddExamActivity);
            }
        });


    }


    private void initDatabase() {
        if (examsDbHelper == null && getActivity() != null)
            examsDbHelper = ExamsDbHelper.getInstance(getContext());
        examsDbHelper.openDB();
        Log.d(TAG, "initDatabase method ");
    }



    private void populateRecyclerView() {
        adapter = new ExamsAdapter(getActivity(), examsDbHelper.getAllIncomingExamsSortByDate());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }

    private void closeDatabase() {
        examsDbHelper.closeDB();
    }




}
