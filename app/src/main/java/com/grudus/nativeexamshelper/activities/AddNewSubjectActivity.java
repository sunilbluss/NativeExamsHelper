package com.grudus.nativeexamshelper.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.helpers.ExceptionsHelper;
import com.grudus.nativeexamshelper.helpers.ThemeHelper;
import com.grudus.nativeexamshelper.pojos.Subject;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddNewSubjectActivity extends AppCompatActivity {

    private final String TAG = "@@@" + this.getClass().getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.add_subject_color)
    EditText colorInput;

    @BindView(R.id.add_subject_title)
    EditText titleInput;

    @BindView(R.id.add_subject_color_icon)
    ImageView colorIcon;

    @BindView(R.id.add_subject_button) Button button;

    private final int[] colorPickerInitVal = new int[3];

    private Subject subject, oldSubject;

    private Subscription subscription;

    private boolean changingExistingSubjectMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_subject);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.add_subject_toolbar_text));
        setSupportActionBar(toolbar);

        getDataFromIntent();

        setSubjectInfoToView();
        changeButtonTextIfChangingExistingSubject();
    }

    private void getDataFromIntent() {
        subject = getIntent().getParcelableExtra("subject") == null
                ? Subject.empty() : (Subject) getIntent().getParcelableExtra("subject");

        changingExistingSubjectMode = !subject.isEmpty();
    }

    private void changeButtonTextIfChangingExistingSubject() {
        if (changingExistingSubjectMode) {
            button.setText(getString(R.string.button_text_change_subject));
            oldSubject = subject.copy();
        }
    }

    private void setSubjectInfoToView() {
        boolean empty = !changingExistingSubjectMode;
        int color = empty ? ContextCompat.getColor(getApplicationContext(), R.color.niceBlueColor)
                : Color.parseColor(subject.getColor());

        if (!empty) {
            titleInput.setText(subject.getTitle());
        }

        updateColorPickerInitValAndSetIconColor(color);
    }

    private void updateColorPickerInitValAndSetIconColor(int color) {
        colorPickerInitVal[0] = Color.red(color);
        colorPickerInitVal[1] = Color.green(color);
        colorPickerInitVal[2] = Color.blue(color);
        colorIcon.setColorFilter(color);
    }



    @OnClick(R.id.add_subject_color)
    public void showColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(AddNewSubjectActivity.this,
                colorPickerInitVal[0], colorPickerInitVal[1], colorPickerInitVal[2]);
        colorPicker.show();

        Button button = (Button) colorPicker.findViewById(R.id.okColorButton);


        button.setOnClickListener(v -> {
            int color = colorPicker.getColor();
            updateColorPickerInitValAndSetIconColor(color);

            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            colorPicker.dismiss();

            subject.setColor(hexColor);
        });

    }


    @OnClick(R.id.add_subject_button)
    public void addSubject() {
        String title = titleInput.getText().toString().trim();
        if (!titleIsCorrect(title)) return;

        subject.setTitle(title);

        if (changingExistingSubjectMode)
            updateSubject();

        else if (!addSubjectToDatabase())
            return;

        Intent goBack = new Intent(getApplicationContext(), SubjectsListActivity.class);
        startActivity(goBack);

    }

    private boolean titleIsCorrect(String title) {
        if (ExceptionsHelper.stringsAreEmpty(title)) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.warning_add_subject_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Character.isLetter(title.charAt(0))) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.warning_add_subject_badchar),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean updateSubject() {
        if (subject.isEmpty()) {
            Log.e(TAG, "updateExisting: Subject is empty. Nie powinno sie zdarzyc");
            return false;
        }

        ExamsDbHelper db = ExamsDbHelper.getInstance(this);
        db.openDB();
        subscription =
            db.updateSubject(oldSubject, subject)
            .subscribeOn(Schedulers.io())
            .subscribe(success -> db.closeDB(), error -> db.closeDB());
        return true;
    }

    private boolean addSubjectToDatabase() {
        if (subject.isEmpty()) {
            Log.e(TAG, "addSubjectToDatabase: Subject is empty. Nie powinno sie zdarzyc");
            return false;
        }
        ExamsDbHelper db = ExamsDbHelper.getInstance(this);
        db.openDB();

        subscription =
        db.findSubjectByTitle(subject.getTitle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(subjectTitle -> {
                    if (subjectTitle != null) {
                        Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.warning_add_subject_subject_already_exists),
                        Toast.LENGTH_SHORT).show();
                        return Observable.empty();
                    }
                    else return db.insertSubject(subject); })
                .subscribe(success -> db.closeDB(), error -> db.closeDB());

        return true;
    }

    private void deleteFocus() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        findViewById(R.id.add_subject_layout).requestFocus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
