package com.grudus.nativeexamshelper.helpers.internet;


import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.pojos.JsonExam;
import com.grudus.nativeexamshelper.pojos.JsonUser;

import java.util.Date;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class RetrofitMain {

    private final Context context;
    private final String BASE_URL;
    private final Retrofit retrofit;

    private ApiUserService userService;

    public RetrofitMain(Context context) {
        this.context = context.getApplicationContext();
        BASE_URL = this.context.getString(R.string.net_host);

        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeHierarchyAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context1)
                -> new Date(json.getAsJsonPrimitive().getAsLong()));

        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(builder.create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        userService = this.retrofit.create(ApiUserService.class);
    }

    public Observable<Response<JsonUser>> getUserInfo(String username, String header) {
        Log.d("@@@@@@@@RETRO", "getUserInfo: " + username + ". " + header);
        return userService.getUser(username, header);
    }

    public Observable<Response<Void>> tryToLogin(String username, String password) {
        return userService.login(username, password);
    }

    public Observable<Response<List<JsonExam>>> getUserExams(String username, String header) {
        return userService.getUserExams(username, header);
    }


}
