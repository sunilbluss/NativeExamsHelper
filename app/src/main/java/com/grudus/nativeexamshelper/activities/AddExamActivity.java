package com.grudus.nativeexamshelper.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grudus.nativeexamshelper.helpers.CalendarDialogHelper;
import com.grudus.nativeexamshelper.helpers.DateHelper;
import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.helpers.ThemeHelper;
import com.grudus.nativeexamshelper.helpers.TimeDialogHelper;
import com.grudus.nativeexamshelper.helpers.TimeHelper;
import com.grudus.nativeexamshelper.pojos.Exam;
import com.grudus.nativeexamshelper.pojos.Subject;

import java.text.SimpleDateFormat;
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
    @BindView(R.id.add_exam_time_input) EditText timeInput;
    @BindView(R.id.add_exam_button) Button addExamButton;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private CalendarDialogHelper calendarDialog;
    private TimeDialogHelper timeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
        ButterKnife.bind(this);

        if (getIntent().getParcelableExtra("subject") != null)
            setSelectedSubjectLabel((Subject) getIntent().getParcelableExtra("subject"));

        toolbar.setTitle(getResources().getString(R.string.add_new_exam_toolbar_text));
        setListenerToDeleteTextViewFocus();

        calendarDialog = new CalendarDialogHelper(this, new CalendarDialogHelper.AfterDateSetListener() {
            @Override
            public void afterDateSet() {
                updateDateView();
            }
        });

        timeDialog = new TimeDialogHelper(this, new TimeDialogHelper.AfterTimeSetListener() {
            @Override
            public void afterTimeSet() {
                updateTimeView();
            }
        });

    }

    private void updateDateView() {
        dateInput.setText(DateHelper.getStringFromDate(calendarDialog.getDate()));
        deleteFocus();
    }

    private void updateTimeView() {
        String textToDisplay = TimeHelper.getFormattedTime(
                timeDialog.getHour(),
                timeDialog.getMinute()
        );

        timeInput.setText(textToDisplay);
        deleteFocus();
    }

    @OnClick(R.id.add_exam_date_input)
    void showDatePicker() {
        calendarDialog.showDialog();
    }

    @OnClick(R.id.add_exam_time_input)
    void showTimePicker() {
        timeDialog.showDialog();
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

        Date correctDate = getDateWithTime();

        String info = extrasInput.getText().toString();
        if (info.replaceAll("\\s+", "").isEmpty()) info = getString(R.string.sse_default_exam_info);

        Exam exam = new Exam(subject, info, correctDate);

        ExamsDbHelper db = ExamsDbHelper.getInstance(this);
        db.openDB();

        db.insertExam(exam);

        db.closeDB();

        Intent goBack = new Intent(getApplicationContext(), ExamsMainActivity.class);
        // new subject has been added, so there is no reason to keep previous activities in stack
        goBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goBack);
    }

    private Date getDateWithTime() {
        Calendar temp = calendarDialog.getCalendar();
        temp.add(Calendar.HOUR_OF_DAY, timeDialog.getHour());
        temp.add(Calendar.MINUTE, timeDialog.getMinute());
        return temp.getTime();
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
