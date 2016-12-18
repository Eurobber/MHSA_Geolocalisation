package com.ece.iceageophone.main.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ece.iceageophone.main.R;
import com.ece.iceageophone.main.util.PreferenceChecker;

import java.util.Set;

import static android.text.TextUtils.isEmpty;
import static com.ece.iceageophone.main.R.id.ETNew;
import static com.ece.iceageophone.main.R.id.ETOld;
import static com.ece.iceageophone.main.R.id.ETPhone;
import static com.ece.iceageophone.main.R.id.ETPhonePwd;
import static com.ece.iceageophone.main.R.id.saveSettingsBtn;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETPASS;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETTGT;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETTGTPASS;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Settings Activity";

    private String[] arraySpinner;

    // Widgets
    private EditText number;
    private EditText remotePwd;
    private EditText newPwd;
    private EditText localPwd;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.arraySpinner = new String[]{
                "On", "Off"
        };
        Spinner s = (Spinner) findViewById(R.id.spinnerApp);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

        // If password has never been set or is not in Shared Preferences file
        if (!PreferenceChecker.getPreferences(this).contains(SETPASS)) {
            // Display a small text message to prompt the user for a new password
            Toast.makeText(this, "You must enter a new password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        useInputs();
    }

    /*
        Implémentation des liens de la barre de navigation
    */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        Intent intent = null;

        if (id == R.id.home) {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
        } else if (id == R.id.contact) {
            intent = new Intent(getApplicationContext(), ContactActivity.class);
        } else if (id == R.id.locate) {
            intent = new Intent(getApplicationContext(), LocateActivity.class);
        } else if (id == R.id.secure) {
            intent = new Intent(getApplicationContext(), SecureActivity.class);
        } else if (id == R.id.scan) {
            intent = new Intent(getApplicationContext(), ScanActivity.class);
        } else if (id == R.id.history) {
            intent = new Intent(getApplicationContext(), HistoryActivity.class);
        } else if (id == R.id.settings) {
            intent = new Intent(getApplicationContext(), SettingsActivity.class);
        }

        if (intent != null) {

            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }

    private void useInputs() {
        number = (EditText) findViewById(ETPhone);
        remotePwd = (EditText) findViewById(ETPhonePwd);
        newPwd = (EditText) findViewById(ETNew);
        localPwd = (EditText) findViewById(ETOld);
        submit = (Button) findViewById(saveSettingsBtn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If password has never been set or is not in Shared Preferences file
                if(!PreferenceChecker.getPreferences(SettingsActivity.this).contains(SETPASS))
                {
                    // Set new password if input
                    if(!isEmpty(newPwd.getText().toString()))
                    {
                        if(newPwd.getText().toString().equals(localPwd.getText().toString())){
                            PreferenceChecker.setPassword(SettingsActivity.this, newPwd.getText().toString());
                        }
                        else{
                            Toast.makeText(SettingsActivity.this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
                        }
                        newPwd.getText().clear();
                        localPwd.getText().clear();
                    }
                    // Ask for it if no input
                    else{
                        // Display a small text message to prompt the user for a new password
                        Toast.makeText(SettingsActivity.this, "You must enter a new password", Toast.LENGTH_SHORT).show();
                    }
                }

                // If device is protected with a local password
                else
                {
                    // Check that the inputs are well-formatted and that the local password is given before submitting
                    if(!isEmpty(number.getText().toString()))
                    {
                        newPwd.getText().clear();
                        // Check number format
                        if(!isNumber()) Toast.makeText(SettingsActivity.this, "Phone number has to be a ... number !", Toast.LENGTH_SHORT).show();
                        else{
                            // Check presence of remote password
                            if(isEmpty(remotePwd.getText().toString())){
                                Toast.makeText(SettingsActivity.this, "Please specify remote password.", Toast.LENGTH_SHORT).show();
                            }
                            else if(isEmpty(localPwd.getText().toString())) Toast.makeText(SettingsActivity.this, "Couldn't save, please specify local password.", Toast.LENGTH_SHORT).show();
                                // If both remote number and remote password, save them
                            else if(PreferenceChecker.isLocalPassword(SettingsActivity.this, localPwd.getText().toString())){
                                PreferenceChecker.setRemoteNumber(SettingsActivity.this, number.getText().toString());
                                PreferenceChecker.setRemotePassword(SettingsActivity.this, remotePwd.getText().toString());
                                number.getText().clear();
                                remotePwd.getText().clear();
                                localPwd.getText().clear();
                            }
                        }
                    }

                    else{
                        if(isEmpty(localPwd.getText().toString()) || isEmpty(newPwd.getText().toString())){
                            Toast.makeText(SettingsActivity.this, "Please enter a new password along with the old one.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Please enter a new password along with the old one.");
                        }

                        if(!isEmpty(newPwd.getText().toString()) && !isEmpty(localPwd.getText().toString())){
                            if(PreferenceChecker.isLocalPassword(SettingsActivity.this, localPwd.getText().toString())){
                                PreferenceChecker.setPassword(SettingsActivity.this, newPwd.getText().toString());
                                newPwd.getText().clear();
                                localPwd.getText().clear();
                                Toast.makeText(SettingsActivity.this, "Password successfully changed", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Password successfully changed to "+PreferenceChecker.getPreferences(SettingsActivity.this).getString(SETPASS, null));
                            }
                            else{
                                Toast.makeText(SettingsActivity.this, "Wrong guess mate ! Luckier next time ;)", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Wrong password ! ");
                            }
                        }

                    }

                }
            }
        });
    }

    private boolean isNumber() {
        if (number.getText().toString().matches("\\d+"))
            return true;
        return false;
    }
}