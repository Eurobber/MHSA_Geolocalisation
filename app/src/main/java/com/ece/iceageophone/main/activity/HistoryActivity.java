package com.ece.iceageophone.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ece.iceageophone.main.R;
import com.ece.iceageophone.main.util.PreferenceChecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static com.ece.iceageophone.main.util.PreferenceChecker.SETPASS;

public class HistoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "History";
    public static final String OUTPUTFILE = "iceageo_log.txt";

    private ListView mListView;
    private Button flushHistory;

    private String path;
    private BufferedReader bfr;

    ArrayAdapter<String> adapter;


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Search Bar filter
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                readRecords(query);
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
         Implementing Navigation links
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
        } else if(id == R.id.scan) {
            intent = new Intent(getApplicationContext(), ScanActivity.class);
        } else if(id == R.id.secure) {
            intent = new Intent(getApplicationContext(), SecureActivity.class);
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

        // Get Listview
        mListView = (ListView) findViewById(R.id.listView_history);
        // Get FlushHistory button
        flushHistory = (Button) findViewById(R.id.flushHistoryBtn);

        // Generate file path
        path = getApplicationContext().getFilesDir() +"/"+ OUTPUTFILE;

        // Reads records if exist
        readRecords("");

        // Listener on Flush History button
        flushHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flushRecords(getApplicationContext())){
                    finish();
                    startActivity(getIntent());
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                    builder.setMessage(R.string.no_file_message)
                            .setTitle(R.string.no_file);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }


    private void readRecords(String filter){
        ArrayList<String> listRec = new ArrayList<>();

        // Read data from file
        File f = new File(path);
        if(f.exists()){
            try {
                bfr = new BufferedReader(new FileReader(path));
                Log.d(TAG, "Log read from " + path);
                String line = "";
                while ((line = bfr.readLine()) != null) {
                    listRec.add(line);
                }
                bfr.close();
            } catch (IOException e) {
                Log.d(TAG, "Unable to read from file "+path);
                e.printStackTrace();
            }

            // Populate ListView accordingly
            if (filter==""){
                String[] arr = new String[listRec.size()];
                for (int i=0; i<listRec.size(); i++){
                    arr[i] = listRec.get(i).toString();
                }
                adapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, arr);
                mListView.setAdapter(adapter);
            }
            else{
                ArrayList<String> filteredList = new ArrayList<>();
                for(String str : listRec)
                {
                    if (str.contains(filter)) filteredList.add(str);
                }
                String[] filteredArr = new String[filteredList.size()];
                for (int i=0; i<filteredList.size(); i++){
                    filteredArr[i] = filteredList.get(i).toString();
                }
                adapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, filteredArr);
                mListView.setAdapter(adapter);
            }
        }
    }



    private boolean flushRecords(Context context)
    {
        try {
            context.deleteFile(OUTPUTFILE);
            Log.d(TAG, "File deleted : " + path);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Cannot delete file " + path, e);
            e.printStackTrace();
        }
        return false;
    }
}