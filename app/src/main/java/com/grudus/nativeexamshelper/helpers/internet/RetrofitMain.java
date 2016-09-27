package com.grudus.nativeexamshelper.helpers.internet;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.pojos.User;

import java.lang.reflect.Type;
import java.util.Date;

import retrofit2.Call;
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

    public Observable<Response<User.JsonUser>> getUserInfo(String username, String header) {
        return userService.getUser(username, header);
    }

    public Observable<Response<Void>> tryToLogin(String username, String password) {
        return userService.login(username, password);
    }


}
