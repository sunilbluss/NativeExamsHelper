package com.grudus.nativeexamshelper.activities;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.adapters.ItemClickListener;
import com.grudus.nativeexamshelper.adapters.UngradedExamsAdapter;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.dialogs.SelectGradeDialog;
import com.grudus.nativeexamshelper.helpers.ThemeHelper;
import com.grudus.nativeexamshelper.pojos.Exam;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UngradedExamsActivity extends AppCompatActivity implements ItemClickListener {
    
    @BindView(R.id.recycler_view_ungraded_exams)
    RecyclerView recyclerView;


    private UngradedExamsAdapter adapter;
    private ExamsDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ungraded_exams);
        ButterKnife.bind(this);

        initDatabase();
        initRecyclerView();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.list_item_to_toolbar));
//        }
    }

    private void initDatabase() {
        dbHelper = ExamsDbHelper.getInstance(this);
        dbHelper.openDB();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UngradedExamsAdapter(dbHelper.getExamsOlderThan(System.currentTimeMillis()), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }


    @Override
    protected void onPause() {
        super.onPause();
        dbHelper.closeDB();
    }


    @Override
    public void itemClicked(View v, final int position) {
        final SelectGradeDialog dialog = new SelectGradeDialog();
        dialog.setListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Toast.makeText(UngradedExamsActivity.this, dialog.getSelectedGrade() + "", Toast.LENGTH_SHORT).show();

                Exam exam = adapter.getExamByPosition(position);
                dbHelper.examBecomesOld(exam, dialog.getSelectedGrade());
                adapter.examHasGrade(position, dbHelper.getExamsOlderThan(System.currentTimeMillis()));
            }
        });

        dialog.show(getFragmentManager(), getString(R.string.tag_dialog_select_grade));
    }
}
