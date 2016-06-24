package com.grudus.nativeexamshelper.activities.sliding;

import android.content.Intent;
import android.database.Cursor;
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
import com.grudus.nativeexamshelper.database.exams.ExamsCursorAdapter;


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
        examsDbHelper.cleanAllExamRecords();
        cursorAdapter.swapCursor(examsDbHelper.selectAllFromExamsSortByDate());
        examsDbHelper.closeDB();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d(TAG, "setUserVisibleHint: is visible");
        }
        else Log.d(TAG, "setUserVisibleHint: isn't visible");
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
        populateListView();
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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Klikles w " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initDatabase() {
        examsDbHelper = new ExamsDbHelper(getActivity());
        examsDbHelper.openDB();
    }

    private void populateListView() {
        Cursor c = examsDbHelper.selectAllFromExamsSortByDate();
        cursorAdapter = new ExamsCursorAdapter(getActivity(), c, 0);
        listView.setAdapter(cursorAdapter);
    }

    private void closeDatabase() {
        examsDbHelper.closeDB();
    }


}
