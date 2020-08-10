package com.example.cs2.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.cs2.Message;
import com.example.cs2.R;
import com.example.cs2.data.DataBaseAdapter;
import com.example.cs2.service.MyWork;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class linear extends Activity {
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    TextView locationTextView;
    ////Database
    DataBaseAdapter mDbHelper;

    SignalStrengthListener signalStrengthListener;

    TextView lte_sig;
    TextView rsrp;
    TextView rsrq;
    TextView sinr;
    TextView locationvv;


    List<CellInfo> cellInfoList;
    int cellSig, cellID, cellMcc, cellMnc, cellPci, cellTac = 0;

    int db_rsrp,db_rsrq,db_snir;
    int dbb_rsrp,dbb_rsrq,dbb_snir;
    String db_lat,db_long;
    String currentDateandTime ,db_loc;
    String dbb_currentDataandTime="hi";
    String ss="Hi";

    boolean rsrp_range,rsrq_range,snir_range;
    boolean timeFlag;
    // String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    TelephonyManager tm;

    Context context;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        context=this;

        ////Database
        mDbHelper= new DataBaseAdapter(this);


        //setup content stuff
        this.setContentView(R.layout.linear);



        locationTextView = findViewById(R.id.GPS_location);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        //////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////

        lte_sig = (TextView) findViewById(R.id.lte_sig);
        rsrp = (TextView) findViewById(R.id.rsrp);
        rsrq = (TextView) findViewById(R.id.rsrq);
        sinr = (TextView) findViewById(R.id.sinr);





        //start the signal strength listener
        signalStrengthListener = new SignalStrengthListener();

        ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(signalStrengthListener, SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        try {
            cellInfoList = tm.getAllCellInfo();
        } catch (Exception e) {
            Log.d("SignalStrength", "+++++++++++++++++++++++++++++++++++++++++ null array spot 1: " + e);

        }


        try {
            for (CellInfo cellInfo : cellInfoList) {
                if (cellInfo instanceof CellInfoLte) {
                    // cast to CellInfoLte and call all the CellInfoLte methods you need
                    // gets RSRP cell signal strength:
                    cellSig = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();

                    // Gets the LTE cell indentity: (returns 28-bit Cell Identity, Integer.MAX_VALUE if unknown)
                    cellID = ((CellInfoLte) cellInfo).getCellIdentity().getCi();

                    // Gets the LTE MCC: (returns 3-digit Mobile Country Code, 0..999, Integer.MAX_VALUE if unknown)
                    cellMcc = ((CellInfoLte) cellInfo).getCellIdentity().getMcc();

                    // Gets theLTE MNC: (returns 2 or 3-digit Mobile Network Code, 0..999, Integer.MAX_VALUE if unknown)
                    cellMnc = ((CellInfoLte) cellInfo).getCellIdentity().getMnc();

                    // Gets the LTE PCI: (returns Physical Cell Id 0..503, Integer.MAX_VALUE if unknown)
                    cellPci = ((CellInfoLte) cellInfo).getCellIdentity().getPci();

                    // Gets the LTE TAC: (returns 16-bit Tracking Area Code, Integer.MAX_VALUE if unknown)
                    cellTac = ((CellInfoLte) cellInfo).getCellIdentity().getTac();

                }

            }
        } catch (Exception e) {
            Log.d("SignalStrength", "++++++++++++++++++++++ null array spot 2: " + e);
        }
        // AddData();

        Button ll = (Button) findViewById(R.id.ll);

        // Set a click listener on that View
        ll.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the Start View is clicked on.
            @Override
            public void onClick(View view) {

                ss=mDbHelper.getData();

Log.e("Size",mDbHelper.getDataList().get(0).Time+"Found");
              //  ss=mDbHelper.getDataList().size();


                Message.message(context,ss);
            }
        });

Button btn=(Button) findViewById(R.id.button);
btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MyWork.class, 30, TimeUnit.MINUTES)
                // .setConstraints(constraints)
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(periodicWorkRequest);
    }
});



    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    db_loc=location.getLatitude()+","+location.getLongitude();
                                    db_long=String.valueOf(location.getLongitude());
                                    db_lat=String.valueOf(location.getLatitude());
                                    locationTextView.setText(db_loc);

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            db_loc=mLastLocation.getLatitude()+","+mLastLocation.getLongitude();
            locationTextView.setText(db_loc);
            db_long=String.valueOf(mLastLocation.getLongitude());
            db_lat=String.valueOf(mLastLocation.getLatitude());

        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        try{
            if(signalStrengthListener != null){tm.listen(signalStrengthListener, SignalStrengthListener.LISTEN_NONE);}
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void onDestroy() {
        super.onDestroy();

        try{
            if(signalStrengthListener != null){tm.listen(signalStrengthListener, SignalStrengthListener.LISTEN_NONE);}
        }catch(Exception e){
            e.printStackTrace();
        }
    }




    private class SignalStrengthListener extends PhoneStateListener {
        @SuppressLint("MissingPermission")
        @Override
        public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {

            //++++++++++++++++++++++++++++++++++

            ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(signalStrengthListener, SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS);

            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            String ltestr = signalStrength.toString();
            String[] parts = ltestr.split(" ");
            String cellSig2 = parts[9];

            try {
                cellInfoList = tm.getAllCellInfo();
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoLte) {
                        // cast to CellInfoLte and call all the CellInfoLte methods you need
                        // gets RSRP cell signal strength:
                        cellSig = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();

                        // Gets the LTE cell identity: (returns 28-bit Cell Identity, Integer.MAX_VALUE if unknown)
                        cellID = ((CellInfoLte) cellInfo).getCellIdentity().getCi();

                        // Gets the LTE MCC: (returns 3-digit Mobile Country Code, 0..999, Integer.MAX_VALUE if unknown)
                        cellMcc = ((CellInfoLte) cellInfo).getCellIdentity().getMcc();

                        // Gets theLTE MNC: (returns 2 or 3-digit Mobile Network Code, 0..999, Integer.MAX_VALUE if unknown)
                        cellMnc = ((CellInfoLte) cellInfo).getCellIdentity().getMnc();

                        // Gets the LTE PCI: (returns Physical Cell Id 0..503, Integer.MAX_VALUE if unknown)
                        cellPci = ((CellInfoLte) cellInfo).getCellIdentity().getPci();

                        // Gets the LTE TAC: (returns 16-bit Tracking Area Code, Integer.MAX_VALUE if unknown)
                        cellTac = ((CellInfoLte) cellInfo).getCellIdentity().getTac();

                    }
                }
            } catch (Exception e) {
                Log.d("SignalStrength", "+++++++++++++++++++++++++++++++ null array spot 3: " + e);
            }

//            cdma_dbm.setText(String.valueOf(parts[3]));
//            cdma_ecio.setText(String.valueOf(parts[4]));
//            evdo_dbm.setText(String.valueOf(parts[5]));
//            evdo_ecio.setText(String.valueOf(parts[6]));
//            evdo_snr.setText(String.valueOf(parts[7]));
            db_rsrp = Integer.parseInt(parts[9]);
            db_rsrq=Integer.parseInt(parts[10]);
            //db_rsrq=String.valueOf(parts[10]);
            db_snir=Integer.parseInt(parts[11]);

            currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            //db_currentDataandTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            lte_sig.setText(String.valueOf(parts[8]));
            rsrp.setText(String.valueOf(parts[9]));
            rsrq.setText(String.valueOf(parts[10]));
            sinr.setText(String.valueOf(parts[11]));
            //locationvv.setText(currentDateandTime);
            //locationTextView.setText(db_loc);


            AddData();

            //AddData(String.valueOf(parts[9]),String.valueOf(parts[10]),String.valueOf(parts[11]),mLastLocation.getLatitude()+","+mLastLocation.getLongitude());

            super.onSignalStrengthsChanged(signalStrength);

            //++++++++++++++++++++++++++++++++++++

        }
    }


    public void deleteAll(){
        String ss=mDbHelper.getData();
        Message.message(this,ss);
    }

    public  void AddData(){

        //Values range check
        if(db_snir>=0&&db_snir>=1000)
            snir_range=true;
        else
            snir_range=false;

        if(db_rsrp>=-140&&db_rsrp>=-40)
            rsrp_range=true;
        else
            rsrp_range=false;

        if(db_rsrq>=-20&&db_rsrq>=-3)
            rsrq_range=true;
        else
            rsrq_range=false;



        if(rsrp_range&&rsrq_range&&snir_range&&db_lat!=null&&db_long!=null) {

            // Condition to save data every second
            if (dbb_currentDataandTime.equals(currentDateandTime))
                timeFlag = false;
            else
                timeFlag = true;


            //dbb_rsrp!=db_rsrp||dbb_rsrq!=db_rsrq||dbb_snir!=db_snir||
            if (dbb_rsrp!=db_rsrp||dbb_rsrq!=db_rsrq||dbb_snir!=db_snir) {
                long isInserted = mDbHelper.insertData(db_long,db_lat,currentDateandTime, db_rsrq, db_rsrp, db_snir);

                dbb_rsrq = db_rsrq;
                dbb_rsrp = db_rsrp;
                dbb_snir = db_snir;

                dbb_currentDataandTime = currentDateandTime;

                //long isInserted = mDbHelper.insertData(db_long,db_lat,currentDateandTime,db_rsrq,db_rsrp,db_snir);
                if (isInserted < 0)
                 Message.message(this, "Data not inserted");
                // else
                //     Message.message(this, "rows: " + isInserted);
                //Message.message(this, db_long + ", " + db_lat + ", " + currentDateandTime + ", " + db_rsrq + ", " + db_rsrp + ", " + db_snir);
            }
        }

    }








}





