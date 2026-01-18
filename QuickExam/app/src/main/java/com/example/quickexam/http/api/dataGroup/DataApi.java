package com.example.quickexam.http.api.dataGroup;

import com.example.quickexam.http.model.dataGroup.DataResponse;
import com.example.quickexam.http.model.dataGroup.HealthGeneralRequest;
import com.example.quickexam.http.model.userGroup.RegisterResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DataApi {
    @Multipart
    @POST("examData/set/ecg")
    Call<DataResponse> setEcg(
            @Part("account") RequestBody account,
            @Part MultipartBody.Part data
    );

    @POST("/examData/set/tempor")
    Call<DataResponse> setTempor(
            @Body HealthGeneralRequest body
    );

    @POST("/examData/set/alcohol")
    Call<DataResponse> setAlcohol(
            @Body HealthGeneralRequest body
    );

    @POST("/examData/set/spo2")
    Call<DataResponse> setSpo2(
            @Body HealthGeneralRequest body
    );

    @POST("/examData/set/ppg")
    Call<DataResponse> setPPG(
            @Body HealthGeneralRequest body
    );

    @POST("/examData/set/blood_sys")
    Call<DataResponse> setBloodSys(
            @Body HealthGeneralRequest body
    );

    @POST("/examData/set/blood_dia")
    Call<DataResponse> setBloodDia(
            @Body HealthGeneralRequest body
    );

    @POST("/examData/set/blood_hr")
    Call<DataResponse> setBloodHr(
            @Body HealthGeneralRequest body
    );
}
