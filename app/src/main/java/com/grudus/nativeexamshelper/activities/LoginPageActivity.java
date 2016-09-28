package com.grudus.nativeexamshelper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.helpers.internet.RetrofitMain;
import com.grudus.nativeexamshelper.pojos.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

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

    private UserPreferences userPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        ButterKnife.bind(this);

        AUTH_HEADER = getString(R.string.net_auth_header);
        retrofit = new RetrofitMain(this);

        userPreferences = new UserPreferences(this);
    }


    @OnClick(R.id.login_view_login_button)
    public void tryToLogIn() {
        String username = loginTextView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();


        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginPageActivity.this, getString(R.string.toast_empty_creditionals), Toast.LENGTH_SHORT).show();
            return;
        }


        subscription = retrofit
                .tryToLogin(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    final int code = response.code();
                    if (code != HttpsURLConnection.HTTP_OK) {
                        tryToShowErrorMessage(response);
                        return;
                    }

                    commitLogin(response);
                    userPreferences.changeUsername(username);

                }, error -> showMessage(getString(R.string.toast_server_error)));

    }

    private void commitLogin(Response<Void> response) {
        String token = response.headers().get(AUTH_HEADER);

        final UserPreferences.User user = userPreferences.getLoggedUser();

        if (!user.getToken().equals(token))
            userPreferences.changeToken(token);

        userPreferences.changeLoginStatus(true);

        Toast.makeText(LoginPageActivity.this, getString(R.string.toast_successful_login), Toast.LENGTH_SHORT).show();
    }

    private void tryToShowErrorMessage(Response<?> response) {
        try {
            showError(response);
        } catch (IOException | JSONException e) {
            showMessage(getString(R.string.toast_unexpected_error));
        }
    }

    private void showError(Response<?> response) throws IOException, JSONException {
        final String jsonMessage = response.errorBody().string();
        final String errorMessage = new JSONObject(jsonMessage).getString("message");
        showMessage(errorMessage);
    }

    private void showMessage(String message) {
        Toast.makeText(LoginPageActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.login_view_registry_button)
    public void connect() {

        final UserPreferences.User user = userPreferences.getLoggedUser();

        subscription = new RetrofitMain(this)
                .getUserInfo(user.getUsername(), user.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.code() != HttpURLConnection.HTTP_OK) {
                        tryToShowErrorMessage(response);
                        return;
                    }

                    showMessage("Hello, " + response.body().getUsername());

                }, error -> showMessage(getString(R.string.toast_server_error)));

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (subscription != null && subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}

