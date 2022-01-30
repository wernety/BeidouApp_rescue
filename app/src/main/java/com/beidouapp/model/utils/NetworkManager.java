package com.beidouapp.model.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkManager {

    public int NetStatus = 0;

    //判断是否联网
    public static boolean isNetworkAvailable(Context context){

        if (context == null){
            return false;
        }else {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isAvailable()) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    Log.i("MainActivity", "当前没有联网");
                    return true;
                }
            }
        }
        return false;
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

    //北斗判断
    public static boolean isBeidou(Context context){
        return false;
    }


    public int NetworkDetect(Context context){
        int status = 0;
        if (isNetworkAvailable(context)){
            if (isWIFI(context)){
                status = 1;
            }
            else if (isMobile(context)){
                status = 2;
            }
            else if (isBluetooth(context)){
                status = 3;
            }
            else if (isBeidou(context)){
                status = 4;
            }
        }
        this.NetStatus = status;
        return status;
    }
}
