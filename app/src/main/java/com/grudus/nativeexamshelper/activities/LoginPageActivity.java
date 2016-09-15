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

    private static final String TAG = "@@@@@@@@@@@@@";
    @BindView(R.id.login_view_login)
    AutoCompleteTextView loginTextView;
    
    @BindView(R.id.login_view_password)
    EditText passwordView;
    
    @BindView(R.id.login_view_login_button)
    Button loginButton;
    
    @BindView(R.id.login_view_registry_button)
    Button registerButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.login_view_login_button)
    public void tryToLogIn() {
        String userName = loginTextView.getText().toString();
        String password = passwordView.getText().toString();

        if (userName.trim().isEmpty() || password.trim().isEmpty()) {
            Toast.makeText(LoginPageActivity.this, "Musisz podać dane", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    // TODO: 16.09.16  CLEAN THIS CODE 


    @OnClick(R.id.login_view_registry_button)
    public void connect() {
        Log.e(TAG, "connect: start");
        loginSpring()
//                .flatMap(new Func1<String, Observable<?>>() {
//                    @Override
//                    public Observable<?> call(String s) {
//                        Log.e(TAG, "after login in connect method " + s);
//                        return getData();
//                    }
//                })
                .subscribeOn(Schedulers.io())
                .subscribe(s -> Log.e(TAG, "after retrieved data in connect method: " + s),
                        error -> Log.e(TAG, "ERROR", error));
    }

    private Observable<String> loginSpring() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                final String BASE_URL = "http://192.168.43.112:8080/post";

                String data = null;
                try {
                    Log.e(TAG, "call: try in " + BASE_URL);
                    data = URLEncoder.encode("username", "UTF-8")
                            + "=" + URLEncoder.encode("admin", "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String text = "";
                BufferedReader reader = null;

                // Send data
                try {

                    // Defined URL  where to send data
                    URL url = new URL(BASE_URL);

                    // Send POST data request

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    // Get the server response

                    Log.e(TAG, "call: conn status " + conn.getResponseCode());
                    Log.e(TAG, "call: conn mess " + conn.getResponseMessage());

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        sb.append(line + "\n");
                    }


                    text = sb.toString();
                    Log.e(TAG, "call: text: " + text);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {

                        reader.close();
                    } catch (Exception ex) {
                    }
                }

                Log.e(TAG, "loginSpring returns " + text);
                // Show response on activity
                subscriber.onNext(text);
            }

        });
    }

    private Observable<String> getData() {
        return Observable.create(new Observable.OnSubscribe<String>() {
             @Override
             public void call(Subscriber<? super String> subscriber) {


                 HttpURLConnection urlConnection = null;
                 BufferedReader reader = null;

                 String toReturn = null;

                 final String BASE_URL = "http://192.168.43.112:8080/users";

                 try {
                     Log.e(TAG, "getData start try " + BASE_URL);
                     URL url = new URL(Uri.parse(BASE_URL).toString());

                     urlConnection = (HttpURLConnection) url.openConnection();
                     urlConnection.setRequestMethod("GET");
                     urlConnection.connect();


                     Log.e(TAG, "call: konekt");

                     InputStream inputStream = urlConnection.getInputStream();
                     StringBuffer buffer = new StringBuffer();

                     reader = new BufferedReader(new InputStreamReader(inputStream));

                     String line;
                     while ((line = reader.readLine()) != null) {
                         // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                         // But it does make debugging a *lot* easier if you print out the completed
                         // buffer for debugging.
                         buffer.append(line).append("\n");
                     }

                     toReturn = buffer.toString();
                     Log.e(TAG, "getData returns " + toReturn);


                 } catch (IOException e) {
                     e.printStackTrace();
                 } finally {
                     if (urlConnection != null) {
                         urlConnection.disconnect();
                     }
                     if (reader != null) {
                         try {
                             reader.close();
                         } catch (final IOException e) {
                             Log.e("@@@@@@@@@", "Error closing stream", e);
                         }
                     }
                    subscriber.onNext(toReturn);
                 }

             }


        });
    }

}

