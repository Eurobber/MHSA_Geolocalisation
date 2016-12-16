package com.ece.iceageophone.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.R.id.input;

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

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, 1);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        // On demande à l'utilisateur la permission d'envoyer des SMS
        Log.d(TAG, "Start application");

        phoneNumberEditText = (EditText) findViewById(R.id.phone_number_edit_text);
        smsBodyEditText = (EditText) findViewById(R.id.sms_body_edit_text);

        sendSmsButton = (Button) findViewById(R.id.send_sms_button);
        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSmsMessage(phoneNumberEditText.getText().toString(), smsBodyEditText.getText().toString());
            }
        });
    }

    /**
     * Sends a text message to a phone number
     * @param phoneNumber
     * @param text
     */
    protected void sendSmsMessage(String phoneNumber, String text) {
        Log.d(TAG, "Sending text message \"" + text + "\" to " + phoneNumber);

        SmsManager smsManager = SmsManager.getDefault();

        smsManager.sendTextMessage(phoneNumber, null, createSmsBody(text, "Password"), null, null);
    }

    protected String createSmsBody(String request, String password)
    {
        final String opener = "Ice-aGeoPhone usingPWD:";
        final String separator = " // ";
        String finalMessage = null;

        try {
            finalMessage = opener+sha1smsMessage(password)+separator+request;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return finalMessage;
    }

    /**
     *
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    protected String sha1smsMessage(String str)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = str.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        String hexCode = "";

        for (int i = 0; i < digest.length; i++) {
            hexCode +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring(1);
        }

        return hexCode;
    }
}