package com.example.cs2.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Rec_Data implements Serializable {

    @SerializedName("longitude")
    public float longitude;

    @SerializedName("latitude")
    public  float latitude;

    @SerializedName("Time")
    public  String Time;

    @SerializedName("RSRP")
    public int RSRP;

    @SerializedName("RSRQ")
    public int RSRQ;

    @SerializedName("SNIR")
    public int SNIR;


    public Rec_Data(float longitude, float latitude,String Time, int RSRP, int RSRQ, int SNIR) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.Time = Time;
        this.RSRP = RSRP;
        this.RSRQ = RSRQ;
        this.SNIR = SNIR;
    }
}
