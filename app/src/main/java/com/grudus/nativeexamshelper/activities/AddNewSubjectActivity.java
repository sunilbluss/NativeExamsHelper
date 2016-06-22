package com.grudus.nativeexamshelper.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.grudus.nativeexamshelper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNewSubjectActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_subject);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.add_subject_toolbar_text));
        setSupportActionBar(toolbar);
    }


}
