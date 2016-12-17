package com.ece.iceageophone.main.util;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class PositionLocater {

    private static final String TAG = "PositionLocater";

    public static Location getGPSLocation(LocationManager locationManager) throws SecurityException, IllegalArgumentException {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

}
