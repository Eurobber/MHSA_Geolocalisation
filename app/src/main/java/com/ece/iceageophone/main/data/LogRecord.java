package com.ece.iceageophone.main.data;

import android.content.Context;
import android.util.Log;

import com.ece.iceageophone.main.activity.HistoryActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.type;


public class LogRecord {
    private String date;
    private String record;
    private static final String TAG = "LogRecord";

    public LogRecord(String rec) {
        this.record = rec;
        this.date = new SimpleDateFormat("dd/MM HH:mm ").format(new Date());
        Log.d(TAG, this.date);
    }

    @Override
    public String toString(){return this.date+this.record+"\r\n";}

    public static void addRecord(Context context, int type, String param1, String param2) {
        FileOutputStream outputStream = null;
        String content = null;
        String path = context.getFilesDir() + "/" + HistoryActivity.OUTPUTFILE;

        try {
            outputStream = context.openFileOutput(HistoryActivity.OUTPUTFILE, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Cannot open file " + path, e);
            e.printStackTrace();
        }

        switch(type){
            case 1: // SMS Sent
                content = "Sent SMS \""+param1+"\" to "+param2;
                break;
            case 2: // Vibrate Request
                content = "Requested vibration from "+param2;
                break;
            case 3: // Ring request
                content = "Requested ringing from "+param2;
                break;
            case 4: // Instructions sent
                content = "Displayed instructions \""+param1+"\" on "+param2+"\'s screen";
                break;
            case 5: // Geolocalisation
                content = "Tried to locate through GPS number "+param2;
                break;
            case 6: // Geolocation through magnetic field
                content = "Tried to locate through magnetic field number "+param2;
                break;
            case 7:
                content = "Received positive response from "+param2+" on"+param1+" request";
                break;
            case 8:
                content = "Received no response from "+param2+" on"+param1+" request";
                break;
            default:
                break;

        }

        try {
            outputStream.write(new LogRecord(content).toString().getBytes());
            outputStream.close();

        } catch (Exception e) {
            Log.e(TAG, "Cannot create file " + path, e);
            e.printStackTrace();
        }
    }
}
