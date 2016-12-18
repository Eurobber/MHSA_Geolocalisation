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

    // Check passwords for commands

    public static boolean isLocalPassword(Context act, String pwd){
        if(pwd.equals(act.getSharedPreferences(SET, MODE_PRIVATE).getString(SETPASS, null))) return true;
        return false;
    }

    public static boolean isHashedLocalPassword(Context act, String pwd){
        try {
            if(pwd.equals(Sha1Hasher.sha1smsMessage(act.getSharedPreferences(SET, MODE_PRIVATE).getString(SETPASS, null)))) return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
