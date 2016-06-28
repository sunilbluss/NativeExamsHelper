package com.grudus.nativeexamshelper.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.pojos.Exam;
import com.grudus.nativeexamshelper.pojos.Subject;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddExamActivity extends AppCompatActivity {

    private final String TAG = "@@@" + this.getClass().getSimpleName();

    @BindView(R.id.add_exam_date_input) EditText dateInput;
    @BindView(R.id.add_exam_subject_input) EditText subjectInput;
    @BindView(R.id.add_exam_extras_input) EditText extrasInput;
    @BindView(R.id.add_exam_button) Button addExamButton;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private Calendar calendar = Calendar.getInstance();


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
        ButterKnife.bind(this);

        if (getIntent().getParcelableExtra("subject") != null)
            setSelectedSubjectLabel((Subject) getIntent().getParcelableExtra("subject"));

        toolbar.setTitle(getResources().getString(R.string.add_new_exam_toolbar_text));
        setListenerToDeleteTextViewFocus();

    }

    @OnClick(R.id.add_exam_date_input)
    void showDatePicker() {
        new DatePickerDialog(AddExamActivity.this, datePickerListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @OnClick(R.id.add_exam_subject_input)
    void openSubjectsListActivity() {
        Intent intent = new Intent(this, SubjectsListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.add_exam_button)
    void addExam()  {
        String subject = subjectInput.getText().toString();
        String date = dateInput.getText().toString();

        if (!inputsAreCorrect(subject, date)) return;

        Date correctDate = DateHelper.tryToGetDateFromString(date);
        if (correctDate == null) return;
        String info = extrasInput.getText().toString();

        Exam exam = new Exam(subject, info, correctDate);

        ExamsDbHelper db = ExamsDbHelper.getInstance(this);
        db.openDB();

        // exam was in the past
        if (examWasInThePast(correctDate)) {
            db.examBecomesOld(exam);
        }
        else db.insertExam(exam);

        db.closeDB();

        Intent goBack = new Intent(getApplicationContext(), ExamsMainActivity.class);
        // new subject has been added, so there is no reason to keep previous activities in stack
        goBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goBack);
    }

    private static boolean examWasInThePast(Date date) {
        return date.getTime() < Calendar.getInstance().getTime().getTime();
    }


    private boolean inputsAreCorrect(String subject, String date) {
        if (subject.replaceAll("\\s+", "").isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.empty_subject_add_exam), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (date.replaceAll("\\s+", "").isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.empty_date_add_exam), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void updateDateView() {
        dateInput.setText(DateHelper.getStringFromDate(calendar.getTime()));
        deleteFocus();
    }

    private void setSelectedSubjectLabel(Subject subject) {
        subjectInput.setText(subject.getTitle());
    }

    private void setListenerToDeleteTextViewFocus() {
        extrasInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    deleteFocus();
                    return true;
                }

                return true;
            }
        });

        extrasInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    deleteFocus();
                }
            }
        });
    }

    private void deleteFocus() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        findViewById(R.id.add_exam_layout).requestFocus();
    }

}
