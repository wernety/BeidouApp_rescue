package com.beidouapp.ui.fragment;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MyLocationData;

import java.util.ArrayList;
import java.util.List;

public class MyLocationListener extends BDAbstractLocationListener {
    private static MyLocationData locData;
    double latitude;
    private double longitude;
    private double altitude;
    private float speed;
    private String time;

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
    }

    public List<String> getLatLng(){
        List<String> list = new ArrayList<String>();
        list.add(String.valueOf(latitude));
        list.add(String.valueOf(longitude));
        list.add(String.valueOf(altitude));
        list.add(String.valueOf(speed));
        list.add(String.valueOf(time));
        return list;
    }
}
