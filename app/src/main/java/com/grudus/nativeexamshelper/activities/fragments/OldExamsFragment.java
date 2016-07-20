package com.grudus.nativeexamshelper.activities.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.SingleSubjectExamsActivity;
import com.grudus.nativeexamshelper.activities.UngradedExamsActivity;
import com.grudus.nativeexamshelper.adapters.ItemClickListener;
import com.grudus.nativeexamshelper.adapters.OldExamsAdapter;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.adapters.OldExamsCursorAdapter;
import com.grudus.nativeexamshelper.pojos.Subject;

/**
 * A simple {@link Fragment} subclass.
 */
public class OldExamsFragment extends Fragment {

    public final String TAG = "@@@" + this.getClass().getSimpleName();

    private RecyclerView recyclerView;
    private OldExamsAdapter adapter;


    public OldExamsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("@@@", "FRAGMENT2: onCreateView");
        View view = inflater.inflate(R.layout.fragment_old_exams, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        Log.d("@@@", "FRAGMENT2: onResume");
        super.onResume();
        populateList();
        setOnItemClickListener();
    }





    private void populateList() {
        ExamsDbHelper db = ExamsDbHelper.getInstance(getContext());
        db.openDB();


        adapter = new OldExamsAdapter(getActivity(), db.getSubjectsWithGrade());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db.closeDB();
    }


    private void initViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_old_exams);
    }

    public void removeAll() {
        ExamsDbHelper db = ExamsDbHelper.getInstance(getContext());
        db.openDB();
        db.cleanAllRecords(ExamsContract.OldExamEntry.TABLE_NAME);
        db.resetSubjectGrades();
        adapter.closeDatabase();
        db.closeDB();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.closeDatabase();
    }



    private void setOnItemClickListener() {

        adapter.setListener(new ItemClickListener() {
            @Override
            public void itemClicked(View v, int position) {
                if (position == OldExamsAdapter.HEADER_POSITION)
                    startActivity(new Intent(getContext(), UngradedExamsActivity.class));

                else {
                    Subject subject = adapter.getSubjectAtPosition(position);
                    Intent intent = new Intent(getContext(), SingleSubjectExamsActivity.class);
                    intent.putExtra(SingleSubjectExamsActivity.INTENT_SUBJECT_TAG, subject);
                    startActivity(intent);
                }
            }
        });

    }
}
