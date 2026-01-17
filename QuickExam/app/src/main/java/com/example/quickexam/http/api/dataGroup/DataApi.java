package com.example.quickexam.http.api.dataGroup;

import com.example.quickexam.http.model.userGroup.RegisterResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DataApi {
    @Multipart
    @POST("examData/set/ecg")
    Call<RegisterResponse> setEcg(
            @Part("account") RequestBody account,
            @Part MultipartBody.Part data
    );
}
