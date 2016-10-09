package com.grudus.nativeexamshelper.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.activities.touchhelpers.ItemRemoveCallback;
import com.grudus.nativeexamshelper.adapters.ItemClickListener;
import com.grudus.nativeexamshelper.adapters.SubjectsAdapter;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.subjects.SubjectsContract;
import com.grudus.nativeexamshelper.dialogs.EditSubjectDialog;
import com.grudus.nativeexamshelper.helpers.JsonObjectHelper;
import com.grudus.nativeexamshelper.helpers.ThemeHelper;
import com.grudus.nativeexamshelper.helpers.ToastHelper;
import com.grudus.nativeexamshelper.net.RetrofitMain;
import com.grudus.nativeexamshelper.pojos.JsonSubject;
import com.grudus.nativeexamshelper.pojos.Subject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SubjectsListActivity extends AppCompatActivity implements ItemClickListener {

    private final String TAG = "@@@" + this.getClass().getSimpleName();

    @BindView(R.id.subjects_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.floating_button_add_subject) FloatingActionButton floatingActionButton;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private ExamsDbHelper examsDbHelper;
    private SubjectsAdapter adapter;

    private Subscription subscriptionDB;
    private Subscription subscriptionNet;
    private RetrofitMain retrofitMain;

    private ToastHelper toastHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects_list);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.subject_list_toolbar_text));
        setSupportActionBar(toolbar);

        initDatabase();
        populateList();
        
        initSwipeListener();

        retrofitMain = new RetrofitMain(this);
        toastHelper = new ToastHelper(this);
    }

    private void initSwipeListener() {
        ItemRemoveCallback itemRemoveCallback = new ItemRemoveCallback(0, ItemTouchHelper.RIGHT, SubjectsAdapter.SubjectsViewHolder.class);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemRemoveCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void initDatabase() {
        examsDbHelper = ExamsDbHelper.getInstance(this);
        examsDbHelper.openDB();
    }

    private void populateList() {
        subscriptionDB =
            examsDbHelper.getAllSubjectsWithoutDeleteChangeSortByTitle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cursor -> {
                    adapter = new SubjectsAdapter(cursor, SubjectsListActivity.this, SubjectsListActivity.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SubjectsListActivity.this));
                });

    }

    @Override
    public void itemClicked(View v, int position) {
        Subject subject = adapter.getItem(position);
        new EditSubjectDialog()
                .addSubject(subject)
                .addListener(editedSubject ->
                    subscriptionDB = examsDbHelper.updateSubjectChange(subject, SubjectsContract.CHANGE_UPDATED)
                        .flatMap(howMany -> examsDbHelper.updateSubject(subject, editedSubject))
                            .flatMap(howMany -> examsDbHelper.getAllSubjectsWithoutDeleteChangeSortByTitle())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(cursor -> {
                                adapter.changeCursor(cursor);
                                if (subject.getTitle().equals(adapter.getItem(position)))
                                    adapter.notifyItemChanged(position);
                                else
                                    adapter.notifyDataSetChanged();

                                subscriptionNet = tryToSendDataToTheServer(editedSubject, position, SubjectsContract.CHANGE_UPDATED);

                            }, error -> toastHelper.showErrorMessage("Błąd", error)))
                .show(getFragmentManager(), getString(R.string.tag_dialog_edit_subject));
    }

    private Subscription tryToSendDataToTheServer(Subject subject, int adapterPosition, String change) {
        JsonSubject jsonSubject = new JsonObjectHelper(this)
                .subjectObjectToJsonSubject(subject, adapter.getSubjectId(adapterPosition), change);

        Observable<Response<Void>> observable = change.equals(SubjectsContract.CHANGE_UPDATED) ? retrofitMain.updateSubject(jsonSubject)
                : retrofitMain.addNewSubject(jsonSubject);

        return observable
                .flatMap((response) -> examsDbHelper.updateSubjectChange(subject, null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(howMany -> Log.d(TAG, "tryToSendDataToTheServer: send to server " + howMany),
                        error -> Log.e(TAG, "tryToSendDataToTheServer: errrr", error));
    }

    @OnClick(R.id.floating_button_add_subject)
    public void addSubject() {
        new EditSubjectDialog()
                .addListener((editedSubject ->
                    subscriptionDB = examsDbHelper.insertSubject(editedSubject)
                            .flatMap(id -> examsDbHelper.getAllSubjectsWithoutDeleteChangeSortByTitle())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(cursor -> {
                                if (cursor.moveToFirst()) {
                                    int position = 0;
                                    do {
                                        if ( cursor.getString(SubjectsContract.SubjectEntry.TITLE_COLUMN_INDEX)
                                                .compareTo(editedSubject.getTitle()) > 0) break;
                                        position++;
                                    } while (cursor.moveToNext());
                                    adapter.changeCursor(cursor);
                                    adapter.notifyItemInserted(position - 1);
                                    subscriptionNet = tryToSendDataToTheServer(
                                            editedSubject,
                                            position - 1,
                                            SubjectsContract.CHANGE_CREATE);
                                }
                })))
                .show(getFragmentManager(), getString(R.string.tag_dialog_add_new_subject));
    }

    public SubjectsAdapter getAdapter() {
        return adapter;
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        examsDbHelper.closeDB();
        adapter.closeCursor();
        if (subscriptionDB != null && !subscriptionDB.isUnsubscribed())
            subscriptionDB.unsubscribe();
        if (subscriptionNet != null && !subscriptionNet.isUnsubscribed())
            subscriptionNet.unsubscribe();
    }

}
