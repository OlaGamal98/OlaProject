package com.example.cs2.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static final String baseURL = "http://192.168.1.36";
    private static final String URL = "http://localhost:88/";

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
           //  .readTimeout(30, TimeUnit.MINUTES)
                .connectTimeout(60, TimeUnit.MILLISECONDS)
       // writeTimeout(300,TimeUnit.MILLISECONDS).addInterceptor(interceptor)
         .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())

                .client(client)
                .build();


        return retrofit;
    }
}
