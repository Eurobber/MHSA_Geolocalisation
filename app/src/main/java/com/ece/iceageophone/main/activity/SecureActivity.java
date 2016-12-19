package com.ece.iceageophone.main.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ece.iceageophone.main.R;
import com.ece.iceageophone.main.data.LogRecord;
import com.ece.iceageophone.main.util.Command;
import com.ece.iceageophone.main.util.CommandSender;
import com.ece.iceageophone.main.util.CustomAdminReceiver;
import com.ece.iceageophone.main.util.PreferenceChecker;

import static android.R.attr.data;
import static com.ece.iceageophone.main.util.PreferenceChecker.*;

public class SecureActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button rmtLock;
    private TextView TVResultPhone;

    private Button onAdmin;
    private Button offAdmin;

    private String remoteNumber;
    private String remotePassword;


    private static final int ADMIN_INTENT = 15;
    private static final String hint = "This application will lock the phone and ask for the password if remotely triggered. To trigger, you need an application password.";
    private ComponentName mCN;
    private DevicePolicyManager mDPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secure_activity);
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

        TVResultPhone = (TextView) findViewById(R.id.TVnumber);
        rmtLock = (Button) findViewById(R.id.BtnRmtLock);
        onAdmin = (Button) findViewById(R.id.BtnAdminRights);
        offAdmin = (Button) findViewById(R.id.BtnDisableAdmin);

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mCN = new ComponentName(SecureActivity.this, CustomAdminReceiver.class);
        getRemote();

        /**
         *   Enables admin privileges on this phone
         */
        this.onAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mCN);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, hint);
                startActivityForResult(intent, ADMIN_INTENT);
            }
        });
        /**
         *   Disables admin privileges on this phone
         */
        this.offAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mDPM.isAdminActive(mCN)){
                    mDPM.removeActiveAdmin(mCN);
                    Toast.makeText(SecureActivity.this, "Disabled admin privileges. This phone is no longer lockable.", Toast.LENGTH_SHORT).show();
                    LogRecord.addRecord(getApplicationContext(), 10, null, null);
                }
            }
        });

        /**
         * Remotely locks target phone if admin privileges are enabled on it
         */
        this.rmtLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PreferenceChecker.getPreferences(SecureActivity.this).contains(SETTGT))
                {
                    CommandSender.sendCommand(Command.LOCK, remoteNumber, remotePassword);
                    Toast.makeText(SecureActivity.this, "Lock screen request sequent to "+remoteNumber, Toast.LENGTH_SHORT).show();
                    LogRecord.addRecord(getApplicationContext(), 8, null, remoteNumber);
                }

                else{
                    Toast.makeText(SecureActivity.this, "Please define a target in the settings before you attempt anything.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        if(PreferenceChecker.getPreferences(this).contains(SETTGT))
        {
            TVResultPhone.setText(PreferenceChecker.getPreferences(this).getString(SETTGT, null));
        }else TVResultPhone.setText("Not set (@Settings)");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK ) {
                Toast.makeText(SecureActivity.this, "Your phone is now remotely lockable. Can't uninstall the app.", Toast.LENGTH_SHORT).show();
                LogRecord.addRecord(getApplicationContext(), 9, null, null);
            }else{
                Toast.makeText(SecureActivity.this, "Couldn't grant administrator privileges.", Toast.LENGTH_SHORT).show();
            }
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
        this.remoteNumber = PreferenceChecker.getPreferences(SecureActivity.this).getString(SETTGT, null);
        this.remotePassword = PreferenceChecker.getPreferences(SecureActivity.this).getString(SETTGTPASS, null);
    }
}