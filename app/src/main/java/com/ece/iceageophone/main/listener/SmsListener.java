package com.ece.iceageophone.main.listener;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ece.iceageophone.main.BuildConfig;
import com.ece.iceageophone.main.activity.LocateActivity;
import com.ece.iceageophone.main.activity.alertdialog.DisabledGPSAlertDialogActivity;
import com.ece.iceageophone.main.exception.MessageFormatException;
import com.ece.iceageophone.main.util.Command;
import com.ece.iceageophone.main.util.CommandFormatter;
import com.ece.iceageophone.main.util.CommandSender;
import com.ece.iceageophone.main.util.SmsSender;

import static android.content.Context.LOCATION_SERVICE;

public class SmsListener extends BroadcastReceiver implements LocationListener {

    private static final String TAG = "SmsListener";

    private static final long MIN_TIME = 1000;
    private static final float MIN_DISTANCE = 1;
    private static final float MAX_ACCURACY = 150;

    final SmsManager sms = SmsManager.getDefault();

    private Context context = null;
    private LocationManager locationManager = null;

    private String senderNum = null;
    private String sentPassword = null;

    @TargetApi(BuildConfig.MIN_SDK_VERSION)
    public void onReceive(Context context, Intent intent) {
        // Used to share data between activities
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                // If SDK version > Marshmallow, get format
                String format = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    format = bundle.getString("format");
                }

                // Looping on PDUs because a large message can be broken into multiple PDUs
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage;
                    if (format == null) {
                        currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    } else {
                        currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                    }
                    // Get sender number and message
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.d(TAG, "Received message \"" + message + "\" from " + senderNum);

                    parseMessage(context, senderNum, message);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Can not handle received message", e);
        }
    }

    private void parseMessage(Context context, String senderNum, String message) {
        if (message != null || message.isEmpty()) {
            // Splitting the message
            String[] splitMessage = message.split(CommandFormatter.SEPARATOR);
            // Checking message format
            if (splitMessage.length >= 3) {
                if (splitMessage[0].equals(CommandFormatter.APPLICATION_NAME)) {
//                  TODO vérification du mot de passe if (splitMessage[1].equals(hash de notre mot de passe))

                    handleCommand(context, senderNum, splitMessage);
                }
                // Else the message is ignored
            }
            // Else the message is ignored
        }
        // Else the message is ignored
    }

    private void handleCommand(Context context, String senderNum, String[] splitMessage) {
        try {
            switch (Command.getCommand(splitMessage[2])) {
                case GET_LOCATION:
                    handleGetLocation(context, senderNum, splitMessage);
                    break;
                case VIBRATE:
                    vibrate();
                default:
                    break;
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "Missing argument", e);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "No match found for received command " + splitMessage[2]);
        }
    }

    private void handleGetLocation(Context context, String senderNum, String[] splitMessage) {
        // Request the location
        if (splitMessage.length == 3) {
            requestLocation(context, senderNum, splitMessage[1]);
        }
        // Process the response
        else if (splitMessage.length == 4) {
            processLocation(context, splitMessage[3]);
        } else {
            Log.d(TAG, "Message formatted incorrectly");
        }
    }

    private void requestLocation(Context context, String senderNum, String sentPassword) {
        this.context = context;
        this.senderNum = senderNum;
        this.sentPassword = sentPassword;
        locationManager = (LocationManager) context.getApplicationContext().getSystemService(LOCATION_SERVICE);
        // Is GPS is not enabled, request user to change settings
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            showSettingsNetWorkAlert(context);
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No permission to access location");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    /**
     * Prompt the user to enable GPS on settings
     * @param context
     */
    public static void showSettingsNetWorkAlert(Context context) {
        Intent intent = new Intent(context, DisabledGPSAlertDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void processLocation(Context context, String locationMessage) {
        try {
            Location location = CommandFormatter.parseLocationMessage(locationMessage);

            Log.d(TAG, "Show location on map");

            // Show location on locate screen's map
            Intent intent = new Intent(context, LocateActivity.class);
            intent.putExtra(LocateActivity.LOCATION, location);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (MessageFormatException e) {
            Log.d(TAG, "Can not process location", e);
        }
    }

    private void vibrate() {

    }

    @Override
    public void onLocationChanged(Location location) {
        // Process changed location only if accuracy < 50m
        if (location.hasAccuracy()) {
            Log.d(TAG, "Location accuracy " + location.getAccuracy());
            if (location.getAccuracy() < MAX_ACCURACY) {
                if (location != null && locationManager != null && context != null && senderNum != null && sentPassword != null) {
                    // Send location via SMS
                    CommandSender.sendCommand(Command.GET_LOCATION, senderNum, sentPassword, location);

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "No permission to access location");
                        return;
                    }

                    // Stop listening to location updates
                    this.locationManager.removeUpdates(this);
                    this.locationManager = null;
                    this.senderNum = null;
                    this.context = null;
                } else {
                    // Else no location found, retry
                    Log.d(TAG, "No GPS location found");
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
