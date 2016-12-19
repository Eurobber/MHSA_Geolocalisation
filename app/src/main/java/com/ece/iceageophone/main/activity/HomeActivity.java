package com.ece.iceageophone.main.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ece.iceageophone.main.R;
import com.ece.iceageophone.main.util.PreferenceChecker;

import static com.ece.iceageophone.main.util.PreferenceChecker.CACHEALT;
import static com.ece.iceageophone.main.util.PreferenceChecker.CACHELAT;
import static com.ece.iceageophone.main.util.PreferenceChecker.CACHELONG;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETPASS;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETTGT;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private TextView TVResultPhone;
    private TextView TVResultLat;
    private TextView TVResultLong;
    private TextView TVResultAlt;


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
                    Manifest.permission.VIBRATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CLEAR_APP_CACHE},
                1);

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
        Log.d(TAG, "Starting application");

        TVResultPhone = (TextView) findViewById(R.id.TVResultphone);
        TVResultLat = (TextView) findViewById(R.id.TVResultRMT);
        TVResultLong = (TextView) findViewById(R.id.TVResultLong);
        TVResultAlt = (TextView) findViewById(R.id.TVResultMine);

        if(PreferenceChecker.getPreferences(this).contains(SETTGT))
        {
            TVResultPhone.setText(PreferenceChecker.getPreferences(this).getString(SETTGT, null));
        }else TVResultPhone.setText("Not set (@Settings)");

        if(PreferenceChecker.getPreferences(this).contains(CACHELAT))
        {
            TVResultLat.setText(PreferenceChecker.getPreferences(this).getString(CACHELAT, null));
        }else TVResultLat.setText("Not found yet");

        if(PreferenceChecker.getPreferences(this).contains(CACHELONG))
        {
            TVResultLong.setText(PreferenceChecker.getPreferences(this).getString(CACHELONG, null));
        }else TVResultLong.setText("Not found yet");

        if(PreferenceChecker.getPreferences(this).contains(CACHEALT))
        {
            TVResultAlt.setText(PreferenceChecker.getPreferences(this).getString(CACHEALT, null));
        }else TVResultAlt.setText("Not found yet");

    }

}