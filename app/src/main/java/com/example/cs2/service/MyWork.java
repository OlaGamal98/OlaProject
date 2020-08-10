package com.example.cs2.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.cs2.data.DataBaseAdapter;
import com.example.cs2.model.Rec_Data;
import com.example.cs2.network.ApiClient;
import com.example.cs2.network.ApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyWork extends Worker {
    DataBaseAdapter dataBaseAdapter;
    public MyWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dataBaseAdapter = new DataBaseAdapter(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("MY WORK","FOUND");


        ArrayList<Rec_Data> data = dataBaseAdapter.getDataList();
        Log.e("GetCashed Dta",data.size()+"FOUND");
        Call<ArrayList<Rec_Data>> call = ApiClient.getClient().create(ApiInterface.class).postUserData(data);
        call.enqueue(new Callback<ArrayList<Rec_Data>>() {
            @Override
            public void onResponse(Call<ArrayList<Rec_Data>> call, Response<ArrayList<Rec_Data>> response) {
               // dataBaseAdapter.deleteAll();
               // Toast.makeText(MyWork.this,"call success ",Toast.LENGTH_SHORT).show();
                Log.e ("Tag1","Suceess");
            }
            @Override
            public void onFailure(Call<ArrayList<Rec_Data>> call, Throwable t) {
             // retransmission
                //Toast.makeText(MyWork.this,"call failure",Toast.LENGTH_SHORT).show();
               // Log.e ("Tag2","ff");

                Log.e ("Tag1",t.getMessage()+"Fail");
                Log.e ("Tag2",t.getCause()+"Fail");
                Log.e ("Tag3",t.toString()+"Fail");




            }
        });

        return Result.success();
    }
}


