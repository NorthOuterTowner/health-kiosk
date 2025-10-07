package com.example.quickexam.http.api.userGroup;

import com.example.quickexam.http.model.userGroup.LoginResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * @apiGroup User
 * 登录接口
 */
public interface LoginApi {

    /**
     * 用户登录（支持账号密码登录或人脸识别登录）
     *
     * 后端接口: POST /user/login
     * Content-Type: multipart/form-data
     *
     * @param account 用户账号（人脸登录可为空）
     * @param pwd 密码（账号登录时必填）
     * @param photo 用户照片（人脸登录时必填）
     * @return 登录响应
     */
    @Multipart
    @POST("user/login")
    Call<LoginResponse> login(
            @Part("account") RequestBody account,
            @Part("pwd") RequestBody pwd,
            @Part MultipartBody.Part photo
    );
}
