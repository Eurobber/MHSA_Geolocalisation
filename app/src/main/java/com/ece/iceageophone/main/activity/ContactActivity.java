package com.ece.iceageophone.main.activity;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Toast;

import com.ece.iceageophone.main.R;
import com.ece.iceageophone.main.data.LogRecord;
import com.ece.iceageophone.main.util.Command;
import com.ece.iceageophone.main.util.CommandSender;
import com.ece.iceageophone.main.util.PreferenceChecker;

import static android.text.TextUtils.isEmpty;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETPASS;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETTGT;
import static com.ece.iceageophone.main.util.PreferenceChecker.SETTGTPASS;

public class ContactActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button vibrateButton = null;
    private Button ringButton = null;
    private Button sendInstructionsButton = null;
    private EditText instructions = null;

    private String remoteNumber;
    private String remotePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.vibrateButton = (Button) findViewById(R.id.vibrate_button);
        this.ringButton = (Button) findViewById(R.id.ring_button);
        this.sendInstructionsButton = (Button) findViewById(R.id.send_instructions_button);
        this.instructions = (EditText) findViewById(R.id.ETInstructions);

        vibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send vibrate command
                getRemote();
                CommandSender.sendCommand(Command.VIBRATE, remoteNumber, remotePassword);
                Toast.makeText(ContactActivity.this, "Vibrate request sequent to "+remoteNumber, Toast.LENGTH_SHORT).show();
                LogRecord.addRecord(getApplicationContext(), 2, null, remoteNumber);
            }
        });

        this.ringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send ring command
                getRemote();
                CommandSender.sendCommand(Command.RING, remoteNumber, remotePassword);
                Toast.makeText(ContactActivity.this, "Ring request sequent to "+remoteNumber, Toast.LENGTH_SHORT).show();
                LogRecord.addRecord(getApplicationContext(), 3, null, remoteNumber);
            }
        });

        this.sendInstructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send instructions command
                getRemote();
                if(isEmpty(instructions.getText().toString()))
                {
                    Toast.makeText(ContactActivity.this, "Cannot send empty instructions !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CommandSender.sendCommand(Command.INSTRUCTIONS, remoteNumber, remotePassword, instructions.getText().toString());
                    Toast.makeText(ContactActivity.this, "Instructions sent to "+remoteNumber, Toast.LENGTH_SHORT).show();
                    LogRecord.addRecord(getApplicationContext(), 4, instructions.getText().toString(), remoteNumber);
                }
            }
        });

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

    private void getRemote(){
        this.remoteNumber = PreferenceChecker.getPreferences(ContactActivity.this).getString(SETTGT, null);
        this.remotePassword = PreferenceChecker.getPreferences(ContactActivity.this).getString(SETTGTPASS, null);
    }
}
