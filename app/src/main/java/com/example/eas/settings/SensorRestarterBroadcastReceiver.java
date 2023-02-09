package com.example.eas.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

/**
 * Created by fabio on 24/01/2016.
 */
public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent serviceIntent = new Intent(context, LocationMonitoringService.class);
            ContextCompat.startForegroundService(context, serviceIntent);

        } else {

            context.startService(new Intent(context, LocationMonitoringService.class));
        }


        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");

    }

}
