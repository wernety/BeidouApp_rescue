package com.beidouapp.model.utils;



import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class state_request {

    private LocationManager lm;
    private Criteria criteria;

    public state_request(Context context) {
        criteria = createFineCriteria();
        lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
    }

//
    public static String getDeviceId(Context context) {
        String deviceId;
//        TelephonyManager telephonemanage = (TelephonyManager) context
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        try {
//            deviceId = telephonemanage.getDeviceId();
//            return deviceId;
//        } catch (Exception e) {
//            Log.i("error", e.getMessage());
//            return null;
//        }
        try {
            deviceId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return deviceId;
        } catch (Exception e) {
            Log.i("error", e.getMessage());
            return null;
        }
    }

    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

 //
    public static int getBattery(Context context){
        BatteryManager batteryManager = (BatteryManager)context.getSystemService(context.BATTERY_SERVICE);
        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return battery;
    }


    public List<String> loc(Context context) {
        List<String> list = new ArrayList<String>();
        //criteria = createFineCriteria();
        Log.i("8023", criteria.toString());
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //String provider = lm.getBestProvider(criteria, true);
        String provider = "passive";
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            } else {
                Location location = lm.getLastKnownLocation(provider);
                Log.i("8023", "-------" + location);

                //获取维度信息
                double latitude = location.getLatitude();
                //获取经度信息
                double longitude = location.getLongitude();
                //获取海拔高度
                double altitude = location.getAltitude();
                //获取速度
                float speed = location.getSpeed();
                //获取时间戳
                long time = location.getTime();





                list.add(String.valueOf(latitude));
                list.add(String.valueOf(longitude));
                list.add(String.valueOf(altitude));
                list.add(String.valueOf(speed));
                list.add(String.valueOf(time));
                // Toast.makeText(context, "1.请打开我的位置", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.i("8023","gangan");
        }
        return list;
    }


    //
    public List<String> whoGiveloc(Context context){
        lm = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
        List<String> lp = lm.getAllProviders();
        for (String item:lp)
        {
            Log.i("8023", "可用位置服务："+item);
        }
        return lp;
    }




    public static Criteria createFineCriteria() {

        Criteria c = new Criteria();
        //c.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        c.setAltitudeRequired(true);//包含高度信息
        c.setBearingRequired(true);//包含方位信息
        c.setSpeedRequired(true);//包含速度信息
        //c.setCostAllowed(false);//允许付费
        //c.setPowerRequirement(Criteria.POWER_HIGH);//高耗电
        Log.i("8023", "gangangangangan");
        return c;
    }





}
