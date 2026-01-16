package com.example.quickexam.http.api.userGroup;

import com.example.quickexam.http.model.TestResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TestApi {
    @GET("test")
    Call<TestResponse> test();
}
