package com.example.quickexam.repository;

import com.example.quickexam.http.HttpRequest;
import com.example.quickexam.http.api.dataGroup.DataApi;
import com.example.quickexam.http.api.userGroup.LoginApi;
import com.example.quickexam.http.api.userGroup.RegisterApi;
import com.example.quickexam.http.model.dataGroup.DataResponse;
import com.google.gson.Gson;

import java.lang.reflect.Array;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;

public class DataRepository {
    private final DataApi dataApi;

    public DataRepository(){
        Gson gson = new Gson();
        Retrofit retrofit = HttpRequest.getRetrofit(gson);
        dataApi = retrofit.create(DataApi.class);
    }
}
