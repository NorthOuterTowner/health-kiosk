package com.example.quickexam.repository;

import com.example.quickexam.http.HttpRequest;
import com.example.quickexam.http.api.dataGroup.DataApi;
import com.example.quickexam.http.model.dataGroup.DataResponse;
import com.example.quickexam.http.model.dataGroup.HealthGeneralRequest;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DataRepository {
    private final DataApi dataApi;

    public DataRepository(){
        Gson gson = new Gson();
        Retrofit retrofit = HttpRequest.getRetrofit(gson);
        dataApi = retrofit.create(DataApi.class);
    }

    /**
     * @param accountStr account of examination people
     * @param ecgFile transform ECG stream data into file and input here
     * @param callback things to do after result come back
     */
    public void submitECGInfo(String accountStr, File ecgFile, final DataCallback callback) {
        RequestBody accountBody = accountStr != null ? RequestBody.create(
                MediaType.parse("text/plain"),
                accountStr
        ) : null;

        RequestBody fileBody = ecgFile != null ? RequestBody.create(
                MediaType.parse("application/octet-stream"),
                ecgFile
        ) : null;

        MultipartBody.Part dataPart = null;
        if(ecgFile != null) {
            dataPart = MultipartBody.Part.createFormData(
                    "data",
                    ecgFile.getName(),
                    fileBody
            );
        }else {
            dataPart = MultipartBody.Part.createFormData(
                    "data",
                    ""
            );
        }

        Call<DataResponse> call = dataApi.setEcg(accountBody, dataPart);
        call.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                }
                else {
                    callback.onError(new Exception("发送失败，响应码：" + response.code()));
                }
            }
            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void submitAlcoholInfo(String account, String data, final DataCallback callback) {
        HealthGeneralRequest req = new HealthGeneralRequest(account, data);
        executeCall(dataApi.setAlcohol(req), callback);
    }

    public void submitSPO2Info(String account, String data, final DataCallback callback) {
        HealthGeneralRequest req = new HealthGeneralRequest(account, data);
        executeCall(dataApi.setSpo2(req), callback);
    }

    public void submitPPGInfo(String account, String data, final DataCallback callback) {
        HealthGeneralRequest req = new HealthGeneralRequest(account, data);
        executeCall(dataApi.setPPG(req), callback);
    }

    public void submitBloodDiaInfo(String account, String data, final DataCallback callback) {
        HealthGeneralRequest req = new HealthGeneralRequest(account, data);
        executeCall(dataApi.setBloodDia(req), callback);
    }

    public void submitBloodSysInfo(String account, String data, final DataCallback callback) {
        HealthGeneralRequest req = new HealthGeneralRequest(account, data);
        executeCall(dataApi.setBloodSys(req), callback);
    }

    public void submitBloodHrInfo(String account, String data, final DataCallback callback) {
        HealthGeneralRequest req = new HealthGeneralRequest(account, data);
        executeCall(dataApi.setBloodHr(req), callback);
    }

    public void submitTemporInfo(String account, String data, final DataCallback callback) {
        HealthGeneralRequest req = new HealthGeneralRequest(account, data);
        executeCall(dataApi.setTempor(req), callback);
    }

    private void executeCall(Call<DataResponse> call, final DataCallback callback) {
        call.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) callback.onSuccess(response.body());
                else callback.onError(new Exception("发送失败，响应码：" + response.code()));
            }
            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface DataCallback {
        void onSuccess(DataResponse response);
        void onError(Throwable t);
    }
}

//Using Example Of Data transmission
//DataRepository dataRepo = new DataRepository();
//
//        dataRepo.submitAlcoholInfo("admin", "1", new DataRepository.DataCallback() {
//    @Override
//    public void onSuccess(DataResponse response) {
//        System.out.println("发送成功：");
//        // Show Success Toast
//    }
//    @Override
//    public void onError(Throwable t) {
//        t.printStackTrace();
//        // Show Error Toast
//    }
//});