package com.ece.iceageophone.main.listener;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ece.iceageophone.main.BuildConfig;
import com.ece.iceageophone.main.exception.MessageFormatException;
import com.ece.iceageophone.main.util.Command;
import com.ece.iceageophone.main.util.CommandSender;
import com.ece.iceageophone.main.util.PositionLocater;
import com.ece.iceageophone.main.util.SmsSender;

import static android.content.Context.LOCATION_SERVICE;

public class SmsListener extends BroadcastReceiver {

    private static final String TAG = "SmsListener";

    final SmsManager sms = SmsManager.getDefault();


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
            String[] splitMessage = message.split(CommandSender.SEPARATOR);
            // Checking message format
            if (splitMessage.length >= 3) {
                if (splitMessage[0].equals(CommandSender.APPLICATION_NAME)) {
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
        // Respond to request
        if (splitMessage.length == 3) {
            sendLocation(context, senderNum);
        }
        // Process the response
        else if (splitMessage.length == 4) {
            processLocation(splitMessage[3]);
        } else {
            Log.d(TAG, "Message formatted incorrectly");
        }
    }

    private void sendLocation(Context context, String senderNum) {
        LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(LOCATION_SERVICE);
        Location location = PositionLocater.getGPSLocation(locationManager);
        String body = formatLocationMessage(location);
        SmsSender.sendSms(senderNum, body);
    }

    private void processLocation(String locationMessage) {
        try {
            Location location = parseLocationMessage(locationMessage);
            Log.d(TAG, "Show location on map");
            // TODO Show location on map of Locate screen
        } catch (MessageFormatException e) {
            Log.d(TAG, "Can not process location", e);
        }
    }

    private static final String COORDINATES_SEPARATOR = ";";

    /**
     * Format Location to String
     * @param location
     * @return
     */
    private String formatLocationMessage(Location location) {
        StringBuilder formattedLocation = new StringBuilder("");
        formattedLocation.append(location.getLatitude())
                            .append(COORDINATES_SEPARATOR)
                            .append(location.getLongitude())
                            .append(COORDINATES_SEPARATOR)
                            .append(location.getAltitude());

        return formattedLocation.toString();
    }

    /**
     * Parse String to Location
     * @return
     */
    private Location parseLocationMessage(String message) throws MessageFormatException {
        String[] splittedMessage = message.split(COORDINATES_SEPARATOR);
        if (splittedMessage.length == 3) {
            Location location = new Location("");
            try {
                location.setLatitude(Double.parseDouble(splittedMessage[0]));
                location.setLongitude(Double.parseDouble(splittedMessage[1]));
                location.setAltitude(Double.parseDouble(splittedMessage[2]));
                return location;
            } catch (NumberFormatException e) {
                Log.d(TAG, "Location coordinates should be doubles", e);
            }
        }
        else {
            Log.d(TAG, "Message formatted incorrectly");
        }
        throw new MessageFormatException("Location formatted incorrectly");
    }

    private void vibrate() {

    }

}
