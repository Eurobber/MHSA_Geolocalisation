package com.ece.iceageophone.main.util;

import android.telephony.SmsManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommandSender {

    private static final String TAG = "CommandSender";

    private static final String opener = "Ice-aGeoPhone usingPWD:";
    private static final String separator = " // ";

    public static void sendCommand(Command command, String targetPhone, String targetPassword) {
        try {
            String body = createSmsBody(command, targetPassword);
            sendSms(targetPhone, body);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | IllegalArgumentException e) {
            Log.e(TAG, "Can not send command " + command + " to " + targetPhone, e);
        }
    }

    /**
     * Sends a text message to a phone number
     * @param phoneNumber
     * @param body
     */
    private static void sendSms(String phoneNumber, String body) throws IllegalArgumentException {
        SmsManager smsManager = SmsManager.getDefault();

        try {
            smsManager.sendTextMessage(phoneNumber, null, body, null, null);
            Log.d(TAG, "Sent text message \"" + body + "\" to " + phoneNumber);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Can not send SMS " +  body + " to " + phoneNumber, e);
            throw e;

        }
    }

    /**
     * Creates an SMS body
     * @param command
     * @param password
     * @return
     */
    private static String createSmsBody(Command command, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        StringBuilder finalMessage = new StringBuilder("");

        try {
            finalMessage.append(opener)
                        .append(sha1smsMessage(password))
                        .append(separator)
                        .append(command.getCommandName());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Log.e(TAG, "Can not encode target password properly", e);
            throw e;
        }

        return finalMessage.toString();
    }

    /**
     * Encrypt a String with SHA-1
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static String sha1smsMessage(String str)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = str.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        String hexCode = "";

        for (int i = 0; i < digest.length; i++) {
            hexCode +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring(1);
        }

        return hexCode;
    }

}
