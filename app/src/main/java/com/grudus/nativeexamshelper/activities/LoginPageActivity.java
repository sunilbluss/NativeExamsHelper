package com.grudus.nativeexamshelper.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.helpers.internet.RetrofitMain;
import com.grudus.nativeexamshelper.pojos.User;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginPageActivity extends AppCompatActivity {

    private static final String TAG = "@@@@@@@@@@@@@" + LoginPageActivity.class.getSimpleName();
    @BindView(R.id.login_view_login)
    AutoCompleteTextView loginTextView;
    
    @BindView(R.id.login_view_password)
    EditText passwordView;
    
    @BindView(R.id.login_view_login_button)
    Button loginButton;
    
    @BindView(R.id.login_view_registry_button)
    Button registerButton;

    private User user;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.login_view_login_button)
    public void tryToLogIn() {
        String userName = loginTextView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();


        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginPageActivity.this, "Musisz podać dane", Toast.LENGTH_SHORT).show();
            return;
        }

        user = new User(userName);

        new RetrofitMain(this)
                .tryToLogin(userName, password)
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    Log.e(TAG, "connect: status is " + response.code());
                    Log.e(TAG, "connect: body is: " + response.body());
                    Log.e(TAG, "connect: headers are: " + response.headers());

                   String token = response.headers().get("X-AUTH-TOKEN");
                    user.setToken(token);

                }, error -> Log.e(TAG, "connect: " + error.getMessage(), error));


    }


    @OnClick(R.id.login_view_registry_button)
    public void connect() {


        // TODO: 27.09.16 rewrite
        new RetrofitMain(this)
                .getUserInfo(user.getUsername(), user.getToken())
                .subscribeOn(Schedulers.io())
        .subscribe(response -> {
            Log.e(TAG, "connect: response " + response.body());

        }, error -> Log.e(TAG, "connect: " + error.getMessage(), error));

    }


}

