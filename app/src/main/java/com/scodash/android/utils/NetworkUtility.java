package com.scodash.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;

public class NetworkUtility {

    private static boolean isWifiEnable = false;
    private static boolean isMobileNetworkAvailable = false;

    public static boolean isNetWorkAvailableNow(Context context) {

        boolean isNetworkAvailable = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        isWifiEnable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        isMobileNetworkAvailable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();

        if (isWifiEnable || isMobileNetworkAvailable) {
            if (isOnline())
                isNetworkAvailable = true;
        }

        return isNetworkAvailable;


    }

    public static boolean isOnline() {
        /*Just to check Time delay*/
        long t = Calendar.getInstance().getTimeInMillis();

        Runtime runtime = Runtime.getRuntime();
        try {
            /*Pinging to Google server*/
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            long t2 = Calendar.getInstance().getTimeInMillis();
            Log.i("NetWork check Time", (t2 - t) + "");
        }
        return false;
    }
}
