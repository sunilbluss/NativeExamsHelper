package com.grudus.nativeexamshelper.net;


import com.grudus.nativeexamshelper.pojos.JsonUser;

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
    String BASE_URL = "/api/user/{username}";

    @GET(BASE_URL)
    Observable<Response<JsonUser>> getUser(@Path("username") String username, @Header(HEADER_TOKEN) String token);

    @POST("/login")
    @FormUrlEncoded
    Observable<Response<Void>> login(@Field("username") String username, @Field("password") String password);

}
