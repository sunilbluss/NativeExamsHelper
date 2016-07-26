package com.grudus.nativeexamshelper.activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.AddExamActivity;
import com.grudus.nativeexamshelper.adapters.ExamsAdapter;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class AddingExamFragment extends Fragment {

    private final String TAG = "@@@" + this.getClass().getSimpleName();

    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;

    private ExamsDbHelper examsDbHelper;
    private ExamsAdapter adapter;
    private Subscription subscription;


    public AddingExamFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_adding_exam, container, false);

        initViews(view);
        setListeners();

        return view;
    }

    private void initViews(View view) {
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button_add_exam);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_adding_exam_content);
    }


    private void setListeners() {
        floatingActionButton.setOnClickListener(view -> {
            Intent openAddExamActivity = new Intent(getActivity(), AddExamActivity.class);
            startActivity(openAddExamActivity);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        initDatabase();
        populateRecyclerView();
    }



    private void initDatabase() {
        if (examsDbHelper == null && getActivity() != null)
            examsDbHelper = ExamsDbHelper.getInstance(getContext());
        examsDbHelper.openDB();
    }


    private void populateRecyclerView() {
        subscription =
            examsDbHelper.getAllIncomingExamsSortByDate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(cursor -> {
                    adapter = new ExamsAdapter(getContext(), cursor);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }, error -> Log.e(TAG, "populateRecyclerView: ERRRRRRR", error));

    }

    @Override
    public void onPause() {
        super.onPause();
        if (!subscription.isUnsubscribed())
            subscription.unsubscribe();
        closeDatabase();
    }

    private void closeDatabase() {
        examsDbHelper.closeDB();
        adapter.closeDatabase();
    }




}
