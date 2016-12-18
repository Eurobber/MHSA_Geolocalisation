package com.ece.iceageophone.main.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ece.iceageophone.main.R;
import com.ece.iceageophone.main.util.SmsSender;

import static android.R.attr.name;
import static android.R.attr.path;
import static android.text.TextUtils.isEmpty;
import static com.ece.iceageophone.main.R.id.ETNew;
import static com.ece.iceageophone.main.R.id.ETOld;
import static com.ece.iceageophone.main.R.id.ETPhone;
import static com.ece.iceageophone.main.R.id.ETPhonePwd;
import static com.ece.iceageophone.main.R.id.phone_number_edit_text;
import static com.ece.iceageophone.main.R.id.saveSettingsBtn;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String SET = "SETTINGS";
    public static final String SETPASS = "PWD_SETTINGS";
    public static final String SETTGT = "TARGET_NUMBER";
    public static final String SETTGTPASS = "TARGET_PASSWORD";
    public static final String SETMODE = "MODE_SETTINGS";
    private static final String TAG = "Settings Activity";
    private SharedPreferences sharedPreferences;

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

        // Ask for a new password if no previous password
        sharedPreferences = getBaseContext().getSharedPreferences(SET, MODE_PRIVATE);

        if (!sharedPreferences.contains(SETPASS)) {
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
                // Set new phone number and new remote password
                if(isNumber() && !isEmpty(remotePwd.getText().toString())){
                    sharedPreferences
                            .edit()
                            .putString(SETTGT, number.getText().toString())
                            .putString(SETTGTPASS, remotePwd.getText().toString())
                            .apply();
                    if(sharedPreferences.contains(SETTGT)&&sharedPreferences.contains(SETTGTPASS)){
                        Toast.makeText(SettingsActivity.this, "Successfully stored new remote number and new remote password !", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Successfully stored new remote number and new remote password !");
                    }
                }
                // If password has never been set or is not in Shared Preferences file
                if (!sharedPreferences.contains(SETPASS)) {
                    if(!isEmpty(newPwd.getText().toString())){
                        sharedPreferences
                                .edit()
                                .putString(SETPASS, newPwd.getText().toString())
                                .apply();
                        if(sharedPreferences.contains(SETPASS)){
                            Toast.makeText(SettingsActivity.this, "Successfully stored new local password !", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Successfully stored new local password !");
                        }
                    }
                }
                // If password is changed, we have to check local password to confirm
                else if(!isEmpty(newPwd.getText().toString()) && !isEmpty(localPwd.getText().toString())){
                    if(sharedPreferences.getString(SETPASS, null)==localPwd.getText().toString()){
                        sharedPreferences
                                .edit()
                                .putString(SETPASS, newPwd.getText().toString())
                                .apply();
                    }
                    else{
                        Toast.makeText(SettingsActivity.this, "Wrong guess mate ! Luckier next time ;)", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Wrong password !");
                    }
                }
            }
        });
    }

    private boolean isNumber() {
        if (!isEmpty(number.getText().toString()) && number.getText().toString().matches("\\d+"))
            return true;
        return false;
    }

    private void reload(){
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}