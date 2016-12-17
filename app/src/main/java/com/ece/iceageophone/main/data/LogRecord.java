package com.ece.iceageophone.main.data;

import android.content.Context;
import android.util.Log;

import com.ece.iceageophone.main.activity.HistoryActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ece.iceageophone.main.activity.HistoryActivity.OUTPUTFILE;

public class LogRecord {
    private String date;
    private String record;
    private int status; // -1 if failed, 1 if succeeded, default if unknown
    private static final String TAG = "LogRecord";

    private FileOutputStream outputStream;

    public LogRecord(String record) {
        this.record = record;
        this.date = new SimpleDateFormat("dd/MM hh:mm |").format(new Date());
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRecord(){return this.date+this.record+System.getProperty("line.separator");}

    public void appendRecord(Context context) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(OUTPUTFILE, Context.MODE_APPEND);
            outputStream.write(getRecord().getBytes());
            outputStream.close();
            Log.d(TAG, "Record saved into " + context.getFilesDir()  +"/"+ OUTPUTFILE);
        } catch (Exception e) {
            Log.e(TAG, "Cannot create file " + context.getFilesDir() +"/"+ OUTPUTFILE, e);
            e.printStackTrace();
        }
    }

    public static boolean flushRecords(Context context)
    {
        try {
            context.deleteFile(OUTPUTFILE);
            Log.d(TAG, "File deleted : " + context.getFilesDir()  +"/"+ OUTPUTFILE);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Cannot delete file " + context.getFilesDir() +"/"+ OUTPUTFILE, e);
            e.printStackTrace();
        }
        return false;
    }
}
