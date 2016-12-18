package com.ece.iceageophone.main.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.ece.iceageophone.main.R;
import com.ece.iceageophone.main.util.Command;
import com.ece.iceageophone.main.util.CommandSender;
import static com.ece.iceageophone.main.util.PasswordChecker.*;

public class HomeActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private EditText phoneNumberEditText;
    private EditText smsBodyEditText;

    private Button sendSmsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActivityCompat.requestPermissions(
                this,
                new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.VIBRATE},
                1);
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


    /*
    Implémentation des liens de la barre de navigation
    */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        Intent intent = null;

        if (id == R.id.home){
            intent = new Intent(getApplicationContext(), HomeActivity.class);
        } else if(id == R.id.contact) {
            intent = new Intent(getApplicationContext(), ContactActivity.class);
        } else if(id == R.id.locate) {
            intent = new Intent(getApplicationContext(), LocateActivity.class);
        } else if(id == R.id.secure) {
            intent = new Intent(getApplicationContext(), SecureActivity.class);
        } else if(id == R.id.scan) {
            intent = new Intent(getApplicationContext(), ScanActivity.class);
        } else if(id == R.id.history) {
            intent = new Intent(getApplicationContext(), HistoryActivity.class);
        } else if(id == R.id.settings) {
            intent = new Intent(getApplicationContext(), SettingsActivity.class);
        }

        if(intent!=null){

            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }

    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Start application");

        phoneNumberEditText = (EditText) findViewById(R.id.phone_number_edit_text);
        smsBodyEditText = (EditText) findViewById(R.id.sms_body_edit_text);

        sendSmsButton = (Button) findViewById(R.id.send_sms_button);
        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommandSender.sendCommand(Command.GET_LOCATION, phoneNumberEditText.getText().toString(), smsBodyEditText.getText().toString());
            }
        });
    }

}