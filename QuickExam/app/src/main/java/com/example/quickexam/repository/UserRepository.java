package com.example.quickexam.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;

import com.example.quickexam.http.HttpRequest;
import com.example.quickexam.http.api.userGroup.LoginApi;
import com.example.quickexam.http.model.TestResponse;
import com.example.quickexam.http.model.userGroup.LoginResponse;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
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
import com.seeta.mvp.MainFragment;

import org.opencv.core.Mat;

public class UserRepository {
    private final LoginApi loginApi;
    private final RegisterApi registerApi;

    public UserRepository(){
        Gson gson = new Gson();
        Retrofit retrofit = HttpRequest.getRetrofit(gson);
        loginApi = retrofit.create(LoginApi.class);
        registerApi = retrofit.create(RegisterApi.class);
    }

    public void login(String account, String pwd, byte[] photoYUVBytes, final LoginCallback callback) {
        // 构建 RequestBody
        RequestBody accountBody = account != null ? RequestBody.create(MediaType.parse("text/plain"), account) : null;
        RequestBody pwdBody = pwd != null ? RequestBody.create(MediaType.parse("text/plain"), pwd) : null;

        MultipartBody.Part photoPart = null;
        if (photoYUVBytes != null) {
            byte[] photoBytes = yuvToJpeg(
                    photoYUVBytes,
                    MainFragment.getmPreviewSize().width,
                    MainFragment.getmPreviewSize().height
            );

            /* Solve the problem of up-down */
            // Transform JPEG to Bitmap
//            Bitmap bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
//
//            // Flipped picture
//            Matrix matrix = new Matrix();
//            matrix.postScale(1f, -1f, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
//
//            Bitmap flipped = Bitmap.createBitmap(
//                    bitmap,
//                    0,0,
//                    bitmap.getWidth(),
//                    bitmap.getHeight(),
//                    matrix,
//                    true
//            );
//
//            // Transform Bitmap to JPEG byte[]
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            flipped.compress(Bitmap.CompressFormat.JPEG, 90, baos);
//            photoBytes = baos.toByteArray();

            RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), photoBytes);
            photoPart = MultipartBody.Part.createFormData("photo", "face.jpg", photoBody);
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
    public void register(String account, String pwd, byte[] photoYUVBytes, final RegisterCallback callback) {
        RequestBody accountBody = RequestBody.create(MediaType.parse("text/plain"), account);
        RequestBody pwdBody = RequestBody.create(MediaType.parse("text/plain"), pwd);

        MultipartBody.Part photoPart = null;
        if (photoYUVBytes != null) {
            byte[] photoBytes = yuvToJpeg(
                    photoYUVBytes,
                    MainFragment.getmPreviewSize().width,
                    MainFragment.getmPreviewSize().height
            );

            /* Solve the problem of up-down */
            // Transform JPEG to Bitmap
//            Bitmap bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
//
//            // Flipped picture
//            Matrix matrix = new Matrix();
//            matrix.postScale(1f, -1f, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
//
//            Bitmap flipped = Bitmap.createBitmap(
//                    bitmap,
//                    0,0,
//                    bitmap.getWidth(),
//                    bitmap.getHeight(),
//                    matrix,
//                    true
//            );
//
//            // Transform Bitmap to JPEG byte[]
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            flipped.compress(Bitmap.CompressFormat.JPEG, 90, baos);
//            photoBytes = baos.toByteArray();

            RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), photoBytes);
            photoPart = MultipartBody.Part.createFormData("photo", "face.jpg", photoBody);
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

    /**
     * 将 YUV420/NV21 数据转换为 JPEG 数据
     *
     * @param yuvData Camera 预览得到的 byte[] 数据
     * @param width   预览宽度
     * @param height  预览高度
     * @return JPEG 格式的 byte[]
     */
    private byte[] yuvToJpeg(byte[] yuvData, int width, int height) {
        // 创建 YuvImage 对象
        YuvImage yuvImage = new YuvImage(yuvData, ImageFormat.NV21, width, height, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 压缩为 JPEG，质量 100 表示无损压缩
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, baos);

        return baos.toByteArray();
    }


    public interface LoginCallback {
        void onSuccess(LoginResponse response);
        void onError(Throwable t);
    }

    public interface RegisterCallback {
        void onSuccess(RegisterResponse response);
        void onError(Throwable t);
    }

    public interface TestCallback {
        void onSuccess(TestResponse response);
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