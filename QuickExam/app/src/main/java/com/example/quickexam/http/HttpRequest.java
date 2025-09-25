package com.example.quickexam.http;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRequest {

    private static OkHttpClient okHttpClient = null;

    public static Retrofit getRetrofit(Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkHttpClient())
                .build();
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null)
            okHttpClient = new OkHttpClient.Builder().build();
        return okHttpClient;
    }

    public static RequestBody getBody(String json) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        return body;
    }

}
