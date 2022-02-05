package com.beidouapp.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.beidouapp.R;
import com.beidouapp.model.messages.Other_loc;
import com.beidouapp.model.messages.recOtherPositions;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.messages.posBD;
import com.beidouapp.model.messages.posFromBD;
import com.beidouapp.model.utils.MyOrientationListener;
import com.beidouapp.ui.MainActivity;
import com.beidouapp.ui.other_loc;


import java.io.IOException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

import okhttp3.Response;

/**
 HomeFragment 作用是显示地图，团队位置的获取是从服务器上获取的，使用onuithread线程来获取位置
 *
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    /**
     *
     */
    private MapView mapView;  //地图
    private BaiduMap mMap;    //地图
    private boolean ifFirst = true;  //判断是否第一次
    private ImageButton btn1;   //
    private ImageButton btn2;   //
    private MKOfflineMap moffline;  //离线地图
    private MKOfflineMapListener mml;   //离线地图监听
    private MKOLUpdateElement MKOLUpdateElement;
    private int cityid; //离线地图下载城市id
    private String username;
    private String password;
    private IntentFilter intentFilter;
    private Timer timer;    //定时器
    private Handler handler;    //定时器处理
    private Handler handlerOtherloc;    //定时器处理
    private TimerTask locFresh;     //定时器处理事务
    private LocationManager lm;     //地点管理
    private Criteria criteria;
    private List<String> lonAndLat;
    private double latitude;        //经度·
    private double lontitude;       //维度
    private BaiduMap.OnMapLongClickListener listener;   //地图长安监听
    private MarkerOptions markerOptions;    //marker形式
    private UiSettings mUiSet;      //
    private BaiduMap.OnMarkerClickListener listenerMark;        //Marker监听器
    private long timestamp;
    private String curToken;
    private MyOrientationListener myOrientationListener;
    private MyLocationData.Builder locDataBuilder;
    private ImageButton otherLocbtn;
    private String token;
    private String bodyOtherLoc;
    private String uid;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        iniAll(view);


        username = "User0";
        password = "000000";


        curToken = getArguments().get("curToken").toString();
        token = getArguments().getString("token").toString();
        uid = getArguments().getString("loginId");
        Log.d("zw", "onCreateView: 测试用的uid是：" + uid);
        Log.d("zw", "onCreateView: 测试用的curToken是：" + token);
        Log.d("zw", "onCreateView: 测试用的curToken是：" + curToken);



       lonAndLat = loc(getActivity().getApplicationContext());
        Log.d("zw", "onCreateView: " + lonAndLat.toString());
        transToBD();


        mMap.getUiSettings().setCompassEnabled(false);


        testJson();
        testBDRequest();

        show_my_loc(String.valueOf(latitude), String.valueOf(lontitude));
//        show_other_loc(String.valueOf(latitude), String.valueOf(lontitude));

//        timInit();
//        handlerInit();

        mapListen();//设置地图监听器，和地图进行交互
        otherInit();//其他地图小部件设置



        return view;

    }

    private void iniAll(@NonNull View view) {
        mapView = view.findViewById(R.id.mMV);
        btn1 = view.findViewById(R.id.dingwei);
        btn2 = view.findViewById(R.id.download);
        otherLocbtn = view.findViewById(R.id.people);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        otherLocbtn.setOnClickListener(this);
        mMap = mapView.getMap();
    }


    /**
     * 测试获取福大那边的位置
     */
    private void testBDRequest() {
        Log.d("zw", "testBDRequest: 测试福大位置获取开始");
        List<String> list = new ArrayList<String>();
        list.add("13886415060");
        Log.d("zw", "testBDRequest: 需要获取位置的设备是：" + list);
        posBD posBD = new posBD(list);
        String json = JSONUtils.sendJson(posBD);
        Log.d("zw", "testBDRequest: 准备发送给福大的json格式是：" + json);
        try {
            Thread threadPos = new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpUtils.getInstance(getActivity().getApplicationContext()).postBD("http://119.3.130.87:50099/whbdApi/device/pos/getCurrent", json, new OkHttpUtils.MyCallback() {
                        @Override
                        public void success(Response response) throws IOException {
                            Log.d("zw", "success: 访问福大北斗的位置信息得到的结果："+ response.body().string());
                        }

                        @Override
                        public void failed(IOException e) {
                            Log.d("zw", "failed: 访问福大北斗获取位置信息失败");
                        }
                    }, curToken);
                }
            });
            threadPos.start();
        }catch (Exception e){
            Log.d("zw", "testBDRequest: 访问福大北斗位置信息线程崩溃");
            e.printStackTrace();
        }

    }


    /**
     * 测试json工具是否正确的函数
     */
    private void testJson() {
        Other_loc other_loc = new Other_loc(username, password);
        String json = JSONUtils.sendJson(other_loc);
        Log.d("zw", "testJson: " + json);
        final String[] latitude = new String[1];
        final String[] lontitude = new String[1];
        String body = "{\"data\":{\"position\":[{\"posTime\":\"2021-12-29 16:43:28\",\"lngDir\":1,\"lng\":\"129.456\",\"latDir\":1,\"deviceId\":\"102839\",\"lat\":\"16.321\",\"speed\":\"10.56\"}]},\"rtnMsg\":\"成功\",\"time\":1642748476622,\"rtnCode\":\"0\"}";
        recOtherPositions pos = JSONUtils.receiveLocJSON(body);
        latitude[0] = pos.getData().getPosition().get(0).getLat();
        lontitude[0] = pos.getData().getPosition().get(0).getLng();
        Log.d("zw", "testJson: " + latitude[0]);
        Log.d("zw", "testJson: " + lontitude[0]);

    }

    private void transToBD() {
        LatLng point = new LatLng(Double.parseDouble(lonAndLat.get(0)),Double.parseDouble(lonAndLat.get(1)));
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(point);
        LatLng desLatlon = converter.convert();
        latitude = desLatlon.latitude;
        lontitude = desLatlon.longitude;
        Log.d("zw", "onCreateView: " + String.valueOf(latitude));
        Log.d("zw", "onCreateView: " + String.valueOf(lontitude));
    }


    /**
     * 定时器处理事件，每10秒处理一次事件，状态1 和 0 表示有无网络
     * 定时10秒获取一次其他人（选中）的位置
     * 由于显示位置的函数中首先将地图上的所有的marker清除，所以再次显示位置的时候，需要重新将所有的marker加载
     * 其中无网络需要将信息存入到一个消息队列中（我理解成文件）
     */
//    private void handlerInit() {
//        handler = new Handler() {
//            @SuppressLint("HandlerLeak")
//            public void handleMessage(Message message) {
//                switch (message.what) {
//                    case 0: {
//                        break;
//                    }
//                    case 1: {
//                        testRequest();
//                        Log.d("zw", "handleMessage:  + 定时成功");
//                        break;
//                    }
//                }
//            }
//        };
//    }
//
//
//    /**
//     * timInit函数作为计时器使用，
//     * 其中message.what中赋值的是网络状态，分为有网络和无网络情况
//     */
//
//    private void timInit() {
//        timer = new Timer();
//        locFresh = new TimerTask() {
//            @Override
//            public void run() {
//                Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
//            }
//        };
//        timer.schedule(locFresh, 0, 10 * 1000);
//    }


    /**
     * gps定位，这台手机上不知道能否运行
     * @param context
     * @return  返回的是gps定位信息
     */
    private List<String> loc(Context context) {
        List<String> list = new ArrayList<String>();
        lm = (LocationManager) getContext().getSystemService(context.LOCATION_SERVICE);
        criteria = createFineCriteria();
        String provider = lm.getBestProvider(criteria, true);
        if (provider != null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("zw", "loc: + 没有权限");
            }
            else{
                lm.requestLocationUpdates("gps", 1000, 1, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });

            }
            Location location = lm.getLastKnownLocation(provider);
            if(location != null){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double altitude = location.getAltitude();
                float speed = location.getSpeed();
                long time = location.getTime();

                list.add(String.valueOf(latitude));
                list.add(String.valueOf(longitude));
                list.add(String.valueOf(altitude));
                list.add(String.valueOf(speed));
                list.add(String.valueOf(time));
            }
        }
        return list;
    }


    /**不论有无网络，都可以根据gps发送定位，但是gps格式的位置需要改成百度上的坐标系
     *
     */
    private void show_my_loc(String lat, String lon) {
        int mXDirection;

        locDataBuilder = new MyLocationData.Builder()
                .accuracy(30)
                .latitude(Double.parseDouble(lat))
                .longitude(Double.parseDouble(lon)).direction(0.54f);

        //初始化方位角 由底层传感器获得
        iniMyLocMap();

    }



    /**网络情况下的位置显示  show_other_loc show_others_loc
     *
     * @param a
     * @param b
     */

    private void show_other_loc(String a, String b) {
        /**
         * a 是经度
         * b 是纬度
         */
        mMap.clear(); //清除地图所有标记
        double latitude = Double.parseDouble(a);
        double longtitude = Double.parseDouble(b);
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longtitude);
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_mark);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);

//在地图上添加Marker，并显示
        mMap.addOverlay(option);

    }

    /**
     * 传入两个List，分别是代表着经度纬度List，第一个位置就是两个List的第一个元素组成的序列对
     * @param a 经度List
     * @param b 纬度List
     */
    private void show_others_loc(List<String> a, List<String> b) {
        /**
         * a 是经度
         * b 是纬度
         */
        mMap.clear();
        if(a.size() == b.size()) {
            int num = a.size();
            for(int i = 0; i<num; i++){
                String lat = a.get(i);
                String lon = b.get(i);
                double latitude = Double.parseDouble(lat);
                double longtitude = Double.parseDouble(lon);
                //定义Maker坐标点
                LatLng point = new LatLng(latitude, longtitude);
//构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_mark);
//构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
//在地图上添加Marker，并显示
                mMap.addOverlay(option);

            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        myOrientationListener.start();
    }

    @Override
    public void onStop() {
        super.onStop();
//        myOrientationListener.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dingwei: {
                show_my_loc(String.valueOf(latitude), String.valueOf(lontitude));
                testRequest();
//                sendRequestForLoc(username);
                break;
            }
            case R.id.download:{
                downLoadMap();
                break;
            }
            case R.id.people:{
                handlerOtherloc = new Handler(){
                    public void handleMessage(Message message) {
                        switch (message.what)
                        {
                            case 1:{
                                Intent intent = new Intent(getActivity(), other_loc.class);
                                intent.putExtra("token", token);
                                intent.putExtra("curToken", curToken);
                                intent.putExtra("status", bodyOtherLoc);
                                intent.putExtra("uid", uid);
//                                Log.d("zw", "handleMessage: post亮哥的服务器得到的数据" + bodyOtherLoc);
//                                intent.putExtra()
                                startActivityForResult(intent, 1); //这里注意使用的是带有回调方式的，回调代码为1
                                break;
                            }
                            default:{break;}
                        }

                    }
                    };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpUtils.getInstance(getActivity().getApplicationContext()).post("http://139.196.122.222:8081/getStatus1", new OkHttpUtils.MyCallback() {
                            @Override
                            public void success(Response response) throws IOException {
                                bodyOtherLoc = response.body().string();
                                Log.d("zw", "success: post亮哥的服务器得到的数据" + bodyOtherLoc);
                                Message message = new Message();
                                message.what = 1;
                                handlerOtherloc.sendMessage(message);
                            }

                            @Override
                            public void failed(IOException e) {
//                                没有网络的时候，显示的应该全是空，及这个时候的json数据为空，但是注意bodyOtherloc仍然需要初始化
                            }
                        });


                    }
                }).start();

                break;
            }
            default:break;
        }
    }

    private void testRequest() {
        show_my_loc(String.valueOf(latitude), String.valueOf(lontitude));
        show_other_loc("30.518848","114.350055");
    }


    /**
     * 网络获取位置
     * @param username
     */
    private void sendRequestForLoc(String username) {
        if (username == "User0")
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Other_loc other_loc = new Other_loc(username, password);
                        String json = JSONUtils.sendJson(other_loc);
                        OkHttpUtils.getInstance(getActivity().getApplicationContext()).post("", json, new OkHttpUtils.MyCallback() {
                            @Override
                            public void success(Response response) throws IOException {
//                                String lontitude;
                                final String[] latitude = new String[1];
                                final String[] lontitude = new String[1];
                                if(response.code() == 200){
                                    String body = response.body().toString();
                                    recOtherPositions pos = JSONUtils.receiveLocJSON(body);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            latitude[0] = pos.getData().getPosition().get(0).getLat();
                                            lontitude[0] = pos.getData().getPosition().get(0).getLng();
                                            show_other_loc(latitude[0], lontitude[0]);
                                        }
                                    });

                                }
                            }

                            @Override
                            public void failed(IOException e) {

                            }
                        });
                    }catch (Exception e)
                    {
                        e.printStackTrace();;
                    }
                }
            }).start();
        }
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        /**
                         * 根据选择的用户（群组id）返回一系列的位置（list类型）
                         * */

                        Other_loc other_loc = new Other_loc(username, password);
                        String json = JSONUtils.sendJson(other_loc);
                        OkHttpUtils.getInstance(getActivity().getApplicationContext()).post("", json, new OkHttpUtils.MyCallback() {
                            @Override
                            public void success(Response response) throws IOException {
                                List<String> lontitude = new ArrayList<String>();
                                List<String> latitude = new ArrayList<String>();
                                if(response.code() == 200){
                                    String body = response.body().toString();
                                    recOtherPositions pos = JSONUtils.receiveLocJSON(body);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            int num = pos.getData().getPosition().size();
                                            for(int i = 0; i < num; i++)
                                            {
                                                lontitude.add(pos.getData().getPosition().get(i).getLng());
                                                latitude.add(pos.getData().getPosition().get(i).getLat());
                                            }
                                            show_others_loc(latitude, lontitude);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void failed(IOException e) {

                            }
                        });

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    private void downLoadMap() {
        moffline = new MKOfflineMap();
        mml = new MKOfflineMapListener() {
            @Override
            public void onGetOfflineMapState(int i, int i1) {
                switch (i){
                    case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
                    {
                        MKOLUpdateElement = moffline.getUpdateInfo(i1);
                        break;
                    }
                    case MKOfflineMap.TYPE_NEW_OFFLINE:
                    {

                        break;
                    }
                    case MKOfflineMap.TYPE_VER_UPDATE:
                    {

                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
            }
        };
        moffline.init(mml);
        ArrayList<MKOLSearchRecord> records = moffline.searchCity("武汉市");
        if(records!=null&&records.size()==1){
            cityid = records.get(0).cityID;
        }
        moffline.start(cityid);
    }


    /**
     * mapListen 作用是和地图进行交互，主要的功能有，长安地图，显示坐标和marker， 确认按钮上传位置，取消按钮取消显示并不做上传
     * marker点击作用是，取消按钮，取消当前marker
     */
    private void mapListen() {
        // 地图长按监听事件，长安地图显示坐标，提交 提交当前位置给服务器， 取消 退出显示
        listener = new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);

                mMap.animateMapStatus(mapStatusUpdate);

                mMap.clear();

                markerOptions = new MarkerOptions();

                markerOptions.position(latLng);

                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark);

                markerOptions.icon(bitmapDescriptor);

                markerOptions.zIndex(17);   //层级

                Marker marker = (Marker) mMap.addOverlay(markerOptions);

                LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
                View view = inflater.inflate(R.layout.text_item, null);

                TextView textView = (TextView) view.findViewById(R.id.tv_loc);
                Button btnCancel = view.findViewById(R.id.btn_cancel);
                Button btnCommit = view.findViewById(R.id.btn_search);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.hideInfoWindow();
                    }
                });

                btnCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                DecimalFormat df = new DecimalFormat("#0.0000000");


                textView.setText("纬度：" + df.format(latLng.latitude) +  "经度：" + df.format(latLng.longitude));
                InfoWindow infoWindow = new InfoWindow(view, latLng, -47);
                mMap.showInfoWindow(infoWindow);
                Log.d("zw", "onMapLongClick: 长按成功");
            }
        };
        mMap.setOnMapLongClickListener(listener);

        //地图marker，取消 取消标记， 退出 退出当前状态
        listenerMark = new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latlon = marker.getPosition();
                Log.d("zw", "onMarkerClick: 取消标记");
                LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
                View view = inflater.inflate(R.layout.text_item, null);
                TextView textView = (TextView) view.findViewById(R.id.tv_loc);
                Button btnCancel = view.findViewById(R.id.btn_cancel);
                Button btnCommit = view.findViewById(R.id.btn_search);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("zw", "onClick: 真的取消标记");
                        marker.remove();
                        mMap.hideInfoWindow();
                    }
                });
                btnCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.hideInfoWindow();
                    }
                });
                InfoWindow infowindow = new InfoWindow(view, latlon, +47);
                mMap.showInfoWindow(infowindow);
                return true;
            }
        };
        mMap.setOnMarkerClickListener(listenerMark);
    }

    private void otherInit() {
        mUiSet = mMap.getUiSettings();
        mUiSet.setCompassEnabled(true);
        mUiSet.setOverlookingGesturesEnabled(false);
        mUiSet.setRotateGesturesEnabled(false);
    }

    /**
     * 这个函数是直接从北斗那边获取想要的位置坐标
     * @param usernames 传入的参数
     */
    private void sendRequestForLoc_BD(List<String> usernames){

    }

    public static Criteria createFineCriteria() {

        Criteria c = new Criteria();
//        c.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        c.setAltitudeRequired(true);//包含高度信息
        c.setBearingRequired(true);//包含方位信息
        c.setSpeedRequired(true);//包含速度信息
//        c.setCostAllowed(true);//允许付费
//        c.setPowerRequirement(Criteria.POWER_HIGH);//高耗电
        return c;
    }

    private void iniMyLocMap() {
        Log.d("zw", "iniMyLocMap: 进入角度设置初始化");
        myOrientationListener = new MyOrientationListener(getActivity());
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {

            @Override
            public void onOrientationChanged(float x) {
                Log.d("zw", "onOrientationChanged: 方向角为：" + x);
                MyLocationData myLocData = locDataBuilder.build();

                Log.d("zw", "show_my_loc: 设置的角度是" + myLocData.direction);

                mMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                mMap.setMyLocationEnabled(true);
                mMap.setMyLocationData(myLocData);

                MyLocationConfiguration configuration = new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL,
                        true,
                        null,
                        0xAAFFFF88,
                        0xAA00FF00);
                // 在定义了以上属性之后，通过如下方法来设置生效：
                mMap.setMyLocationConfiguration(configuration);
                locDataBuilder.direction(360 - x);

                if (ifFirst) {
                    LatLng ll = new LatLng(myLocData.latitude, myLocData.longitude);
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll);
                    builder.zoom(20.0f);    // 放大为20层级
                    mMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    //放大层级
                    ifFirst = false;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Log.d("zw", "onActivityResult: 回调到HomeFragment：" + requestCode);
            Log.d("zw", "onActivityResult: 现在的resultcode是：" + resultCode);
            if (resultCode == 2)
            {
                Log.d("zw", "onActivityResult: 是由other_loc Activity返回：" + resultCode);
                Log.d("zw", "onActivityResult: 现在确定是否由other_loc返回的：" + data.getStringArrayListExtra("pos"));
                ArrayList<String> idlist = data.getStringArrayListExtra("pos");
                List<String> list = new ArrayList<String>(idlist);
//                list.add("13886415060");
                Log.d("zw", "testBDRequest: 需要获取位置的设备是：" + list);
                posBD posBD = new posBD(list);
                String json = JSONUtils.sendJson(posBD);
                Log.d("zw", "testBDRequest: 准备发送给福大的json格式是：" + json);
                try {
                    Thread threadPos = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpUtils.getInstance(getActivity().getApplicationContext()).postBD("http://119.3.130.87:50099/whbdApi/device/pos/getCurrent", json, new OkHttpUtils.MyCallback() {
                                @Override
                                public void success(Response response) throws IOException {
                                    Log.d("zw", "success: 访问福大北斗的位置信息得到的结果："+ response.body().string());
                                }

                                @Override
                                public void failed(IOException e) {
                                    Log.d("zw", "failed: 访问福大北斗获取位置信息失败");
                                }
                            }, curToken);
                        }
                    });
                    threadPos.start();
                }catch (Exception e){
                    Log.d("zw", "testBDRequest: 访问福大北斗位置信息线程崩溃");
                    e.printStackTrace();
                }
            }
            else if(resultCode == 0)
            {

            }
        }
    }
}
