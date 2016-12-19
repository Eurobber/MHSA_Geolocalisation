package com.ece.iceageophone.main.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.MODE_PRIVATE;


public class PreferenceChecker {
    public static final String SET = "SETTINGS";
    public static final String SETPASS = "PWD_SETTINGS";
    public static final String SETTGT = "TARGET_NUMBER";
    public static final String SETTGTPASS = "TARGET_PASSWORD";

    public static final String CACHELAT = "TARGET_LATITUDE";
    public static final String CACHELONG = "TARGET_LONGITUDE";
    public static final String CACHEALT = "TARGET_ALTITUDE";

    public static final String INSTRUCTIONS = "LOCAL_INSTRUCTIONS";

    public static final String TAG = "PreferenceChecker";

    public static SharedPreferences getPreferences(Context a){
        return a.getSharedPreferences(SET, MODE_PRIVATE);
    }

    // Set SharedPreferences

    public static void setPassword(Context a, String pwd){
        getPreferences(a)
                .edit()
                .putString(SETPASS, pwd)
                .apply();
        if(getPreferences(a).contains(SETPASS)){
            Toast.makeText(a, "Successfully stored new local password !", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Successfully stored new local password !");
        }
    }

    public static void setRemoteNumber(Context a, String num){
        getPreferences(a)
                .edit()
                .putString(SETTGT, num)
                .apply();
        if(getPreferences(a).contains(SETTGT)){
            Toast.makeText(a, "Successfully stored new remote number !", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Successfully stored new remote number !");
        }
    }

    public static void setRemoteLat(Context a, String num){
        getPreferences(a)
                .edit()
                .putString(CACHELAT, num)
                .apply();
        if(getPreferences(a).contains(CACHELAT)){
            Toast.makeText(a, "Successfully stored remote latitude !", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Successfully stored remote latitude !");
        }
    }

    public static void setRemoteLong(Context a, String num){
        getPreferences(a)
                .edit()
                .putString(CACHELONG, num)
                .apply();
        if(getPreferences(a).contains(CACHELONG)){
            Toast.makeText(a, "Successfully stored remote longitude !", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Successfully stored remote longitude !");
        }
    }

    public static void setRemoteAlt(Context a, String num){
        getPreferences(a)
                .edit()
                .putString(CACHEALT, num)
                .apply();
        if(getPreferences(a).contains(CACHEALT)){
            Toast.makeText(a, "Successfully stored remote altitude !", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Successfully stored remote altitude !");
        }
    }


    public static void setRemotePassword(Context a, String numPwd){
        getPreferences(a)
                .edit()
                .putString(SETTGTPASS, numPwd)
                .apply();
        if(getPreferences(a).contains(SETTGTPASS)){
            Toast.makeText(a, "Successfully stored new remote password !", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Successfully stored new remote password !");
        }
    }

    public static void setRemoteInstructions(Context a, String instructions){
        getPreferences(a)
                .edit()
                .putString(INSTRUCTIONS, instructions)
                .apply();
        if(getPreferences(a).contains(INSTRUCTIONS)){
            Toast.makeText(a, "Set new instructions !", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Set new instructions !");
        }
    }

    // Check passwords for commands

    public static boolean isLocalPassword(Context act, String pwd){
        return pwd.equals(act.getSharedPreferences(SET, MODE_PRIVATE).getString(SETPASS, null));
    }

    public static boolean isHashedLocalPassword(Context act, String pwd){
        String localBuffer, remoteBuffer, hashedRemoteBuffer;
        try {
            localBuffer = Sha1Hasher.sha1smsMessage(act.getSharedPreferences(SET, MODE_PRIVATE).getString(SETPASS, null));
            remoteBuffer = Sha1Hasher.sha1smsMessage(act.getSharedPreferences(SET, MODE_PRIVATE).getString(SETTGTPASS, null));
            hashedRemoteBuffer = Sha1Hasher.sha1smsMessage(remoteBuffer);
            if(pwd.equals(localBuffer) || pwd.equals(remoteBuffer) || pwd.equals(hashedRemoteBuffer)){
                return true;
            }

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
