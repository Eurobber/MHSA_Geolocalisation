package com.ece.iceageophone.main.util;

import android.telephony.SmsManager;
import android.util.Log;

public class SmsSender {

    private static final String TAG = "SmsSender";

    /**
     * Sends a text message to a phone number
     * @param phoneNumber
     * @param body
     */
    public static void sendSms(String phoneNumber, String body) throws IllegalArgumentException {
        SmsManager smsManager = SmsManager.getDefault();

        try {
            smsManager.sendTextMessage(phoneNumber, null, body, null, null);
            Log.d(TAG, "Sent text message \"" + body + "\" to " + phoneNumber);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Can not send SMS " +  body + " to " + phoneNumber, e);
            throw e;

        }
    }

}
