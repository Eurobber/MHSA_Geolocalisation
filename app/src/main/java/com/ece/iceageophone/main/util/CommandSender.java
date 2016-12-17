package com.ece.iceageophone.main.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommandSender {

    private static final String TAG = "CommandSender";

    /**
     * Send a simple command
     * @param command
     * @param targetPhone
     * @param targetPassword
     */
    public static void sendCommand(Command command, String targetPhone, String targetPassword) {
        try {
            String body = CommandFormatter.formatMessageBody(command, targetPassword);
            SmsSender.sendSms(targetPhone, body);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | IllegalArgumentException e) {
            Log.e(TAG, "Can not send command " + command + " to " + targetPhone, e);
        }
    }

    /**
     * Send a command with one or more arguments
     * @param command
     * @param targetPhone
     * @param targetPassword
     */
    public static void sendCommand(Command command, String targetPhone, String targetPassword, Object... args) {
        try {
            String body = CommandFormatter.formatMessageBody(command, targetPassword, args);
            SmsSender.sendSms(targetPhone, body);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | IllegalArgumentException e) {
            Log.e(TAG, "Can not send command " + command + " to " + targetPhone, e);
        }
    }

}
