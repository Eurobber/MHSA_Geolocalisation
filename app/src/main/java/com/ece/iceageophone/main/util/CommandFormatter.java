package com.ece.iceageophone.main.util;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.ece.iceageophone.main.activity.HomeActivity;
import com.ece.iceageophone.main.exception.MessageFormatException;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.MODE_PRIVATE;
import static com.ece.iceageophone.main.util.PreferenceChecker.SET;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETPASS;

public class CommandFormatter {

    private static final String TAG = "CommandFormatter";

    public static final String APPLICATION_NAME = "Ice-aGeoPhone";
    public static final String SEPARATOR = " // ";
    public static final String COORDINATES_SEPARATOR = ";";

    /**
     * Creates a simple SMS command body : <application_name> // <hashed_password> // <command>
     * @param command
     * @param password
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public static String formatMessageBody(Command command, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        StringBuilder finalMessage = new StringBuilder("");

        try {
            finalMessage.append(APPLICATION_NAME)
                    .append(SEPARATOR)
                    .append(Sha1Hasher.sha1smsMessage(password))
                    .append(SEPARATOR)
                    .append(command.getCommandName());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Log.e(TAG, "Can not encode target password properly", e);
            throw e;
        }

        return finalMessage.toString();
    }

    /**
     * Creates an SMS command body with one or more arguments :  <application_name> // <hashed_password> // <command> // <arg1> // <arg2> // ...
     * @param command
     * @param password
     * @param args
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public static String formatMessageBody(Command command, String password, Object... args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String startMessage = CommandFormatter.formatMessageBody(command, password);

        StringBuilder finalMessage = new StringBuilder(startMessage);

        for (Object arg : args) {
            if (arg != null) {
                String stringArg = null;
                if (arg instanceof Location) {
                    stringArg = formatLocationMessage((Location) arg);
                } else {
                    stringArg = arg.toString();
                }

                if (stringArg != null) {
                    finalMessage.append(SEPARATOR)
                            .append(stringArg);
                }
            }
        }

        return finalMessage.toString();
    }

    /**
     * Format Location to String
     * @param location
     * @return
     */
    private static String formatLocationMessage(Location location) {
        StringBuilder formattedLocation = new StringBuilder("");
        formattedLocation.append(location.getLatitude())
                .append(COORDINATES_SEPARATOR)
                .append(location.getLongitude())
                .append(COORDINATES_SEPARATOR)
                .append(location.getAltitude());

        return formattedLocation.toString();
    }

    /**
     *
     * @param message
     * @return
     */
    public static boolean isCommand(String message, Context context) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (message != null || message.isEmpty()) {
            // Splitting the message
            String[] splitMessage = message.split(CommandFormatter.SEPARATOR);
            // Checking message format
            if (splitMessage.length >= 3) {
                if (splitMessage[0].equals(CommandFormatter.APPLICATION_NAME)) {
                    if(PreferenceChecker.isHashedLocalPassword(context, splitMessage[1])) return true;
                }
                // Else the message is ignored
            }
            // Else the message is ignored
        }
        // Else the message is ignored
        return false;
    }

    /**
     * Parse String to Location
     * @return
     */
    public static Location parseLocationMessage(String message) throws MessageFormatException {
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
        } else {
            Log.d(TAG, "Message formatted incorrectly");
        }
        throw new MessageFormatException("Location formatted incorrectly");
    }

}
