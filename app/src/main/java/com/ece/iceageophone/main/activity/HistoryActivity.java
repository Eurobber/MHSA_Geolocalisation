package com.ece.iceageophone.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ece.iceageophone.main.R;
import com.ece.iceageophone.main.data.LogRecord;
import com.ece.iceageophone.main.util.SmsSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public class HistoryActivity extends ActionBarActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText contextDisplay;
    private static final String TAG = "History";
    private ListView mListView;
    private Button flushHistory;
    public static final String OUTPUTFILE = "iceageo_log.txt";
    private ArrayList<LogRecord> LLR;

    private InputStream in;
    private BufferedReader bfr;
    String ln;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
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
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "Starting History");
        mListView = (ListView) findViewById(R.id.listView_history);

        flushHistory = (Button) findViewById(R.id.flushHistoryBtn);
        flushHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LogRecord.flushRecords(getApplicationContext())){
                    finish();
                    startActivity(getIntent());
                };
            }
        });

        LLR = fetchRecords();
        if(LLR!=null)
        {
            List<String> LS = new ArrayList<>();
            for(LogRecord lr : LLR){
                LS.add(lr.getRecord());
                Log.d(TAG, "Read record from " + getApplicationContext().getFilesDir()+"/"+OUTPUTFILE);
            }

            String[] stringArray = LS.toArray(new String[0]);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, stringArray);
            mListView.setAdapter(adapter);
        }
    }

    public ArrayList<LogRecord> fetchRecords(){

        ArrayList<LogRecord> LLR = new ArrayList<LogRecord>();
        BufferedReader BR = null;

        try {
            StringBuffer output = new StringBuffer();
            BR = new BufferedReader(new FileReader(getApplicationContext().getFilesDir()+"/"+OUTPUTFILE));
            Log.d(TAG, "Log read from " + getApplicationContext().getFilesDir()+"/"+OUTPUTFILE);
            String line = "";
            while ((line = BR.readLine()) != null) {
                LogRecord r = new LogRecord(line);
                LLR.add(r);
                Log.d(TAG, "Output file now has "+LLR.size()+" record(s)");
            }
            BR.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return LLR;
    }
}