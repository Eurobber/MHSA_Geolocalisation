package com.ece.iceageophone.main.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.WindowManager;

import com.ece.iceageophone.main.activity.alertdialog.DisabledGPSAlertDialogActivity;

public class PositionLocater {

    private static final String TAG = "PositionLocater";

    /**
     * Get GPS location
     * @param context
     * @param locationManager
     * @return
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    public static Location requestGPSLocation(Context context, LocationManager locationManager) throws SecurityException, IllegalArgumentException {
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Is GPS is not enabled, request user to change settings
        if (!gpsEnabled) {
            showSettingsNetWorkAlert(context);
        }
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    /**
     * Prompt the user to enable GPS on settings
     * @param context
     */
    public static void showSettingsNetWorkAlert(Context context){
        Intent intent = new Intent(context, DisabledGPSAlertDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
