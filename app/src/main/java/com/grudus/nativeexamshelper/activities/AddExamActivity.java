package com.grudus.nativeexamshelper.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.grudus.nativeexamshelper.AddingExamMainActivity;
import com.grudus.nativeexamshelper.DateHelper;
import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.pojos.Exam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

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

    }

    @OnClick(R.id.add_exam_date_input)
    void dateInput() {
        new DatePickerDialog(AddExamActivity.this, date, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @OnClick({R.id.add_exam_extras_input, R.id.add_exam_subject_input})
    void infoInput() {
        Log.e("++++++++++++++++++++", "ddduuuuppa");
    }

    @OnClick(R.id.add_exam_button)
        // TODO: 6/20/16 change this "throws"
    void addExam()  {
        String subject = subjectInput.getText().toString();
        if (subject.replaceAll("\\s+", "").isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.empty_subject_add_exam), Toast.LENGTH_SHORT).show();
            setFocusAndShowKeyboard(subjectInput);
            return;
        }
        String date = dateInput.getText().toString();
        if (date.replaceAll("\\s+", "").isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.empty_date_add_exam), Toast.LENGTH_SHORT).show();
            setFocusAndShowKeyboard(dateInput);
            return;
        }
        String info = extrasInput.getText().toString();
        try {
            Exam exam = new Exam(subject.trim(), info.trim(), DateHelper.getDateFromString(date));

        Intent goBackToExamsView = new Intent(getApplicationContext(), AddingExamMainActivity.class);
        goBackToExamsView.putExtra("newExam", exam);
            goBackToExamsView.putExtra("reopen", true);
        startActivity(goBackToExamsView);
        } catch (ParseException e) {
            for (StackTraceElement s : e.getStackTrace()) Log.e("_____-----____", s.toString());
        }
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
        String myFormat = getResources().getString(R.string.date_format);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        dateInput.setText(sdf.format(calendar.getTime()));
    }

    private void setFocusAndShowKeyboard(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

}
