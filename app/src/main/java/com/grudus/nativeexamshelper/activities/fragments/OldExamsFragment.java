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

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class OldExamsFragment extends Fragment {

    public final String TAG = "@@@" + this.getClass().getSimpleName();

    private RecyclerView recyclerView;
    private OldExamsAdapter adapter;
    private ExamsDbHelper examsDbHelper;

    private Subscription subscription;

    public OldExamsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_old_exams, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_old_exams);
    }


    @Override
    public void onStart() {
        super.onStart();
        initDatabase();
        populateList();
    }

    private void initDatabase() {
        if (examsDbHelper == null && getActivity() != null)
            examsDbHelper = ExamsDbHelper.getInstance(getContext());
        examsDbHelper.openDB();
    }


    private void populateList() {
        examsDbHelper.getSubjectsWithGrade()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(cursor -> {
                    adapter = new OldExamsAdapter(getActivity(), cursor);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    setOnItemClickListener();
                });
    }

    private void setOnItemClickListener() {
        adapter.setListener((v, position) -> {
            if (position == OldExamsAdapter.HEADER_POSITION)
                startActivity(new Intent(getContext(), UngradedExamsActivity.class));

            else {
                Subject subject = adapter.getSubjectAtPosition(position);
                Intent intent = new Intent(getContext(), SingleSubjectExamsActivity.class);
                intent.putExtra(SingleSubjectExamsActivity.INTENT_SUBJECT_TAG, subject);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        closeDatabase();
    }

    private void closeDatabase() {
        examsDbHelper.closeDB();
        adapter.closeDatabase();
    }
}
