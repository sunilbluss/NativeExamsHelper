package com.grudus.nativeexamshelper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.helpers.ToastHelper;
import com.grudus.nativeexamshelper.helpers.internet.RetrofitMain;
import com.grudus.nativeexamshelper.pojos.JsonExam;
import com.grudus.nativeexamshelper.pojos.JsonUser;
import com.grudus.nativeexamshelper.pojos.UserPreferences;

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

        tryToReceiveData();
    }

    private void showInfo() {
        usernameView.setText(jsonUser.getUsername());
        emailView.setText(jsonUser.getEmail());

    }

    private void tryToReceiveData() {
        subscription = retrofit
                .getUserInfo(user.getUsername(), user.getToken())
                .flatMap(response -> {
                    if (response.code() != HttpURLConnection.HTTP_OK) {
                        toastHelper.tryToShowErrorMessage(response);
                        return Observable.empty();
                    }

                    this.jsonUser = response.body();
                    Log.d(TAG, "tryToReceiveData: response: " + jsonUser);
                    return retrofit.getUserExams(user.getUsername(), user.getToken());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.code() != HttpURLConnection.HTTP_OK) {
                        toastHelper.tryToShowErrorMessage(response);
                        return;
                    }
                    List<JsonExam> exams = response.body();
                    Log.d(TAG, "tryToReceiveData: exams " + exams );

                    showInfo();
                    showExams(exams);

                }, error -> toastHelper.showErrorMessage(getString(R.string.toast_server_error), error));
    }

    private void showExams(List<JsonExam> exams) {
        LinearLayout parent = (LinearLayout) usernameView.getParent();
        for (int i = 0; i < exams.size(); i++) {
            TextView tv = new TextView(parent.getContext());
            tv.setText(exams.get(i).getSubject().getTitle() + ", " + exams.get(i).getDate());
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
