package com.grudus.nativeexamshelper.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.adapters.SingleSubjectExamsAdapter;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.helpers.ColorHelper;
import com.grudus.nativeexamshelper.pojos.OldExam;
import com.grudus.nativeexamshelper.pojos.Subject;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleSubjectExamsActivity extends AppCompatActivity {

    public static final String INTENT_SUBJECT_TAG = "subject";
    private final String TAG = "@@@" + this.getClass().getSimpleName();


    @BindView(R.id.recycler_view_single_subject_exams)
    RecyclerView recyclerView;

    @BindView(R.id.sse_info_layout)
    LinearLayout infoLayout;

    private SingleSubjectExamsAdapter adapter;
    private String subjectTitle;
    private ExamsDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_subject_exams);
        ButterKnife.bind(this);

        Subject subject = getIntent().getParcelableExtra(INTENT_SUBJECT_TAG);
        subjectTitle = subject.getTitle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(subjectTitle);
//        changeColors(toolbar, subject.getColor());
        setSupportActionBar(toolbar);

        openDatabase();
        initRecyclerView();


        updateStatistics();
        initListeners();
    }

    private void openDatabase() {
        dbHelper = ExamsDbHelper.getInstance(this);
        dbHelper.openDBReadOnly();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SingleSubjectExamsAdapter(this, dbHelper.getSubjectGrades(subjectTitle));
        recyclerView.setAdapter(adapter);
//        adapter.closeCursor();
    }



    private void initListeners() {

    }

    private void updateStatistics() {
        String[] numbers = calculateAllStatistics();
//        ((TextView) findViewById(R.id.sse_title)).setText(subjectTitle);
        ((TextView) findViewById(R.id.sse_info_average)).setText(getString(R.string.sse_info_average) + numbers[0]);
        ((TextView) findViewById(R.id.sse_info_median)).setText(getString(R.string.sse_info_median) + numbers[1]);
        ((TextView) findViewById(R.id.sse_info_dominant)).setText(getString(R.string.sse_info_dominant) + numbers[2]);
        ((TextView) findViewById(R.id.sse_info_passed)).setText(getString(R.string.sse_info_passed) + numbers[3]);
        ((TextView) findViewById(R.id.sse_info_failed)).setText(getString(R.string.sse_info_failed) + numbers[4]);
        ((TextView) findViewById(R.id.sse_info_percent)).setText(getString(R.string.sse_info_percent) + numbers[5]);
    }


    private String[] calculateAllStatistics() {
        String[] results = new String[6];
        int counter = 0;
        double sum = 0;
        double grade;
        double median;
        double dominant = -1;
        int oneGradeCounter = 0;
        int maxOneGrade = 0;
        int passed = 0;
        Cursor c = dbHelper.getOrderedSubjectGrades(subjectTitle);
        c.moveToFirst();

        do {
            grade = c.getDouble(ExamsContract.OldExamEntry.GRADE_COLUMN_INDEX);
            if (grade == OldExam.getEmptyGrade()) continue;
            if (dominant == -1) dominant = grade;

            if (dominant == grade) oneGradeCounter++;
            else {
                if (oneGradeCounter > maxOneGrade) {
                    dominant = grade;
                    maxOneGrade = oneGradeCounter;
                }
                oneGradeCounter = 0;
            }

            if (grade > 1.3) passed++;
            sum += grade;
            counter++;

        } while (c.moveToNext());

        c.moveToPosition(counter / 2);
        median = c.getDouble(ExamsContract.OldExamEntry.GRADE_COLUMN_INDEX);

        c.close();

        DecimalFormat format = new DecimalFormat("0.00");
        results[0] = " " + format.format(sum / counter);
        results[1] = " " + format.format(median);
        results[2] = " " + format.format(dominant);
        results[3] = " " +passed;
        results[4] = " " + (counter - passed);
        results[5] = " " + format.format( (100d * passed / counter) ) + "%";

        return results;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dbHelper != null) dbHelper.closeDB();
        adapter.closeCursor();
    }


    private void changeColors(Toolbar toolbar, String color) {
        toolbar.setBackgroundColor(Color.parseColor(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int intColor = ColorHelper.getNearestResColor(Color.parseColor(color), getResources().getStringArray(R.array.statusBarColors));
            getWindow().setStatusBarColor(intColor);
            getWindow().setNavigationBarColor(intColor);
        }
    }


}

