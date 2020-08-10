package com.example.cs2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.cs2.Message;
import com.example.cs2.model.Rec_Data;

import java.util.ArrayList;

public final class DataBaseAdapter {

    DataBaseHelper helper;


    private StringBuffer stringBuffer;

    public DataBaseAdapter(Context context) {
        helper =new DataBaseHelper(context);
    }

    //public long insertData( String time,int rsrq, int rsrp, int snir) {
    public long insertData(String longitude, String latitude , String time,int rsrq, int rsrp, int snir) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.COLUMN_LONGITUDE,longitude);
        contentValues.put(DataBaseHelper.COLUMN_LATITUDE,latitude);
        contentValues.put(DataBaseHelper.COLUMN_TIME,time);
        contentValues.put(DataBaseHelper.COLUMN_RSRQ,rsrq);
        contentValues.put(DataBaseHelper.COLUMN_RSRP,rsrp);
        contentValues.put(DataBaseHelper.COLUMN_SNIR,snir);

        long result = db.insert(DataBaseHelper.TABLE_NAME,null ,contentValues);

        return result;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from "+ DataBaseHelper.TABLE_NAME);
        db.close();
    }



    public String getData(){
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns={DataBaseHelper.COLUMN_LONGITUDE,DataBaseHelper.COLUMN_LATITUDE,DataBaseHelper.COLUMN_TIME,DataBaseHelper.COLUMN_RSRQ,DataBaseHelper.COLUMN_RSRP,DataBaseHelper.COLUMN_SNIR};
        Cursor cursor =db.query(DataBaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        stringBuffer=new StringBuffer();
        if(cursor.getCount()==0)
            return "Nothing found";
        else {
            while (cursor.moveToNext()) {
                // String rsrp = cursor.getString(3);
                //String rsrq = cursor.getString(2);
                //String snir = cursor.getString(4);
                //String time = cursor.getString(1);
                //String loca =cursor.getString(4);
                stringBuffer.append("Long :"+ cursor.getString(0)+", ");
                stringBuffer.append("Lat :"+ cursor.getString(1)+", ");
                stringBuffer.append("Time :"+ cursor.getString(2)+", ");
                stringBuffer.append("Rsrq :"+ cursor.getString(3)+", ");
                stringBuffer.append("rsrp :"+ cursor.getString(4)+", ");
                stringBuffer.append("snir :"+ cursor.getString(5)+"\n");
                //stringBuffer.append(time + " " + rsrq + " " + rsrp + " " + snir + "\n");
            }

            cursor.close();
            return stringBuffer.toString();
        }
    }

    public ArrayList<Rec_Data> getDataList(){
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns={DataBaseHelper.COLUMN_LONGITUDE,DataBaseHelper.COLUMN_LATITUDE,DataBaseHelper.COLUMN_TIME,DataBaseHelper.COLUMN_RSRQ,DataBaseHelper.COLUMN_RSRP,DataBaseHelper.COLUMN_SNIR};
        Cursor cursor =db.query(DataBaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        ArrayList<Rec_Data> result=new ArrayList<>();
            while (cursor.moveToNext()) {
                result.add(new Rec_Data(cursor.getFloat(0),
                        cursor.getFloat(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getInt(5)));
            }
            cursor.close();
            return result;

    }
    public Cursor getAllData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+DataBaseHelper.TABLE_NAME,null);
        return res;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    static class DataBaseHelper extends SQLiteOpenHelper{
        public static final String DATABASE_NAME = "networkMeasurements.db";

        public static final String TABLE_NAME = "measurements";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_RSRP = "rsrp";
        public final static String COLUMN_RSRQ = "rsrq";
        public final static String COLUMN_SNIR = "snir";
        public final static String COLUMN_LATITUDE = "latitude";
        public final static String COLUMN_LONGITUDE = "longitude";
        public final static String COLUMN_TIME = "time";

        public final static int DATABASE_VERSION=1;

        private Context context;

        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context=context;
//            Message.message(context, "constructor called");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String SQL_CREATE_TABLE =  "CREATE TABLE " +TABLE_NAME + " ("
                    +_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_LONGITUDE + " TEXT, "
                    + COLUMN_LATITUDE + " TEXT, "
                    + COLUMN_TIME + " TEXT, "
                    + COLUMN_RSRQ + " INTEGER, "
                    + COLUMN_RSRP + " INTEGER, "
                    + COLUMN_SNIR + " INTEGER );";

            try {
                db.execSQL(SQL_CREATE_TABLE);
                Message.message(context, "onCreate Called ");
            } catch (SQLException e) {
                Message.message(context, " "+e);
            }


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context, "onUprgrade Called ");
                db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
                onCreate(db);
            } catch (SQLException e) {
                Message.message(context, " "+e);
            }
        }
    }
}
