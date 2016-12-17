package com.ece.iceageophone.main.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommandSender {

    private static final String TAG = "CommandSender";

    public static final String APPLICATION_NAME = "Ice-aGeoPhone";
    public static final String SEPARATOR = " // ";

    /**
     * Send a command
     * @param command
     * @param targetPhone
     * @param targetPassword
     */
    public static void sendCommand(Command command, String targetPhone, String targetPassword) {
        try {
            String body = formatMessageBody(command, targetPassword);
            SmsSender.sendSms(targetPhone, body);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | IllegalArgumentException e) {
            Log.e(TAG, "Can not send command " + command + " to " + targetPhone, e);
        }
    }

    /**
     * Creates an SMS body
     * @param command
     * @param password
     * @return
     */
    private static String formatMessageBody(Command command, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        StringBuilder finalMessage = new StringBuilder("");

        try {
            finalMessage.append(APPLICATION_NAME)
                        .append(SEPARATOR)
                        .append(sha1smsMessage(password))
                        .append(SEPARATOR)
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
