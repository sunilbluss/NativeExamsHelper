package com.grudus.nativeexamshelper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.database.ExamsDbHelper;
import com.grudus.nativeexamshelper.database.converters.CursorToArrayConverter;
import com.grudus.nativeexamshelper.helpers.ToastHelper;
import com.grudus.nativeexamshelper.net.RetrofitMain;
import com.grudus.nativeexamshelper.pojos.JsonExam;
import com.grudus.nativeexamshelper.pojos.JsonUser;
import com.grudus.nativeexamshelper.pojos.UserPreferences;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SyncActivity extends AppCompatActivity {

    private static final String TAG = "@@@@" + SyncActivity.class.getSimpleName();
    @BindView(R.id.sync_username) TextView usernameView;
    @BindView(R.id.sync_email) TextView emailView;

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
        subscription = retrofit
                .getUserInfo()
                .flatMap(response -> {
                    if (response.code() != HttpURLConnection.HTTP_OK) {
                        toastHelper.tryToShowErrorMessage(response);
                        return Observable.empty();
                    }

                    this.jsonUser = response.body();
                    Log.d(TAG, "tryToReceiveData: response: " + jsonUser);

                    return new CursorToArrayConverter(getApplicationContext()).getChangedSubjectsAsJson();
                })
                .flatMap(array -> retrofit.insertSubjects(array))
                .flatMap(response -> {
                    if (response.code() == HttpURLConnection.HTTP_OK) {
                        return examsDbHelper.updateSubjectChangesToNull();
                    }
                    try {
                        Log.e(TAG, "tryToReceiveData: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, "tryToReceiveData: ", e);
                    }
                    return Observable.empty();
                })
                .flatMap(howMany -> retrofit.getUserExams())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.code() != HttpURLConnection.HTTP_OK) {
                        toastHelper.tryToShowErrorMessage(response);
                        return;
                    }
                    List<JsonExam> exams = response.body();


                    showInfo();
                    showExams(exams);
                }, error -> toastHelper.showErrorMessage(getString(R.string.toast_server_error), error));

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
