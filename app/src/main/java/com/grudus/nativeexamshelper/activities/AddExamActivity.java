package com.grudus.nativeexamshelper.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.grudus.nativeexamshelper.DateHelper;
import com.grudus.nativeexamshelper.ExceptionsHelper;
import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.pojos.Exam;
import com.grudus.nativeexamshelper.pojos.Subject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddExamActivity extends AppCompatActivity {

    @BindView(R.id.add_exam_date_input) EditText dateInput;
    @BindView(R.id.add_exam_subject_input) EditText subjectInput;
    @BindView(R.id.add_exam_extras_input) EditText extrasInput;
    @BindView(R.id.add_exam_button) Button addExamButton;

    private Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
        ButterKnife.bind(this);

        if (getIntent().getParcelableExtra("subject") != null)
            setSelectedSubjectLabel((Subject) getIntent().getParcelableExtra("subject"));
    }

    @OnClick(R.id.add_exam_date_input)
    void dateInput() {
        new DatePickerDialog(AddExamActivity.this, date, calendar.get(Calendar.YEAR),
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
        if (subject.replaceAll("\\s+", "").isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.empty_subject_add_exam), Toast.LENGTH_SHORT).show();
            return;
        }
        String date = dateInput.getText().toString();
        if (date.replaceAll("\\s+", "").isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.empty_date_add_exam), Toast.LENGTH_SHORT).show();
            return;
        }
        String info = extrasInput.getText().toString();
        Date correctDate;

        try {
            correctDate = DateHelper.getDateFromString(date);
        } catch (ParseException e) {
            ExceptionsHelper.printError(e);
            return;
        }
        Exam exam = new Exam(subject, info, correctDate);

        Intent goBackToExamsView = new Intent(getApplicationContext(), AddingExamMainActivity.class);
        goBackToExamsView.putExtra("newExam", exam);
        goBackToExamsView.putExtra("reopen", true);
        startActivity(goBackToExamsView);
    }


    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };


    private void updateLabel() {
        dateInput.setText(DateHelper.getStringFromDate(calendar.getTime()));
    }

    private void setSelectedSubjectLabel(Subject subject) {
        subjectInput.setText(subject.getTitle());
    }

}
