package com.beidouapp.ui.fragment;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MyLocationData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyLocationListener extends BDAbstractLocationListener {
    private static MyLocationData locData;
    double latitude;
    private double longitude;
    private double altitude;
    private float speed;
    private String time;
    private float direction;
    private String district;

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        //MyLocationData 是定位数据建造的类就把它想象成一个工具用于管理定位数据（可以设置它的精度、定位方向、经纬度等参数）

        locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius()).direction(100).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();



        this.latitude = bdLocation.getLatitude();
        this.longitude = bdLocation.getLongitude();
        this.altitude = bdLocation.getAltitude();
        this.speed = bdLocation.getSpeed();
        this.time = bdLocation.getTime();
        this.direction = bdLocation.getDirection();
        this.district = bdLocation.getDistrict();
//        Log.d("zw", "onReceiveLocation: 此时的几个关键信息为" + this.latitude + "接下来： " + this.longitude +
//                "此时所处的区域为" + bdLocation.getDistrict() + "此时的时间为" + this.time);
    }

    public List<String> getLatLng(){
        List<String> list = new ArrayList<String>();
        list.add(String.valueOf(this.latitude));
        list.add(String.valueOf(this.longitude));
        list.add(String.valueOf(this.altitude));
        list.add(String.valueOf(this.speed));
        list.add(String.valueOf(this.time));
        list.add(String.valueOf(this.direction));
        return list;
    }

    public String getDistrict() {
        return district;
    }
}
