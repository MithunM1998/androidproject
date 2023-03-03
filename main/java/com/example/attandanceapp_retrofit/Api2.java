package com.example.attandanceapp_retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api2 {
    @POST("updateAttendance")
    Call<model> createPost(@Body model model);
    @FormUrlEncoded
    @POST("updateAttendance")
    Call<model> createPost(@Field("status") String status , @Field("msg") String msg);
}
