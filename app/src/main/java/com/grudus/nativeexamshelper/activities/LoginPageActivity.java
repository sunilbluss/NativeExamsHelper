package com.grudus.nativeexamshelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.helpers.ExceptionsHelper;
import com.grudus.nativeexamshelper.helpers.ToastHelper;
import com.grudus.nativeexamshelper.net.RetrofitMain;
import com.grudus.nativeexamshelper.pojos.UserPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginPageActivity extends AppCompatActivity {

    private static final String TAG = "@@@@@@@@@@@@@" + LoginPageActivity.class.getSimpleName();
    private String AUTH_HEADER;

    private Subscription subscription;
    private RetrofitMain retrofit;


    @BindView(R.id.login_view_login)
    AutoCompleteTextView loginTextView;
    
    @BindView(R.id.login_view_password)
    EditText passwordView;
    
    @BindView(R.id.login_view_login_button)
    Button loginButton;
    
    @BindView(R.id.login_view_registry_button)
    Button registerButton;

    @BindView(R.id.progress_bar_login)
    ProgressBar progressBar;

    @BindView(R.id.progress_bar_login_parent)
    LinearLayout progressBarParent;

    private UserPreferences userPreferences;
    private ToastHelper toastHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        ButterKnife.bind(this);

        AUTH_HEADER = getString(R.string.net_auth_header);
        retrofit = new RetrofitMain(this);

        userPreferences = new UserPreferences(this);
        toastHelper = new ToastHelper(this);
    }


    @OnClick(R.id.login_view_login_button)
    public void tryToLogIn() {
        String username = loginTextView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();


        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginPageActivity.this, getString(R.string.toast_empty_creditionals), Toast.LENGTH_SHORT).show();
            return;
        }


       logInToServer(username, password);
    }

    private void logInToServer(String username, String password) {
        progressBarParent.setVisibility(View.VISIBLE);
        subscription = retrofit
                .tryToLogin(username, password)
                .flatMap(response -> {
                    ExceptionsHelper.checkLoginResponse(response);

                    commitLogin(response);

                    return retrofit.getUserInfo();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    ExceptionsHelper.checkResponse(response);

                    userPreferences.changeId(response.body().getId());
                    userPreferences.changeUsername(response.body().getUsername());
                    toastHelper.showMessage(getString(R.string.toast_successful_login));

                }, error -> {
                    progressBarParent.setVisibility(View.GONE);
                    toastHelper.showErrorMessage(error.getMessage(), error);
                }, () -> {
                    progressBarParent.setVisibility(View.GONE);
                    LoginPageActivity.this.finish();
                    this.startActivity(new Intent(getApplicationContext(), SyncActivity.class));
                });
    }

    private void commitLogin(Response<Void> response) {
        String token = response.headers().get(AUTH_HEADER);

        final UserPreferences.User user = userPreferences.getLoggedUser();

        if (!user.getToken().equals(token))
            userPreferences.changeToken(token);

        userPreferences.changeLoginStatus(true);
    }


    @OnClick(R.id.login_view_registry_button)
    public void register() {

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (subscription != null && subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}

