package com.grudus.nativeexamshelper.activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.adapters.UngradedExamsAdapter;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UngradedExamsActivity extends AppCompatActivity {
    
    @BindView(R.id.recycler_view_ungraded_exams)
    RecyclerView recyclerView;

    private ExamsDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ungraded_exams);
        ButterKnife.bind(this);

        initDatabase();
        initRecyclerView();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.list_item_to_toolbar));
//        }
    }

    private void initDatabase() {
        dbHelper = ExamsDbHelper.getInstance(this);
        dbHelper.openDB();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new UngradedExamsAdapter(dbHelper.getExamsOlderThan(System.currentTimeMillis())));
    }


    @Override
    protected void onPause() {
        super.onPause();
        dbHelper.closeDB();
    }
}
