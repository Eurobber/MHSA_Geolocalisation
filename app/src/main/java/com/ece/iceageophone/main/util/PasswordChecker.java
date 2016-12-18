package com.ece.iceageophone.main.util;


import android.app.Activity;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PasswordChecker {
    public static final String SET = "SETTINGS";
    public static final String SETPASS = "PWD_SETTINGS";
    public static final String SETTGT = "TARGET_NUMBER";
    public static final String SETTGTPASS = "TARGET_PASSWORD";
    public static final String SETMODE = "MODE_SETTINGS";

    public static void askForPwd(Activity act){

    }

    public static SharedPreferences getPreferences(Activity a){
        return a.getBaseContext().getSharedPreferences(SET, MODE_PRIVATE);
    }

}
