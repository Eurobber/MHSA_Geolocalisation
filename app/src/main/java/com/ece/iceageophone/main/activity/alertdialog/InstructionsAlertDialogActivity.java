package com.ece.iceageophone.main.activity.alertdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.ece.iceageophone.main.activity.SettingsActivity;
import com.ece.iceageophone.main.util.PreferenceChecker;

import static com.ece.iceageophone.main.util.PreferenceChecker.INSTRUCTIONS;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETPASS;

public class InstructionsAlertDialogActivity extends Activity {

    private static final String TAG = "InstructionsAlertDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Displaying instructions");

        // Prompt user to enable GPS on Settings
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(InstructionsAlertDialogActivity.this);

        alertDialog.setTitle("IMPORTANT : PLEASE READ");
        alertDialog.setMessage(PreferenceChecker.getPreferences(this).getString(INSTRUCTIONS, null));

        // On pressing cancel button
        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // Showing alert message
        alertDialog.show();
    }

}
