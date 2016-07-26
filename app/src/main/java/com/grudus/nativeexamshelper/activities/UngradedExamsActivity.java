package com.grudus.nativeexamshelper.activities;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UngradedExamsActivity extends AppCompatActivity implements ItemClickListener {
    
    @BindView(R.id.recycler_view_ungraded_exams)
    RecyclerView recyclerView;


    private UngradedExamsAdapter adapter;
    private ExamsDbHelper dbHelper;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ungraded_exams);
        ButterKnife.bind(this);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.list_item_to_toolbar));
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDatabase();

        initRecyclerView();
    }

    private void initDatabase() {
        dbHelper = ExamsDbHelper.getInstance(this);
        dbHelper.openDB();
    }

    private void initRecyclerView() {
        subscription =
            dbHelper.getExamsOlderThan(System.currentTimeMillis())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cursor -> {
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        adapter = new UngradedExamsAdapter(cursor, this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setHasFixedSize(true);
                    });
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbHelper.closeDB();
        if (!subscription.isUnsubscribed())
            subscription.unsubscribe();
    }


    @Override
    public void itemClicked(View v, final int position) {
        final SelectGradeDialog dialog = new SelectGradeDialog();
        Log.i("@@@###", "itemClicked: " + dbHelper);
        dialog.setListener((dialogInterface, which) -> {
            Toast.makeText(UngradedExamsActivity.this, dialog.getSelectedGrade() + "", Toast.LENGTH_SHORT).show();

            Exam exam = adapter.getExamByPosition(position);

            subscription =
                dbHelper.examBecomesOld(exam, dialog.getSelectedGrade())
                        .flatMap((id) -> dbHelper.getExamsOlderThan(System.currentTimeMillis()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((cursor) -> {
                            adapter.examHasGrade(position, cursor);
                        });
        });

        dialog.show(getFragmentManager(), getString(R.string.tag_dialog_select_grade));
    }

}
