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
import com.grudus.nativeexamshelper.helpers.GradeStatisticsCalculator;
import com.grudus.nativeexamshelper.helpers.StatisticsTextFormatter;
import com.grudus.nativeexamshelper.helpers.ThemeHelper;
import com.grudus.nativeexamshelper.pojos.OldExam;
import com.grudus.nativeexamshelper.pojos.Subject;
import com.grudus.nativeexamshelper.pojos.grades.Grades;

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
        ThemeHelper.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_subject_exams);
        ButterKnife.bind(this);

        getIntentInformation();

        setToolbar();

        openDatabase();
        initRecyclerView();

        calculateAllStatistics();
        updateStatisticsLabels();

        initListeners();

    }

    private void getIntentInformation() {
        Subject subject = getIntent().getParcelableExtra(INTENT_SUBJECT_TAG);
        subjectTitle = subject.getTitle();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) toolbar.setTitle(subjectTitle);
        setSupportActionBar(toolbar);
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

    private void updateStatisticsLabels() {
        ((TextView) findViewById(R.id.sse_info_average)).setText(getString(R.string.sse_info_average) + StatisticsTextFormatter.getAverage());
        ((TextView) findViewById(R.id.sse_info_median)).setText(getString(R.string.sse_info_median) + StatisticsTextFormatter.getMedian());
        ((TextView) findViewById(R.id.sse_info_dominant)).setText(getString(R.string.sse_info_dominant) + StatisticsTextFormatter.getDominant());
        ((TextView) findViewById(R.id.sse_info_passed)).setText(getString(R.string.sse_info_passed) + StatisticsTextFormatter.getPassedExams());
        ((TextView) findViewById(R.id.sse_info_failed)).setText(getString(R.string.sse_info_failed) + StatisticsTextFormatter.getFailedExams());
        ((TextView) findViewById(R.id.sse_info_percent)).setText(getString(R.string.sse_info_percent) + StatisticsTextFormatter.getPercentOfPassedExams());
    }


    private void calculateAllStatistics() {
        GradeStatisticsCalculator calculator = new GradeStatisticsCalculator(Grades.getFirstPassedGrade());
        calculator.setUpDatabaseData(dbHelper.getOrderedSubjectGrades(subjectTitle), ExamsContract.OldExamEntry.GRADE_COLUMN_INDEX);
        calculator.calculateFromDatabase();
        setUpStatisticsTextFormatter(calculator);
    }

    private void setUpStatisticsTextFormatter(GradeStatisticsCalculator calculator) {
        StatisticsTextFormatter.setCalculator(calculator);
        StatisticsTextFormatter.setNoDominantText(getString(R.string.sse_info_lack_dominant));
    }
    

    @Override
    protected void onPause() {
        super.onPause();
        if (dbHelper != null) dbHelper.closeDB();
        adapter.closeCursor();
    }



}

