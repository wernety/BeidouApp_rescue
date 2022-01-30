package com.beidouapp.model.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class LocationUtils {
    private LocationManager lm;
    private LocationProvider lp;
    private Criteria criteria;

    public LocationUtils(Context context) {
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //lp = lm.getProvider(LocationManager.FUSED_PROVIDER);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        String provider = lm.getBestProvider(criteria, true);
        lm.requestSingleUpdate(provider, new MyLocationListener(), null);
        Location location = lm.getLastKnownLocation(provider);
        Log.i("8023", "-------" + provider);
        while (location == null){
            lm.requestSingleUpdate(provider, new MyLocationListener(), null);
            location = lm.getLastKnownLocation(provider);
            Log.i("8023", "-------" + location);

        }
        Log.i("8023", "-------" + location);

        //获取维度信息
        double latitude = location.getLatitude();
        //获取经度信息
        double longitude = location.getLongitude();
        Log.i("8023", String.valueOf(latitude) + String.valueOf(longitude));

    }

    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }
}
