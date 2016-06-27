package com.grudus.nativeexamshelper.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.exams.ExamsContract;
import com.grudus.nativeexamshelper.database.subjects.SingleSubjectExamsCursorAdapter;
import com.grudus.nativeexamshelper.pojos.OldExam;
import com.grudus.nativeexamshelper.pojos.Subject;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleSubjectExamsActivity extends AppCompatActivity {

    public static final String INTENT_SUBJECT_TAG = "subject";
    private final String TAG = "@@@" + this.getClass().getSimpleName();


    @BindView(R.id.list_view_single_subject_exams)
    ListView listView;

    private CursorAdapter cursorAdapter;
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
        toolbar.setTitle(getString(R.string.sse_toolbar));
//        changeColors(subject.getColor());
        setSupportActionBar(toolbar);


        populateList();
        populateInfo();
    }

    private void populateInfo() {
        String[] numbers = calculate();
        ((TextView) findViewById(R.id.sse_title)).setText(subjectTitle);
        ((TextView) findViewById(R.id.sse_info_average)).setText(getString(R.string.sse_info_average) + numbers[0]);
        ((TextView) findViewById(R.id.sse_info_median)).setText(getString(R.string.sse_info_median) + numbers[1]);
        ((TextView) findViewById(R.id.sse_info_dominant)).setText(getString(R.string.sse_info_dominant) + numbers[2]);
        ((TextView) findViewById(R.id.sse_info_passed)).setText(getString(R.string.sse_info_passed) + numbers[3]);
        ((TextView) findViewById(R.id.sse_info_failed)).setText(getString(R.string.sse_info_failed) + numbers[4]);
        ((TextView) findViewById(R.id.sse_info_percent)).setText(getString(R.string.sse_info_percent) + numbers[5]);
    }

    private String[] calculate() {
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
            if (grade == OldExam.POSSIBLE_GRADES[0]) continue;
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

    private void populateList() {
        dbHelper = ExamsDbHelper.getInstance(this);
        dbHelper.openDBReadOnly();
        cursorAdapter = new SingleSubjectExamsCursorAdapter(this, dbHelper.getSubjectGrades(subjectTitle), 0);
        listView.setAdapter(cursorAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dbHelper != null) dbHelper.closeDB();
        if (cursorAdapter != null) cursorAdapter.changeCursor(null);
    }


//    private void changeColors(String color) {
//        toolbar.setBackgroundColor(Color.parseColor(color));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getStatusBarColor(Color.parseColor(color)));
//        }
//    }

    private int getStatusBarColor(int color) {
        return color;
    }

    private int max3(int a, int b, int c) {
        return Math.max(Math.max(a, b), c);
    }

}

