package com.ece.iceageophone.main.data;

import android.util.Log;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LogRecord {
    private String date;
    private String record;
    private static final String TAG = "LogRecord";

    private FileOutputStream outputStream;

    public LogRecord(String rec) {
        this.record = rec;
        this.date = new SimpleDateFormat("dd/MM hh:mm ").format(new Date());
        Log.d(TAG, this.date);
    }

    @Override
    public String toString(){return this.date+this.record+"\r\n";}
}
