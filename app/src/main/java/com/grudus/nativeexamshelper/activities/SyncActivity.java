package com.grudus.nativeexamshelper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.converters.CursorToArrayConverter;
import com.grudus.nativeexamshelper.helpers.ExceptionsHelper;
import com.grudus.nativeexamshelper.helpers.ToastHelper;
import com.grudus.nativeexamshelper.net.RetrofitMain;
import com.grudus.nativeexamshelper.pojos.JsonExam;
import com.grudus.nativeexamshelper.pojos.JsonUser;
import com.grudus.nativeexamshelper.pojos.UserPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SyncActivity extends AppCompatActivity {

    private static final String TAG = "@@@@" + SyncActivity.class.getSimpleName();


    @BindView(R.id.sync_username) TextView usernameView;
    @BindView(R.id.sync_email) TextView emailView;
    @BindView(R.id.progress_bar_sync_parent) LinearLayout progressBarParent;

    private UserPreferences preferences;
    private UserPreferences.User user;
    private ToastHelper toastHelper;
    private RetrofitMain retrofit;

    private JsonUser jsonUser;
    private ExamsDbHelper examsDbHelper;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ButterKnife.bind(this);

        toastHelper = new ToastHelper(this);
        preferences = new UserPreferences(this);
        user = preferences.getLoggedUser();
        retrofit = new RetrofitMain(this);
        examsDbHelper = ExamsDbHelper.getInstance(this);

        tryToReceiveData();
    }

    private void showInfo() {
        usernameView.setText(jsonUser.getUsername());
        emailView.setText(jsonUser.getEmail());

    }

    private void tryToReceiveData() {
        progressBarParent.setVisibility(View.VISIBLE);
        subscription = retrofit
                .getUserInfo()
                .flatMap(response -> {
                    ExceptionsHelper.checkResponse(response);

                    this.jsonUser = response.body();
                    Log.d(TAG, "tryToReceiveData: response: " + jsonUser);

                    return new CursorToArrayConverter(getApplicationContext()).getChangedSubjectsAsJson();
                })
                .flatMap(array -> retrofit.insertSubjects(array))
                .flatMap(response -> {
                    ExceptionsHelper.checkResponse(response);
                    return examsDbHelper.updateSubjectChangesToNull();
                })
                .flatMap(howMany -> retrofit.getUserExams())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    ExceptionsHelper.checkResponse(response);

                    List<JsonExam> exams = response.body();

                    showInfo();
                    showExams(exams);
                }, error -> {
                    toastHelper.showErrorMessage(getString(R.string.toast_server_error), error);
                    progressBarParent.setVisibility(View.GONE);
                }, () -> progressBarParent.setVisibility(View.GONE));

    }


    private void showExams(List<JsonExam> exams) {
        LinearLayout parent = (LinearLayout) usernameView.getParent();
        for (int i = 0; i < exams.size(); i++) {
            TextView tv = new TextView(parent.getContext());
            tv.setText(exams.get(i).toString());
            tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            parent.addView(tv);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
