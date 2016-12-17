package com.ece.iceageophone.main.util;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.R.attr.data;

public class CustomLogger {

    private static final String TAG = "CustomLogger";
    private static final String OUTPUTFILE = "iceageo_log.txt";

    /**
     *
     * @param command : input command to be logged
     */
    public static void appendLog(String command, Context context) {
        File logFile = new File(context.getFilesDir()+File.separator+OUTPUTFILE);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Cannot create file " + context.getFilesDir()+File.separator+OUTPUTFILE, e);
                e.printStackTrace();
            }
            try {
                OutputStreamWriter osw = new OutputStreamWriter(context.openFileOutput(context.getFilesDir()+File.separator+OUTPUTFILE, Context.MODE_PRIVATE));
                osw.append(command);
                osw.close();
            } catch (IOException e) {
                Log.e(TAG, "Cannot append log to " + context.getFilesDir()+File.separator+OUTPUTFILE, e);
                e.printStackTrace();
            }
        }
    }

    public static void getHistory()
    {

    }

    public static void getHistory(String contains)
    {

    }

    private String readFromFile(Context context) {

        String str = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                str = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return str;
    }
}
