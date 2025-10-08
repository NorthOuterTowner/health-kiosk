package com.example.quickexam.repository;

import com.example.quickexam.http.HttpRequest;
import com.example.quickexam.http.api.userGroup.LoginApi;
import com.example.quickexam.http.model.userGroup.LoginResponse;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.example.quickexam.http.api.userGroup.RegisterApi;
import com.example.quickexam.http.model.userGroup.RegisterResponse;


public class UserRepository {
    private final LoginApi loginApi;
    private final RegisterApi registerApi;

    public UserRepository(){
        Gson gson = new Gson();
        Retrofit retrofit = HttpRequest.getRetrofit(gson);
        loginApi = retrofit.create(LoginApi.class);
        registerApi = retrofit.create(RegisterApi.class);
    }

    public void login(String account, String pwd, File photoFile, final LoginCallback callback) {
        // 构建 RequestBody
        RequestBody accountBody = account != null ? RequestBody.create(MediaType.parse("text/plain"), account) : null;
        RequestBody pwdBody = pwd != null ? RequestBody.create(MediaType.parse("text/plain"), pwd) : null;

        MultipartBody.Part photoPart = null;
        if (photoFile != null && photoFile.exists()) {
            RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), photoFile);
            photoPart = MultipartBody.Part.createFormData("photo", photoFile.getName(), photoBody);
        }

        Call<LoginResponse> call = loginApi.login(accountBody, pwdBody, photoPart);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("登录失败，响应码：" + response.code()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
    public void register(String account, String pwd, File photoFile, final RegisterCallback callback) {
        RequestBody accountBody = RequestBody.create(MediaType.parse("text/plain"), account);
        RequestBody pwdBody = RequestBody.create(MediaType.parse("text/plain"), pwd);

        MultipartBody.Part photoPart = null;
        if (photoFile != null && photoFile.exists()) {
            RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), photoFile);
            photoPart = MultipartBody.Part.createFormData("photo", photoFile.getName(), photoBody);
        }

        Call<RegisterResponse> call = registerApi.register(accountBody, pwdBody, photoPart);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("注册失败，响应码：" + response.code()));
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface LoginCallback {
        void onSuccess(LoginResponse response);
        void onError(Throwable t);
    }

    public interface RegisterCallback {
        void onSuccess(RegisterResponse response);
        void onError(Throwable t);
    }

}
/** Using Example Of Login
 *
 * UserRepository userRepo = new UserRepository();
 * 1.账号密码登录
 * userRepo.login("admin", "123456", null, new UserRepository.LoginCallback() {
 *     @Override
 *     public void onSuccess(LoginResponse response) {
 *         System.out.println("登录成功：" + response.getToken());
 *     }
 *
 *     @Override
 *     public void onError(Throwable t) {
 *         t.printStackTrace();
 *     }
 * });
 *
 * 2.人脸登录
 * File photo = new File("/path/to/photo.jpg");
 * userRepo.login(null, null, photo, new UserRepository.LoginCallback() {
 *     @Override
 *     public void onSuccess(LoginResponse response) {
 *         System.out.println("人脸登录成功：" + response.getToken());
 *     }
 *
 *     @Override
 *     public void onError(Throwable t) {
 *         t.printStackTrace();
 *     }
 * });
 */

/** Using Example of register
 *
 * UserRepository userRepo = new UserRepository();
 * File photo = new File("/path/to/photo.jpg");
 * userRepo.register("newUser", "123456", photo, new UserRepository.RegisterCallback() {
 *     @Override
 *     public void onSuccess(RegisterResponse response) {
 *         System.out.println("注册成功：" + response.getMsg());
 *     }
 *
 *     @Override
 *     public void onError(Throwable t) {
 *         t.printStackTrace();
 *     }
 * });
 */