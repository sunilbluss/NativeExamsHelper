package com.grudus.nativeexamshelper.net;


import com.grudus.nativeexamshelper.pojos.JsonExam;
import com.grudus.nativeexamshelper.pojos.JsonSubject;
import com.grudus.nativeexamshelper.pojos.JsonUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface ApiUserService {

    String HEADER_TOKEN = "X-AUTH-TOKEN";

    @GET("/api/user/{username}")
    Observable<Response<JsonUser>> getUser(@Path("username") String username, @Header(HEADER_TOKEN) String token);

    @POST("/login")
    @FormUrlEncoded
    Observable<Response<Void>> login(@Field("username") String username, @Field("password") String password);

    @GET("api/user/{username}/exams")
    Observable<Response<List<JsonExam>>> getUserExams(@Path("username") String username, @Header(HEADER_TOKEN) String token);

    @POST("api/user/{username}/subjects")
    @FormUrlEncoded
    Observable<Response<Void>> insertSubjects(@Path("username") String username, @Header(HEADER_TOKEN) String token, @Field("subjects[]") ArrayList<JsonSubject> subjects);

}
