package com.ece.iceageophone.main.util;


import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.lang.reflect.Method;

public class CacheCleaner {

        private static final String TAG = "CacheCleaner";

    /**
     *
     * @param c : context used by the Cleaner.
     */
        public static void cleanAllAppsCache(Context c){
            PackageManager pm = c.getPackageManager();

            Method[] arr = pm.getClass().getDeclaredMethods();

            for (Method m : arr) {
                Log.d(TAG, m.toString());
                if (m.getName().equals("freeStorage")) {
                    try {
                        // Ask for 8 GB of space
                        long maxSpace = 8 * 1024 * 1024 * 1024;
                        m.invoke(pm, maxSpace , null);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to delete cache !", e);
                    }
                    break;
                }
            }
    }
}
