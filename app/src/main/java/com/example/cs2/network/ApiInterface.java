package com.example.cs2.network;

import com.example.cs2.model.Rec_Data;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("/GP/CrowdSourcing/")
    Call<ArrayList<Rec_Data>> postUserData(@Body ArrayList<Rec_Data> rec_data);
}
