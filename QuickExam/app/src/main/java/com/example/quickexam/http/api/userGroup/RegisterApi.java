package com.example.quickexam.http.api.userGroup;

import com.example.quickexam.http.model.userGroup.RegisterResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RegisterApi {
    /**
     * 用户注册（自动识别人脸进行注册）
     *
     * 后端接口: POST /user/register
     * Content-Type: multipart/form-data
     *
     * @param account NEED
     * @param pwd NEED
     * @param photo NEED
     * @return Response
     */
    @Multipart
    @POST("user/login")
    Call<RegisterResponse> register(
            @Part("account") RequestBody account,
            @Part("pwd") RequestBody pwd,
            @Part MultipartBody.Part photo
    );
}
