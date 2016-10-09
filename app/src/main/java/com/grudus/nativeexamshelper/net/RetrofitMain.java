package com.grudus.nativeexamshelper.net;


import android.content.Context;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.grudus.nativeexamshelper.R;
import com.grudus.nativeexamshelper.pojos.JsonExam;
import com.grudus.nativeexamshelper.pojos.JsonSubject;
import com.grudus.nativeexamshelper.pojos.JsonUser;
import com.grudus.nativeexamshelper.pojos.UserPreferences;

import java.util.ArrayList;
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
    private final UserPreferences userPreferences;

    private final ApiUserService userService;
    private final ApiSubjectService subjectService;
    private final ApiExamService examService;

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
        subjectService = this.retrofit.create(ApiSubjectService.class);
        examService = this.retrofit.create(ApiExamService.class);
        userPreferences = new UserPreferences(context);

    }

    public Observable<Response<JsonUser>> getUserInfo() {
        UserPreferences.User user = userPreferences.getLoggedUser();
        return userService.getUser(user.getUsername(), user.getToken());
    }

    public Observable<Response<Void>> tryToLogin(String username, String password) {
        return userService.login(username, password);
    }

    public Observable<Response<List<JsonExam>>> getUserExams() {
        UserPreferences.User user = userPreferences.getLoggedUser();
        return examService.getUserExams(user.getUsername(), user.getToken());
    }

    public Observable<Response<Void>> insertSubjects(ArrayList<JsonSubject> subjects) {
        UserPreferences.User user = userPreferences.getLoggedUser();
        return subjectService.insertSubjects(user.getUsername(), user.getToken(), subjects);
    }

    public Observable<Response<Void>> updateSubject(JsonSubject subject) {
        UserPreferences.User user = userPreferences.getLoggedUser();
        return subjectService.updateSubject(user.getUsername(), subject.getId(), user.getToken(), subject);
    }


    public Observable<Response<Void>> addNewSubject(JsonSubject jsonSubject) {
        UserPreferences.User user = userPreferences.getLoggedUser();
        return subjectService.createSubject(user.getUsername(), user.getToken(), jsonSubject);
    }
}
