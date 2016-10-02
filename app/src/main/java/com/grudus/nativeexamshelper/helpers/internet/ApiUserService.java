package com.grudus.nativeexamshelper.helpers.internet;


import com.grudus.nativeexamshelper.pojos.JsonExam;
import com.grudus.nativeexamshelper.pojos.JsonUser;

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

    @GET("/api/user/{username}")
    Observable<Response<JsonUser>> getUser(@Path("username") String username, @Header("X-AUTH-TOKEN") String token);

    @POST("/login")
    @FormUrlEncoded
    Observable<Response<Void>> login(@Field("username") String username, @Field("password") String password);

    @GET("api/user/{username}/exams")
    Observable<Response<List<JsonExam>>> getUserExams(@Path("username") String username, @Header("X-AUTH-TOKEN") String token);
}
