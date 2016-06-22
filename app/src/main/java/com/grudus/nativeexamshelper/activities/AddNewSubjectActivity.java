package com.grudus.nativeexamshelper.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.pojos.Subject;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private boolean changingExisting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_subject);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.add_subject_toolbar_text));
        setSupportActionBar(toolbar);

        subject = getIntent().getParcelableExtra("subject") == null
                ? Subject.empty() : (Subject) getIntent().getParcelableExtra("subject");

        changingExisting = !subject.isEmpty();

        if (changingExisting) {
            button.setText(getString(R.string.button_text_change_subject));
            oldSubject = subject.copy();
        }

        setSubjectInfoToView();
    }

    private void setSubjectInfoToView() {
        boolean empty = subject.isEmpty();
        int color = empty ? ContextCompat.getColor(getApplicationContext(), R.color.niceBlueColor)
                : Color.parseColor(subject.getColor());

        if (!empty) {
            titleInput.setText(subject.getTitle());
        }

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


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerInitVal[0] = colorPicker.getRed();
                colorPickerInitVal[1] = colorPicker.getGreen();
                colorPickerInitVal[2] = colorPicker.getBlue();

                int color = colorPicker.getColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & color));

                colorPicker.dismiss();

                subject.setColor(hexColor);
                colorIcon.setColorFilter(color);
            }
        });

    }


    @OnClick(R.id.add_subject_button)
    public void addSubject() {
        String title = titleInput.getText().toString().trim();
        if (!titleIsCorrect(title)) return;

        subject.setTitle(title);

        if (changingExisting)
            updateSubject();

        else if (!addSubjectToDatabase())
            return;

        Intent goBack = new Intent(getApplicationContext(), SubjectsListActivity.class);
        goBack.putExtra("editable", changingExisting);
        startActivity(goBack);

    }

    private boolean titleIsCorrect(String title) {
        if (title.replaceAll("\\s+", "").isEmpty()) {
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

        ExamsDbHelper db = new ExamsDbHelper(this);
        db.openDB();
        db.updateSubject(oldSubject, subject);
        db.closeDB();
        return true;

    }

    private boolean addSubjectToDatabase() {
        if (subject.isEmpty()) {
            Log.e(TAG, "addSubjectToDatabase: Subject is empty. Nie powinno sie zdarzyc");
            return false;
        }
        ExamsDbHelper db = new ExamsDbHelper(this);
        db.openDB();

        if (db.findSubjectByTitle(subject.getTitle()) != null) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.warning_add_subject_subject_already_exists),
                    Toast.LENGTH_SHORT).show();
            db.closeDB();
            return false;
        }

        db.insertSubject(subject);
        Log.d(TAG, "Dodano " + subject + " do bazy danych");

        db.closeDB();
        return true;
    }


}
