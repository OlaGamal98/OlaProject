package com.example.cs2.activity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.cs2.R;

import java.util.List;
/*
TS 27.007 8.5
Defined values
<rsrp>:
0 -113 dBm or less
1 -111 dBm
2...30 -109... -53 dBm
31 -51 dBm or greater
99 not known or not detectable
 */

/*
The parts[] array will then contain these elements:

part[0] = "Signalstrength:"  _ignore this, it's just the title_
parts[1] = GsmSignalStrength
parts[2] = GsmBitErrorRate
parts[3] = CdmaDbm
parts[4] = CdmaEcio
parts[5] = EvdoDbm
parts[6] = EvdoEcio
parts[7] = EvdoSnr
parts[8] = LteSignalStrength
parts[9] = LteRsrp
parts[10] = LteRsrq
parts[11] = LteRssnr
parts[12] = LteCqi
parts[13] = gsm|lte
parts[14] = _not reall sure what this number is_
 */

public class measurements  extends Activity {

    // How do I release the Listener when app closes???????
    SignalStrengthListener signalStrengthListener;

    TextView signalStrengthTextView;



    List<CellInfo> cellInfoList;
    int RSRP;
    int RSRQ;
    int SNR;
     String RSRP_parts;
     String RSRQ_parts;
     String SNR_parts;
     String CDMA_Dbm;
     String CDMA_Ecio;
     String Evdo_Dbm;
     String Evdo_Ecio;
     String Evdo_Snr;
     String LTE_sig_strength;
     String LTE_cqi;

    TelephonyManager tm;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setup content stuff
        this.setContentView(R.layout.measurements);
        //                  to display the percentage
        signalStrengthTextView = findViewById(R.id.number);



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
                    RSRP = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp();

                    // Gets the RSRQ
                    RSRQ = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrq();

                    // Gets the SNR
                    SNR = ((CellInfoLte) cellInfo).getCellSignalStrength().getRssnr();

                }

            }
        } catch (Exception e) {
            Log.d("SignalStrength", "++++++++++++++++++++++ null array spot 2: " + e);
        }

        // Find the View that shows the RSRP button
        Button RSRP = findViewById(R.id.RSRP_button);
        // Find the progress bar
        final ProgressBar progress_bar = findViewById(R.id.circularProgressbar);


        // Set a click listener on that View
        RSRP.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the RSRP View is clicked on.
            @Override
            public void onClick(View view) {
                signalStrengthTextView.setText(RSRP_parts);
                // progress bar display
                float max= -75;
                float min= -105;
                float value = Float.parseFloat(RSRP_parts);
                final float pStatus = ((value - min) / (max - min ))*100;

                final Handler handler = new Handler();
                final TextView tv;


                if (Float.parseFloat(RSRP_parts) >= -105 && Float.parseFloat(RSRP_parts) <= -75){
                Resources res = getResources();
                Drawable drawable = res.getDrawable(R.drawable.circular);
                final ProgressBar mProgress = findViewById(R.id.circularProgressbar);
                mProgress.setProgress(0);   // Main Progress
                mProgress.setSecondaryProgress(100); // Secondary Progress
                mProgress.setMax(100);// Maximum Progress
                mProgress.setProgressDrawable(drawable);

      /*  ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
        animation.setDuration(50000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();*/


//                to find the textview to display the quality ( good , bad , .. )
                tv = findViewById(R.id.quality);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
//                while (pStatus < 100) {
//                    pStatus += 1;

                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                mProgress.setProgress((int) pStatus);
                                if (Float.parseFloat(RSRP_parts) >= -80 ) // excellent signal
                                {
                                    tv.setText("Excellent");
                                }
                                else if (Float.parseFloat(RSRP_parts) >= -90 && Float.parseFloat(RSRP_parts) < -80 ) // good
                                {
                                    tv.setText("Good");
                                }
                                else if (Float.parseFloat(RSRP_parts) >= -100 && Float.parseFloat(RSRP_parts) < -90 ) // poor
                                {
                                    tv.setText("Fair");
                                }
                                else
                                {
                                    tv.setText("Poor");
                                }

                            }
                        });
                        try {
                            // Sleep for 200 milliseconds.
                            // Just to display the progress slowly
                            Thread.sleep(8); //thread will take approx 1.5 seconds to finish
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                }
                    }
                }).start();

            }}
        });
        Button RSRQ = findViewById(R.id.RSRQ_button);

        // Set a click listener on that View
        RSRQ.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the RSRQ View is clicked on.
            @Override
            public void onClick(View view) {
                signalStrengthTextView.setText(RSRQ_parts);
                // progress bar display
                float max= -5;
                float min= -25;
                float value = Float.parseFloat(RSRQ_parts);
                final float pStatus = ((value - min) / (max - min ))*100;

                final Handler handler = new Handler();
                final TextView tv;


                if (Float.parseFloat(RSRQ_parts) >= -25 && Float.parseFloat(RSRQ_parts) <= -5){
                    Resources res = getResources();
                    Drawable drawable = res.getDrawable(R.drawable.circular);
                    final ProgressBar mProgress = findViewById(R.id.circularProgressbar);
                    mProgress.setProgress(0);   // Main Progress
                    mProgress.setSecondaryProgress(100); // Secondary Progress
                    mProgress.setMax(100);// Maximum Progress
                    mProgress.setProgressDrawable(drawable);

      /*  ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
        animation.setDuration(50000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();*/


//                to find the textview to display the quality ( good , bad , .. )
                    tv = findViewById(R.id.quality);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
//                while (pStatus < 100) {
//                    pStatus += 1;

                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    mProgress.setProgress((int) pStatus);
                                    if (Float.parseFloat(RSRQ_parts) >= -10 ) // excellent signal
                                    {
                                        tv.setText("Excellent");
                                    }
                                    else if (Float.parseFloat(RSRQ_parts) >= -15 && Float.parseFloat(RSRQ_parts) < -10 ) // good
                                    {
                                        tv.setText("Good");
                                    }
                                    else if (Float.parseFloat(RSRQ_parts) >= -10 && Float.parseFloat(RSRQ_parts) < -20 ) // poor
                                    {
                                        tv.setText("Fair");
                                    }
                                    else
                                    {
                                        tv.setText("Poor");
                                    }

                                }
                            });
                            try {
                                // Sleep for 200 milliseconds.
                                // Just to display the progress slowly
                                Thread.sleep(8); //thread will take approx 1.5 seconds to finish
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                }
                        }
                    }).start();

                }
            }
        });


        Button SNIR = findViewById(R.id.SNIR_button);

        // Set a click listener on that View
        SNIR.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the SNIR View is clicked on.
            @Override
            public void onClick(View view) {
                signalStrengthTextView.setText(SNR_parts);
                // progress bar display
                float max= 25;
                float min= -5;
                float value = Float.parseFloat(SNR_parts);
                final float pStatus = ((value - min) / (max - min ))*100;

                final Handler handler = new Handler();
                final TextView tv;


                if (Float.parseFloat(SNR_parts) >= -5 && Float.parseFloat(SNR_parts) <= 25){
                    Resources res = getResources();
                    Drawable drawable = res.getDrawable(R.drawable.circular);
                    final ProgressBar mProgress = findViewById(R.id.circularProgressbar);
                    mProgress.setProgress(0);   // Main Progress
                    mProgress.setSecondaryProgress(100); // Secondary Progress
                    mProgress.setMax(100);// Maximum Progress
                    mProgress.setProgressDrawable(drawable);

      /*  ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
        animation.setDuration(50000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();*/


//                to find the textview to display the quality ( good , bad , .. )
                    tv = findViewById(R.id.quality);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
//                while (pStatus < 100) {
//                    pStatus += 1;

                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    mProgress.setProgress((int) pStatus);
                                    if (Float.parseFloat(SNR_parts) >= 20 ) // excellent signal
                                    {
                                        tv.setText("Excellent");
                                    }
                                    else if (Float.parseFloat(SNR_parts) >= 13 && Float.parseFloat(SNR_parts) < 20 ) // good
                                    {
                                        tv.setText("Good");
                                    }
                                    else if (Float.parseFloat(SNR_parts) >= 0 && Float.parseFloat(SNR_parts) < 13 ) // poor
                                    {
                                        tv.setText("Fair");
                                    }
                                    else
                                    {
                                        tv.setText("Poor");
                                    }

                                }
                            });
                            try {
                                // Sleep for 200 milliseconds.
                                // Just to display the progress slowly
                                Thread.sleep(8); //thread will take approx 1.5 seconds to finish
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                }
                        }
                    }).start();

                }
            }
        });

        // Find the View that shows the start button
        Button linear = findViewById(R.id.linear);

        // Set a click listener on that View
       linear.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the Start View is clicked on.
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(measurements.this, com.example.cs2.activity.linear.class);
                startActivity(startIntent);
            }
        });

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
        @RequiresApi(api = Build.VERSION_CODES.O)
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
                        RSRP = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp();

                        // Gets the RSRQ
                        RSRQ = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrq();

                        // Gets the SNR
                        SNR = ((CellInfoLte) cellInfo).getCellSignalStrength().getRssnr();



                    }
                }
            } catch (Exception e) {
                Log.d("SignalStrength", "+++++++++++++++++++++++++++++++ null array spot 3: " + e);
            }
            CDMA_Dbm =String.valueOf(parts[3]);
            CDMA_Ecio=String.valueOf(parts[4]);
            Evdo_Dbm=String.valueOf(parts[5]);
            Evdo_Ecio=String.valueOf(parts[6]);
            Evdo_Snr=String.valueOf(parts[7]);
            LTE_sig_strength =String.valueOf(parts[8]);
            RSRP_parts =String.valueOf(parts[9]);
            RSRQ_parts =String.valueOf(parts[10]);
            SNR_parts =String.valueOf(parts[11]);
            LTE_cqi =String.valueOf(parts[12]);

            super.onSignalStrengthsChanged(signalStrength);

            //++++++++++++++++++++++++++++++++++++

        }
    }



}














