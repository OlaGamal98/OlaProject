package com.example.cs2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cs2.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find the View that shows the start button
        Button start = findViewById(R.id.start_button);

        // Set a click listener on that View
        start.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the Start View is clicked on.
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(MainActivity.this, measurements.class);
                startActivity(startIntent);
                // Toast.makeText(MainActivity.this,"f1",Toast.LENGTH_SHORT).show();
            }
        });

    }


    //@RequiresApi(api = Build.VERSION_CODES.M)
    //private void workManager() {

        // Constraints for our work
        //Constraints constraints = new Constraints.Builder()
           //     .setRequiresDeviceIdle(true)
           //     .setRequiredNetworkType(NetworkType.CONNECTED)
            //    .setRequiresBatteryNotLow(true)
             //   .build();


       // PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MyWork.class, 30, TimeUnit.MILLISECONDS)
               // .setConstraints(constraints)
           //     .build();

        // Send our work to be scheduled.
      //  WorkManager.getInstance(this).enqueue(periodicWorkRequest);
        //Toast.makeText(MainActivity.this,"f2",Toast.LENGTH_SHORT).show();

    //}



}
