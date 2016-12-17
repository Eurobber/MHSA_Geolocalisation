package com.ece.iceageophone.main.activity.alertdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

public class DisabledGPSAlertDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prompt user to enable GPS on Settings
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisabledGPSAlertDialogActivity.this);

        alertDialog.setTitle("Request to enable GPS");
        alertDialog.setMessage("A location request has been made but GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button, show settings
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                DisabledGPSAlertDialogActivity.this.startActivity(intent);
                finish();
            }
        });

        // On pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        // Showing alert message
        alertDialog.show();
    }

}
