package com.beidouapp.model.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkManager {

    public static final int TYPE_WIFI_MOBILE = 1;
    public static final int TYPE_BLUETOOTH = 2;
    public static final int TYPE_NOT_CONNECT = 0;

    /**
     * 判断类型
     * @param context
     * @return
     */
    public static int getConnectivityStatus(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                    || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_WIFI_MOBILE;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_BLUETOOTH) {
                return TYPE_BLUETOOTH;
            }
        }
        return TYPE_NOT_CONNECT;
    }

    // wifi判断
    public static boolean isWIFI(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }

    //移动网络判断
    public static boolean isMobile(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
            return true;
        }
        return false;
    }

    //Bluetooth判断
    public static boolean isBluetooth(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_BLUETOOTH){
            return true;
        }
        return false;
    }

}
