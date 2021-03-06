package com.ece.iceageophone.main.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ece.iceageophone.main.R;
import com.ece.iceageophone.main.data.LogRecord;
import com.ece.iceageophone.main.util.Command;
import com.ece.iceageophone.main.util.CommandSender;
import com.ece.iceageophone.main.util.PreferenceChecker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.ece.iceageophone.main.util.PreferenceChecker.SETPASS;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETTGT;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETTGTPASS;

public class LocateActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    public static final String LOCATION = "location";

    private Button requestLocationGpsButton = null;
//    private Button requestLocationGeomagneticButton = null;
    private GoogleMap googleMap;
    private Location targetLocation = null;

    private String remoteNumber;
    private String remotePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locate_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // If password has never been set or is not in Shared Preferences file
        if (!PreferenceChecker.getPreferences(this).contains(SETPASS)) {
            // Display a small text message to prompt the user for a new password
            Toast.makeText(this, "You must enter a new password", Toast.LENGTH_SHORT).show();
        }

        // Get location data
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            targetLocation = (Location) bundle.getParcelable(LOCATION);
        }

        // Get request button
        this.requestLocationGpsButton = (Button) findViewById(R.id.request_location_gps_button);
//        this.requestLocationGeomagneticButton = (Button) findViewById(R.id.request_location_geomagnetic_button);

        // If there is no location, a request can be sent
        if (targetLocation == null) {
            // Show request button and set listener
            this.requestLocationGpsButton.setVisibility(View.VISIBLE);
//            this.requestLocationGeomagneticButton.setVisibility(View.VISIBLE);

            requestLocationGpsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Send GPS location request
                    getRemote();
                    CommandSender.sendCommand(Command.GET_GPS_LOCATION, remoteNumber, remotePassword);
                    Toast.makeText(LocateActivity.this, "Locate request sent to "+remoteNumber, Toast.LENGTH_SHORT).show();
                    LogRecord.addRecord(getApplicationContext(), 5, null, remoteNumber);
                }
            });

//            requestLocationGeomagneticButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Send geomagnetic location request
//                    getRemote();
//                    CommandSender.sendCommand(Command.GET_GEOMAGNETIC_LOCATION, remoteNumber, remotePassword);
//                    Toast.makeText(LocateActivity.this, "Geolocate request sent to "+remoteNumber, Toast.LENGTH_SHORT).show();
//                    LogRecord.addRecord(getApplicationContext(), 6, null, remoteNumber);
//                }
//            });
        }
        // Else location is available, the request has already been made
        else {
            // Hide request button
            this.requestLocationGpsButton.setVisibility(View.GONE);
//            this.requestLocationGeomagneticButton.setVisibility(View.GONE);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map_fragment);
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a market on the target position and move the camera
        LatLng target = new LatLng(targetLocation.getLatitude(), targetLocation.getLongitude());
        this.googleMap.addMarker(new MarkerOptions().position(target).title("Target phone"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 16.0f));
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

    private void getRemote(){
        this.remoteNumber = PreferenceChecker.getPreferences(LocateActivity.this).getString(SETTGT, null);
        this.remotePassword = PreferenceChecker.getPreferences(LocateActivity.this).getString(SETTGTPASS, null);
    }

}