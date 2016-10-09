package com.grudus.nativeexamshelper.net;


import com.grudus.nativeexamshelper.pojos.JsonExam;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

import static com.grudus.nativeexamshelper.net.ApiUserService.HEADER_TOKEN;

public interface ApiExamService {

    String BASE_URL = com.grudus.nativeexamshelper.net.ApiUserService.BASE_URL + "/exams";

    @GET(BASE_URL)
    Observable<Response<List<JsonExam>>> getUserExams(@Path("username") String username, @Header(HEADER_TOKEN) String token);

}
