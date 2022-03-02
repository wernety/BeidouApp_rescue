package com.beidouapp.ui.fragment;

import static android.content.Context.SENSOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.beidouapp.model.utils.JSONUtils.Receive;
import static com.beidouapp.model.utils.JSONUtils.receivePosFromBDJson;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.UFormat;
import android.location.Address;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
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
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.beidouapp.R;
import com.beidouapp.model.DataBase.Pos;
import com.beidouapp.model.DataBase.orgAndUidAndKey;
import com.beidouapp.model.messages.Other_loc;
import com.beidouapp.model.messages.posFromBD;
import com.beidouapp.model.messages.recOtherPositions;
import com.beidouapp.model.messages.tracePosFromBD;
import com.beidouapp.model.messages.tracsPos;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.messages.posBD;
import com.beidouapp.model.utils.MyOrientationListener;
import com.beidouapp.model.utils.id2name;
import com.beidouapp.model.utils.selfPosJson;
import com.beidouapp.ui.DemoApplication;
import com.beidouapp.ui.other_loc;
import com.beidouapp.ui.trace_activity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import org.litepal.FluentQuery;
import org.litepal.LitePal;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

/**
 HomeFragment 作用是显示地图，团队位置的获取是从服务器上获取的，使用onuithread线程来获取位置
 *
 */

public class HomeFragment extends Fragment implements View.OnClickListener, SensorEventListener {

    /**
     *
     */
    private MapView mapView;  //地图
    private BaiduMap mMap;    //地图
    private boolean ifFirst = true;  //判断是否第一次
    private ImageButton btn1;   //
    private ImageButton traceBtn;
    private ImageButton btn3;   //
    private ImageButton btn4;   //
    private ImageButton btn5;   //
    private TextView textView1;   //
    private TextView textView2;   //
    private TextView textView3;   //
    private TextView textView4;   //
    private TextView textView5;   //

    private MKOfflineMap moffline;  //离线地图
    private MKOfflineMapListener mml;   //离线地图监听
    private MKOLUpdateElement MKOLUpdateElement;
    private int cityid; //离线地图下载城市id
    private String username;
    private String password;
    private IntentFilter intentFilter;
    private Timer timer;    //定时器
    private Handler handler;    //定时器处理
    private Handler handlerTrace;
    private static Handler handlerOtherloc;    //定时器处理
    private Handler handlermyloc;   //定时器处理
    private TimerTask locFresh;     //定时器处理事务
    private LocationManager lm;     //地点管理
    private Criteria criteria;
    private List<String> lonAndLat;
    private double latitude;        //经度·
    private double lontitude;       //维度
    private double altitude;       //海拔
    private double speed;       //速度
    private String district = new String();
    private String weatherType;
    private String weatherTemperature;
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
    private List<orgAndUidAndKey> records;
    private orgAndUidAndKey record;
    private String org;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private String pass;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private static Handler handlerecOtherloc;
    private List<posFromBD.Position> posLists;
    private CancellationTokenSource cancellationTokenSource;
    private List<Pos> posRecords;
    private Pos posRecord;
    private starPos selfPos;
    private String[] ss=new String[]{
            "集合点",
            "休息点",
            "救援集合点"
    };
    private String tracerId;
    private long startTime;
    private long endTime;
    private JSONArray array;
    private Handler handlerOtherTracePos;
    private Thread threadForUploadSelfLoc;
    private Thread threadForTrace;
    private Thread threadForWeather;
    private List<LatLng> traceList;
    private LocationClient locationClient;
    private MyLocationListener myLocationListener;
    private float mCurrentDir;
    private DemoApplication application;
    private Overlay mPolyline;
    private BaiduMap.OnPolylineClickListener listenerTrace;
    private int n = 0;
    private Handler handlerMyweather;
    private int delete;
    private SensorManager mSensorManager;
    private float currentDegree;
    private float mlastDir;
    private ImageView compass;


    public HomeFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        iniAll(view);
        iniMap();


        curToken = getArguments().get("curToken").toString();
        token = getArguments().getString("token");
        uid = getArguments().getString("loginId");
        pass = getArguments().getString("pass");



        try {
            lonAndLat = loc(getActivity().getApplicationContext());
            transToBD();
//            lonAndLat = loc2();
            show_info_text(lonAndLat);
//            latitude = Double.parseDouble(lonAndLat.get(0));
//            lontitude = Double.parseDouble(lonAndLat.get(1));
        } catch (Exception e) {
            Log.d("zw", "onCreateView: 位置信息捕获失败");
            e.printStackTrace();
            //使用其他方式获取位置
            lonAndLat = loc2();
            show_info_text(lonAndLat);
            latitude = Double.parseDouble(lonAndLat.get(0));
            lontitude = Double.parseDouble(lonAndLat.get(1));
            Log.d("zw", "onCreateView: 此时使用是新的定位方式");
        }
        Log.d("zw", "onCreateView: 此时的位置信息是" + lonAndLat.toString());
        mMap.getUiSettings().setCompassEnabled(false);
        testBDRequest();

        handlermyloc();
        timInit();


        mapListen();//设置地图监听器，和地图进行交互
        otherInit();//其他地图小部件设置



        return view;

    }

    /**
     * @description:
     * @event
     * @param view
     * @param
     * @return
     */
    private void iniAll(@NonNull View view) {
        application = (DemoApplication) getActivity().getApplicationContext();
        mapView = view.findViewById(R.id.mMV);
        btn1 = view.findViewById(R.id.dingwei);
        otherLocbtn = view.findViewById(R.id.people);
        traceBtn = view.findViewById(R.id.trace);
        btn1.setOnClickListener(this);
        otherLocbtn.setOnClickListener(this);
        traceBtn.setOnClickListener(this);
        mMap = mapView.getMap();
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.d("zw", "iniAll: 传感器有" + deviceSensors);

        textView1 = view.findViewById(R.id.weatherTemperature);
        textView2 = view.findViewById(R.id.longitudeLatitude);
        textView3 = view.findViewById(R.id.altitude);
        textView4 = view.findViewById(R.id.velocity);
        textView5 = view.findViewById(R.id.direction);
        compass = view.findViewById(R.id.imageViewCompass);
    }

    private void iniMap() {
        mMap.setMaxAndMinZoomLevel(17,11);
        //设置状态变化，当缩放等级为17级别的时候禁止缩放
        mMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                float zoom = mMap.getMapStatus().zoom;
                if (zoom>17){
                    Log.d("zw", "onMapStatusChangeStart: 此时地图的缩放等级为" + zoom);
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.zoom(17.0f);    // 放大为20层级
                    mMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                float zoom = mMap.getMapStatus().zoom;
                if (zoom>17){
                    Log.d("zw", "onMapStatusChangeStart: 此时地图的缩放等级为" + zoom);
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.zoom(17.0f);    // 放大为20层级
                    mMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                float zoom = mMap.getMapStatus().zoom;
                if (zoom>17){
                    Log.d("zw", "onMapStatusChangeStart: 此时地图的缩放等级为" + zoom);
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.zoom(17.0f);    // 放大为20层级
                    mMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }
        });
        mMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mMap.setMyLocationEnabled(true);
        myLocationListener = new MyLocationListener();
        locationClient = new LocationClient(getActivity().getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        option.setCoorType("bd09ll");
        option.setLocationNotify(true);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(myLocationListener);
        locationClient.start();
//        myOrientationListener = new MyOrientationListener(getActivity().getApplicationContext());
//        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
//            @Override
//            public void onOrientationChanged(float x) {
//                mCurrentDir = x;
//                int yushu = (int) (x % 45);
//                int chushu = (int) (x / 45);
//                switch ((int) (x / 45)) {
//                    case 0: {
//                        textView5.setText(new StringBuilder().append("方向：").append("北偏东").append((int) (x % 45)).append("°").toString());
//                        Log.d("directionIs", (int) (x / 45)+ "北偏东" + (int) (x % 45) + "°");
//                        break;
//                    }
//                    case 1: {
//                        textView5.setText(new StringBuilder().append("方向：").append("东偏北").append((int) (x % 45)).append("°").toString());
//                        Log.d("directionIs", (int) (x / 45)+ "东偏北" + (int) (x % 45) + "°");
//                        break;
//                    }
//                    case 2: {
//                        textView5.setText(new StringBuilder().append("方向：").append("东偏南").append((int) (x % 45)).append("°").toString());
//                        Log.d("directionIs", (int) (x / 45)+ "东偏南" + (int) (x % 45) + "°");
//                        break;
//                    }
//                    case 3: {
//                        textView5.setText(new StringBuilder().append("方向：").append("南偏东").append((int) (x % 45)).append("°").toString());
//                        Log.d("directionIs", (int) (x / 45)+ "南偏东" + (int) (x % 45) + "°");
//                        break;
//                    }
//                    case 4: {
//                        textView5.setText(new StringBuilder().append("方向：").append("南偏西").append((int) (x % 45)).append("°").toString());
//                        Log.d("directionIs", (int) (x / 45)+ "南偏西" + (int) (x % 45) + "°");
//                        break;
//                    }
//                    case 5: {
//                        textView5.setText(new StringBuilder().append("方向：").append("西偏南").append((int) (x % 45)).append("°").toString());
//                        Log.d("directionIs", (int) (x / 45)+ "西偏南" + (int) (x % 45) + "°");
//                        break;
//                    }
//                    case 6: {
//                        textView5.setText(new StringBuilder().append("方向：").append("西偏北").append((int) (x % 45)).append("°").toString());
//                        Log.d("directionIs", (int) (x / 45)+ "西偏北" + (int) (x % 45) + "°");
//                        break;
//                    }
//                    case 7: {
//                        textView5.setText(new StringBuilder().append("方向：").append("北偏西").append((int) (x % 45)).append("°").toString());
//                        Log.d("directionIs", (int) (x / 45)+ "北偏西" + (int) (x % 45) + "°");
//                        break;
//                    }
//                }
//            }
//        });
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
//    private void testJson() {
//        Other_loc other_loc = new Other_loc(username, password);
//        String json = JSONUtils.sendJson(other_loc);
//        Log.d("zw", "testJson: " + json);
//        final String[] latitude = new String[1];
//        final String[] lontitude = new String[1];
//        String body = "{\"data\":{\"position\":[{\"posTime\":\"2021-12-29 16:43:28\",\"lngDir\":1,\"lng\":\"129.456\",\"latDir\":1,\"deviceId\":\"102839\",\"lat\":\"16.321\",\"speed\":\"10.56\"}]},\"rtnMsg\":\"成功\",\"time\":1642748476622,\"rtnCode\":\"0\"}";
//        recOtherPositions pos = JSONUtils.receiveLocJSON(body);
//        latitude[0] = pos.getData().getPosition().get(0).getLat();
//        lontitude[0] = pos.getData().getPosition().get(0).getLng();
//        Log.d("zw", "testJson: " + latitude[0]);
//        Log.d("zw", "testJson: " + lontitude[0]);
//
//    }

    private void transToBD() {
        LatLng point = new LatLng(Double.parseDouble(lonAndLat.get(0)),Double.parseDouble(lonAndLat.get(1)));
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(point);
        LatLng desLatlon = converter.convert();
        latitude = desLatlon.latitude;
        lontitude = desLatlon.longitude;

//        Log.d("zw", "onCreateView: " + String.valueOf(latitude));
//        Log.d("zw", "onCreateView: " + String.valueOf(lontitude));
    }


    /**
     * 定时器处理事件，每10秒处理一次事件，状态1 和 0 表示有无网络
     * 定时10秒获取一次其他人（选中）的位置
     * 由于显示位置的函数中首先将地图上的所有的marker清除，所以再次显示位置的时候，需要重新将所有的marker加载
     * 其中无网络需要将信息存入到一个消息队列中（我理解成文件）
     */
    private void handlermyloc() {
        handlermyloc = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 0: {
                        break;
                    }
                    case 1: {
//                        locationClient.stop();
//                        myLocationListener = new MyLocationListener();
//                        if (locationClient == null)
//                        {
//                            locationClient = new LocationClient(getActivity().getApplicationContext());
//                        }
//                        LocationClientOption option = new LocationClientOption();
//                        option.setOpenGps(true);
//                        option.setIsNeedAddress(true);
//                        option.setNeedDeviceDirect(true);
//                        option.setCoorType("bd09ll");
//                        locationClient.setLocOption(option);
//                        locationClient.registerLocationListener(myLocationListener);
//                        locationClient.start();


//                        Log.d("zw", "onCreateView: 重新定位时的一些位置信息" + lonAndLat.toString());
                        try {
//                            lonAndLat = loc(getActivity().getApplicationContext());
//                            transToBD();
                            lonAndLat = loc2();
                            show_info_text(lonAndLat);
                            latitude = Double.parseDouble(lonAndLat.get(0));
                            lontitude = Double.parseDouble(lonAndLat.get(1));
                        }catch (Exception e){
                            Log.d("zw", "onCreateView: 位置信息捕获失败");
                            e.printStackTrace();
//                            lonAndLat = locUseOtherWay(getActivity().getApplicationContext());
                            lonAndLat = loc2();
                            show_info_text(lonAndLat);
                            latitude = Double.parseDouble(lonAndLat.get(0));
                            lontitude = Double.parseDouble(lonAndLat.get(1));

//                            transToBD();
                        }finally {
                            show_my_loc(String.valueOf(latitude), String.valueOf(lontitude), mCurrentDir);
                        }
                        show_info_text(lonAndLat);
                        break;
                    }
                }
            }
        };
    }


    /**
     * timInit函数作为计时器使用，
     * 其中message.what中赋值的是网络状态，分为有网络和无网络情况
     */

    private void timInit() {
        timer = new Timer();
        locFresh = new TimerTask() {
            @Override
            public void run() {
                Message messageMyloc = new Message();
                messageMyloc.what = 1;
                handlermyloc.sendMessage(messageMyloc);
            }
        };
        timer.schedule(locFresh, 0, 2*1000);
    }


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
//                Log.d("zw", "loc: 此时的provider为空");
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

    /**
     * 使用百度定位实现的
     * @return
     */
    public List<String> loc2() {
        List<String> list = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        //添加地图位置监听
        list = myLocationListener.getLatLng();
        try {
            list2 = loc(getActivity().getApplicationContext());
            if (list2.get(2) != null)
            {
                list.set(2, list2.get(2));
                list.set(3, list2.get(3));
            }
//            list.set(4, list2.get(4));
        }catch (Exception e){e.printStackTrace();}
        Log.d("loc2函数返回list结果", "loc2: "+list);
        return list;
    }



    /**不论有无网络，都可以根据gps发送定位，但是gps格式的位置需要改成百度上的坐标系
     *
     */
    private void show_my_loc(String lat, String lon, float dir) {
        int mXDirection;

        if (lat.equals("0")&&lon.equals("0"))
        {
            Log.d("zw", "show_my_loc: 此时的坐标为0，0");
        }else{
            locDataBuilder = new MyLocationData.Builder()
                    .accuracy(30)
                    .latitude(Double.parseDouble(lat))
                    .longitude(Double.parseDouble(lon))
                    .direction(dir);


            MyLocationConfiguration configuration = new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL,
                    true,
                    null,
                    0xAAFFFF88,
                    0xAA00FF00);
            // 在定义了以上属性之后，通过如下方法来设置生效：
            mMap.setMyLocationConfiguration(configuration);

            MyLocationData myLocData = locDataBuilder.build();
            mMap.setMyLocationData(myLocData);
//            Log.d("传感器", "show_my_loc: "+myLocData.toString());

//            LatLng latLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
//            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
////
//            mMap.animateMapStatus(mapStatusUpdate);
//
            if (ifFirst) {
                LatLng ll = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon) );
//            LatLng ll = new LatLng(myLocData.longitude, myLocData.latitude);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll);
                builder.zoom(14.0f);    // 放大为14层级
                mMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                //放大层级
                n = n+1;
                if(n==2){
                    ifFirst = false;
                }
            }

            //初始化方位角 由底层传感器获得
//        iniMyLocMap();
        }
    }

    /**网络状态下的辅助信息栏的显示
     *先根据经纬度查询区域编码，根据区域编码查询天气，包含天气类型，温度
     *
     */
    private void show_info_text(List<String> infoList) {
        java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00");
        java.text.DecimalFormat   df2   =new   java.text.DecimalFormat("#.0");
        java.text.DecimalFormat   df3   =new   java.text.DecimalFormat("#");
        latitude = Double.parseDouble(infoList.get(0));
        lontitude = Double.parseDouble(infoList.get(1));
        latitude = Double.parseDouble(df.format(latitude));
        lontitude = Double.parseDouble(df.format(lontitude));
        altitude = Double.parseDouble(infoList.get(2));
        speed = Double.parseDouble(infoList.get(3));
        textView2.setText(new StringBuilder().append("E").append(lontitude).append("°  ").append("N").append(latitude).append("°").toString());
        textView3.setText(new StringBuilder().append("海拔：").append(df2.format(altitude)).append("米").toString());
        textView4.setText(new StringBuilder().append("速度：").append(df3.format(speed)).append("m/s").toString());
        district = myLocationListener.getDistrict();
//        Log.d("zw", "show_info_text: 此时的区域为" + district);
        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put("city", district);
//        Log.d("zw", "show_info_text: 此时的所在区域是：" + district);
        try {
            if (!district.isEmpty()) {
                handlerMyweatherIni();
                threadForWeather = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpUtils.getInstance(getActivity().getApplicationContext()).get("http://wthrcdn.etouch.cn/weather_mini",hm,new OkHttpUtils.MyCallback() {
                            @Override
                            public void success(Response response) throws IOException {
                                JSONObject object = JSON.parseObject(response.body().string());
                                JSONObject result = object.getJSONObject("data");
//                    Log.d("查询天气", "success: 结果" + object);
                                JSONArray forecast = result.getJSONArray("forecast");
                                JSONObject today = forecast.getJSONObject(0) ;
//                    Log.d("今日天气", "success: "+ today);
                                String high = today.getString("high");
                                String low = today.getString("low");
                                weatherType = today.getString("type");
                                high = high.substring(2,high.length()-1);
                                low = low.substring(2,low.length()-1);
                                weatherTemperature = new StringBuilder().append(weatherType).append("  "+low).append("~").append(high).append("℃").toString();
                                Message message = new Message();
                                message.what = 1;
                                handlerMyweather.sendMessage(message);
//                    Log.d("天气温度", "success: "+ weatherTemperature);
                            }
                            @Override
                            public void failed(IOException e) {
                                Log.d("getmsg", e.getMessage());
                            }
                        });
                    }
                });
                threadForWeather.start();
        }}catch (Exception exception){
            exception.printStackTrace();
            Log.d("zw", "show_info_text: 此时获取天气失败");
        }

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
//        mMap.clear(); //清除地图所有标记
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
//        myOrientationListener.start();
        Log.d("zw", "onStart: Fragment开始实现");
    }

    @Override
    public void onStop() {
        super.onStop();
//        myOrientationListener.stop();

    }

    /**
     * 这里是对应前面switch时使用的hide和show
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            Log.d("zw", "onResume: Fragment继续呈现");
            selfPos = (starPos) getArguments().getSerializable("selfPos");
            delete = getArguments().getInt("delete");
            if(selfPos != null){
                if (delete == 0){
                    Log.d("zw", "onResume: 显示回传的位置");
                    show_selfbuild_loc(selfPos,delete);
                }else {
                    Log.d("zw", "onHiddenChanged: 删除在PosManager中删除的marker");
                    delete_selfbuild_loc(selfPos);
                }
            }
            selfPos = null;
            getArguments().putSerializable("selfPos", selfPos);


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

    }

    /**
     * 显示从PosManagerFragment处返回的点
     * @param selfPos
     */
    private void show_selfbuild_loc(starPos selfPos, int delete) {
        double latitude = Double.parseDouble(selfPos.getLatitude());
        double lontitude = Double.parseDouble(selfPos.getLontitude());

        LatLng point = new LatLng(latitude, lontitude);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(point);
        mMap.animateMapStatus(mapStatusUpdate);

        BitmapDescriptor bitmap;
        Bundle bundle = new Bundle();
        bundle.putString("deviceID", selfPos.getUid());
        bundle.putString("Text", selfPos.getText());
        switch ((int) selfPos.getLegend()){
            case 0: {
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.black);
                break;
            }
            case 1:{
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.red);
                break;
            }
            case 2:{
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.deepblue);
                break;
            }
            case 3:{
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.yellow);
            } default:{
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_mark);
                break;
            }
        }

        markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.icon(bitmap);
        markerOptions.zIndex(17);   //层级
        markerOptions.extraInfo(bundle);
        markerOptions.scaleX(0.1f);
        markerOptions.scaleY(0.1f);


        LatLngBounds build = new LatLngBounds.Builder().include(point).build();
        Log.d("z", "delete_selfbuild_loc: 此时的build是" + build);
        List<Marker> markersInBounds = mMap.getMarkersInBounds(build);
        Log.d("zw", "delete_selfbuild_loc: 此时的marker有" + markersInBounds);
        if (markersInBounds!=null){
            Log.d("zw", "show_selfbuild_loc: 重复添加marker了，所以在这里不添加");
        }else{
            Marker markerFromPosManager = (Marker) mMap.addOverlay(markerOptions);
        }
        Log.d("zw", "show_selfbuild_loc: 设置回传的位置成功");
    }

    /**
     * @return null
     * @Title
     * @parameter
     * @Description 删除在PosManager列表中删除的点
     * @author chx
     * @data 2022/2/28/028  15:53
     */
    private void delete_selfbuild_loc(starPos selfPos) {
        double latitude = Double.parseDouble(selfPos.getLatitude());   //原坐标点
        double lontitude = Double.parseDouble(selfPos.getLontitude());
        LatLng latLng = new LatLng(latitude, lontitude);
        LatLngBounds build = new LatLngBounds.Builder().include(latLng).build();
        Log.d("z", "delete_selfbuild_loc: 此时的build是" + build);
        List<Marker> markersInBounds = mMap.getMarkersInBounds(build);
        Log.d("zw", "delete_selfbuild_loc: 此时的marker有" + markersInBounds);
        if (markersInBounds!=null){
            markersInBounds.get(0).remove();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        locationClient.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dingwei: {
                show_my_loc(String.valueOf(latitude), String.valueOf(lontitude), mCurrentDir);
                testRequest();
//                sendRequestForLoc(username);
                break;
            }
            case R.id.people:{
                handlerOtherloc = new Handler(){
                    @SuppressLint("HandlerLeak")
                    public void handleMessage(Message message) {
                        switch (message.what)
                        {
                            case 1:{
                                Intent intent = new Intent(getActivity(), other_loc.class);
                                intent.putExtra("token", token);
                                intent.putExtra("curToken", curToken);
                                intent.putExtra("status", bodyOtherLoc);
                                intent.putExtra("uid", uid);
                                intent.putExtra("pass", pass);
//                                Log.d("zw", "handleMessage: post亮哥的服务器得到的数据" + bodyOtherLoc);
//                                intent.putExtra()
                                startActivityForResult(intent, 1); //这里注意使用的是带有回调方式的，回调代码为1
                                break;
                            }
                            case 2:{
                                bodyOtherLoc = new String();
                                records = LitePal.where("uid = ?", uid).find(orgAndUidAndKey.class);
                                if(records.isEmpty()){
                                    org = new String();
                                    curToken = "f9bddcacc678ea185bf8158d90087fbc";
                                    Log.d("zw", "failed: 这个账号原本没有登陆，数据库查无此人信息，返回的所有东西都将是空");
                                }else{
                                    record = records.get(0);
                                    org = record.getOrg();
                                }
                                Intent intent = new Intent(getActivity(), other_loc.class);
                                token = new String(); //这地方先写了，没有网络的时候，token为空
                                intent.putExtra("token", token);
                                intent.putExtra("curToken", curToken);
                                intent.putExtra("status", bodyOtherLoc);
                                intent.putExtra("uid", uid);
                                intent.putExtra("pass", pass);
                                intent.putExtra("org", org);
                                startActivityForResult(intent, 1);
                                break;
                            }
                            default:{break;}
                        }

                    }
                    };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("zw", "run: 开始进行网络请求");
                        OkHttpUtils.getInstance(getActivity().getApplicationContext()).
                                get("http://120.27.249.235:8081/getStatus1",
                                        new OkHttpUtils.MyCallback() {
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
//                                没有网络的时候，拿取数据库里面的字段，传入到other_loc里面，使得在无网络的情况下也能正常显示组织
//                                此时的bodyOtherLoc必须是空
                                Log.d("zw", "failed: 网络请求返回参数失败");
                                Message message = new Message();
                                message.what = 2;
                                handlerOtherloc.sendMessage(message);

                            }
                        });
                    }
                }).start();
                break;
            }
            case R.id.trace:{
                Log.d("zw", "onClick: 点击轨迹按钮");
                handlerTrace = new Handler(){
                    @SuppressLint("HandlerLeak")
                    public void handleMessage(Message message) {
                        switch (message.what)
                        {
                            case 1:{
                                Intent intent = new Intent(getActivity(), trace_activity.class);
                                intent.putExtra("token", token);
                                intent.putExtra("curToken", curToken);
                                intent.putExtra("status", bodyOtherLoc);
                                intent.putExtra("uid", uid);
                                intent.putExtra("pass", pass);
//                                Log.d("zw", "handleMessage: post亮哥的服务器得到的数据" + bodyOtherLoc);
//                                intent.putExtra()
                                startActivityForResult(intent, 2); //这里注意使用的是带有回调方式的，回调代码为1
                                break;
                            }
                            case 2:{
                                bodyOtherLoc = new String();
                                records = LitePal.where("uid = ?", uid).find(orgAndUidAndKey.class);
                                if(records.isEmpty()){
                                    org = new String();
                                    curToken = "f9bddcacc678ea185bf8158d90087fbc";
                                    Log.d("zw", "failed: 这个账号原本没有登陆，数据库查无此人信息，返回的所有东西都将是空");
                                }else{
                                    record = records.get(0);
                                    org = record.getOrg();
                                }
                                Intent intent = new Intent(getActivity(), trace_activity.class);
                                token = new String(); //这地方先写了，没有网络的时候，token为空
                                intent.putExtra("token", token);
                                intent.putExtra("curToken", curToken);
                                intent.putExtra("status", bodyOtherLoc);//
                                intent.putExtra("uid", uid);
                                intent.putExtra("pass", pass);
                                intent.putExtra("org", org);
                                startActivityForResult(intent, 2);
                                break;
                            }
                            default:{break;}
                        }
                    }
                };


                threadForTrace = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("zw", "run: 开始进行状态请求");
                        OkHttpUtils.getInstance(getActivity().getApplicationContext()).
                                get("http://120.27.249.235:8081/getStatus1",
                                        new OkHttpUtils.MyCallback() {
                            @Override
                            public void success(Response response) throws IOException {
                                bodyOtherLoc = response.body().string(); //状态信息
                                Log.d("zw", "success: post亮哥的服务器得到的数据" + bodyOtherLoc);
                                Message message = new Message();
                                message.what = 1;
                                handlerTrace.sendMessage(message);
                            }

                            @Override
                            public void failed(IOException e) {
//                                没有网络的时候，拿取数据库里面的字段，传入到other_loc里面，使得在无网络的情况下也能正常显示组织
//                                此时的bodyOtherLoc必须是空
                                Log.d("zw", "failed: 网络请求返回参数失败");
                                Message message = new Message();
                                message.what = 2;
                                handlerTrace.sendMessage(message);
                            }
                        });
                    }
                });
                threadForTrace.start();
                break;
            }
            default:break;
        }
    }

    private void testRequest() {
        show_other_loc("30.518848","114.350055");
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
//                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
//
//                mMap.animateMapStatus(mapStatusUpdate);

//                mMap.clear();

                markerOptions = new MarkerOptions();

                markerOptions.position(latLng);

                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark);

                markerOptions.icon(bitmapDescriptor);

                markerOptions.zIndex(14);   //层级

//                Bundle bundle = new Bundle();
//                bundle.putString("deviceID", "13886415060");
/**
 * 816到821设置额外信息
 */
//                markerOptions.extraInfo(bundle);

                Marker marker = (Marker) mMap.addOverlay(markerOptions);

                LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
                View view = inflater.inflate(R.layout.text_item, null);

                TextView tv_mapLongClicklatitude = (TextView) view.findViewById(R.id.tv_mapLongClicklatitude);
                TextView tv_mapLongClicklongtitude = (TextView) view.findViewById(R.id.tv_mapLongClicklongtitude);
                Button btnCancel = view.findViewById(R.id.btn_cancel);
                Button btnCommit = view.findViewById(R.id.btn_search);
                EditText et_text = view.findViewById(R.id.et_text);
                EditText et_locInfo = view.findViewById(R.id.et_locInfo);
                Spinner locChoose = view.findViewById(R.id.locChoose_Spinner);
                Spinner legendChoose = view.findViewById(R.id.legendChoose_Spinner);
//                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity().getApplicationContext(),
//                        android.R.layout.simple_spinner_item ,ss);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        marker.remove();
                        mMap.hideInfoWindow();
                    }
                });

                //提交按钮
                btnCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //根据选择切换标识颜色
                        switch ((int) legendChoose.getSelectedItemId()){
                            case 0: {
                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.black);
                                marker.setIcon(bitmapDescriptor);
                                marker.setScale((float) 0.1);
                                break;
                            }
                            case 1:{
                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.red);
                                marker.setIcon(bitmapDescriptor);
                                marker.setScale((float) 0.1);
                                break;
                            }
                            case 2:{
                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.deepblue);
                                marker.setIcon(bitmapDescriptor);
                                marker.setScale((float) 0.1);
                                break;
                            }
                            case 3:{
                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.yellow);
                                marker.setIcon(bitmapDescriptor);
                                marker.setScale((float) 0.1);
                            } default:break;
                        }


                        posRecords = LitePal.where("latitude=? and lontitude=?",
                                String.valueOf(latLng.latitude), String.valueOf(latLng.longitude)).find(Pos.class);

                        //写本地库
                        if (posRecords.isEmpty()){
                            posRecord = new Pos();
                            posRecord.setLatitude(String.valueOf(latLng.latitude));
                            posRecord.setLontitude(String.valueOf(latLng.longitude));
                            posRecord.setStatus("离线");
                            posRecord.setUid(uid);
                            if (et_text.getText().toString().isEmpty()){
                                posRecord.setText("没有设置");
                            }else{
                            posRecord.setText(et_text.getText().toString());
                            }
                            if(locChoose.getSelectedItem().toString().isEmpty())
                            {
//                                posRecord.setTag("99");
                                posRecord.setTag("没有设置");
                            }else{
//                                posRecord.setTag(String.valueOf(locChoose.getSelectedItemId()));
                                posRecord.setTag(String.valueOf(locChoose.getSelectedItem().toString()));
                            }
                            if (et_locInfo.getText().toString().isEmpty()){
                                posRecord.setLocInfo("无");
                            }else {
                                posRecord.setLocInfo(et_locInfo.getText().toString());
                            }
//                            posRecord.setLocInfo("已经写死，暂不设置");
                            posRecord.setLegend((int) legendChoose.getSelectedItemId());
                            posRecord.save();
                        }else{
                            posRecord = posRecords.get(0);
                            if (et_text.getText().toString().isEmpty()){
                                posRecord.setText("没有设置");
                            }else{
                                posRecord.setText(et_text.getText().toString());
                            }
                            if(locChoose.getSelectedItem().toString().isEmpty())
                            {
//                                posRecord.setTag("99");
                                posRecord.setTag("没有设置");
                            }else{
//                                posRecord.setTag(String.valueOf(locChoose.getSelectedItemId()));
                                posRecord.setTag(String.valueOf(locChoose.getSelectedItem().toString()));
                            }
                            posRecord.setLegend((int) legendChoose.getSelectedItemId());
                            posRecord.save();
                        }
                        //写库结束


                        //新线程上传自建点
                        threadForUploadSelfLoc = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    selfPosJson selfPosJson = new selfPosJson(uid, posRecord.getLontitude(),
                                            posRecord.getLatitude(), (int) legendChoose.getSelectedItemId(), posRecord.getText(), posRecord.getLocInfo(), 0, posRecord.getTag());
                                    String json = JSONUtils.sendJson(selfPosJson);
                                    OkHttpUtils.getInstance(getActivity().getApplicationContext()).post("http://120.27.249.235:8081/addPositionOfSelf", json, new OkHttpUtils.MyCallback() {
                                        @Override
                                        public void success(Response response) throws IOException {
                                            //将数据库发送状态修改成已发送
                                            Log.d("zw", "success: 发送自建点成功，还没到发布那一步" + json);
                                        }

                                        @Override
                                        public void failed(IOException e) {
                                            Log.d("zw", "failed: 发送自建点连接网络就失败了");
                                        }
                                    });
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Log.d("zw", "run: 上传自建点失败");
                                }
                            }
                        });
                        threadForUploadSelfLoc.start();

                        mMap.hideInfoWindow();
                    }
                });

                DecimalFormat df = new DecimalFormat("#0.0000");


                tv_mapLongClicklongtitude.setText(df.format(latLng.latitude));
                tv_mapLongClicklatitude.setText(df.format(latLng.longitude));
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
                Log.d("zw", "onMarkerClick: 点击marker成功，开始执行响应事件");
                String deviceID;
                LatLng latlon = marker.getPosition();
                try {
                    Bundle info = marker.getExtraInfo();
                    deviceID = info.getString("deviceID");
                }catch (Exception e){
                    e.printStackTrace();
                    deviceID = uid;
                }
                Log.d("zw", "onMarkerClick: marker响应事件中的deviceID" + deviceID);
                LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
                View view = inflater.inflate(R.layout.item_markerclick, null);
                TextView tv_markerClickLatitude = (TextView) view.findViewById(R.id.tv_markerClickLatitude);
                TextView tv_markerClickLontitude = (TextView) view.findViewById(R.id.tv_markerClickLontitude);
                TextView tv_markerClickID = (TextView) view.findViewById(R.id.tv_markerClickID);
                Button btnDelete = view.findViewById(R.id.btn_markerclickDelete);
                Button btnExit = view.findViewById(R.id.btn_makerclickExit);
                String finalDeviceID = deviceID;
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("zw", "onClick: 真的取消标记");
                        //取消标记会将对应全局变量里面的团队位置ID记录删除
                        try {
                            List<String> otherLocIDRecord = application.getOtherLocIDRecord();
                            int size = otherLocIDRecord.size();
                            int No;
                            for (int i=0;i<size;i++){
                                if (otherLocIDRecord.get(i).equals(finalDeviceID)){
                                    otherLocIDRecord.remove(i);
                                    Log.d("zw", "onClick: 此时的otherlocid是" + otherLocIDRecord);
                                    application.setOtherLocIDRecord(otherLocIDRecord);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        marker.remove();
                        mMap.hideInfoWindow();
                    }
                });
                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.hideInfoWindow();
                    }
                });
                DecimalFormat df = new DecimalFormat("#0.000");
                String nickName = id2name.transform(application.dbHelper.getWritableDatabase(), application.getUserID(), deviceID);
                tv_markerClickLatitude.setText(df.format(latlon.latitude));
                tv_markerClickLontitude.setText(df.format(latlon.longitude));
                tv_markerClickID.setText(nickName);
                InfoWindow infowindow = new InfoWindow(view, latlon, +47);
                mMap.showInfoWindow(infowindow);
                return true;
            }
        };

        listenerTrace = new BaiduMap.OnPolylineClickListener() {
            @Override
            public boolean onPolylineClick(Polyline polyline) {
                Log.d("zw", "onPolylineClick: 轨迹选中响应");
                List<LatLng> points = polyline.getPoints();
                LatLng point = points.get(0);
                String string = polyline.getExtraInfo().getString("traceID");
                String nickName = id2name.transform(application.dbHelper.getWritableDatabase(), application.getUserID(), string);
                LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
                View view = inflater.inflate(R.layout.item_traceclick, null);
                TextView tv_traceClickID = view.findViewById(R.id.tv_traceClickID);
                Button btn_traceClickExit = view.findViewById(R.id.btn_traceClickExit);
                Button btn_traceClickCancel = view.findViewById(R.id.btn_traceClickCancel);
                tv_traceClickID.setText(nickName);
                btn_traceClickExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.hideInfoWindow();
                    }
                });

                btn_traceClickCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        polyline.remove();
                        mMap.hideInfoWindow();
                    }
                });
                InfoWindow infoWindow = new InfoWindow(view, point, -47);
                mMap.showInfoWindow(infoWindow);
                return true;
            }
        };
        mMap.setOnMarkerClickListener(listenerMark);
        mMap.setOnPolylineClickListener(listenerTrace);
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

        myOrientationListener = new MyOrientationListener(getActivity());
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {

            @Override
            public void onOrientationChanged(float x) {
                Log.d("zw", "onOrientationChanged: 进入设置方位角");
                MyLocationConfiguration configuration = new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL,
                        true,
                        null,
                        0xAAFFFF88,
                        0xAA00FF00);
                // 在定义了以上属性之后，通过如下方法来设置生效：
                mMap.setMyLocationConfiguration(configuration);
                locDataBuilder.direction(360 - x);
                MyLocationData myLocData = locDataBuilder.build();
                mMap.setMyLocationData(myLocData);

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

    /**
     *
     * @param requestCode 请求代码是1表示是进入other_loc，请求代码是2表示进入trace_activity
     * @param resultCode  返回代码是0表示异常情况，返回代码是1或者2表示正常返回
     * @param data 在other_loc当中data数据包含的是请求列表
     *             在trace_activity当中data数据表示的是一个String
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Log.d("zw", "onActivityResult: 回调到HomeFragment：" + requestCode);
            Log.d("zw", "onActivityResult: 现在的resultcode是：" + resultCode);
            if (resultCode == 2)
            {
                Exception e = new Exception("请求名单为空异常");
                try {
                    handleRecOtherloc();
                    List<String> list;
                    ArrayList<String> idlist = data.getStringArrayListExtra("pos");
                    if (idlist.isEmpty())throw e;
                    list = new ArrayList<String>(idlist);
                    Log.d("zw", "testBDRequest: 需要获取位置的设备是：" + list);
                    posBD posBD = new posBD(list);
                    String json = JSONUtils.sendJson(posBD);
                    Log.d("zw", "testBDRequest: 准备发送给福大的json格式是：" + json);
                    try {
                        Thread threadPos = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpUtils.getInstance(getActivity().getApplicationContext()).postBD("http://119.3.130.87:50099/whbdApi/device/pos/getCurrent",
                                        json,
                                        new OkHttpUtils.MyCallback() {
                                            @Override
                                            public void success(Response response) throws IOException {
                                                String rec = response.body().string();
                                                Log.d("zw", "success: 访问福大北斗的位置信息得到的结果："+ rec);
                                                posFromBD recPos = receivePosFromBDJson(rec);
                                                posLists = recPos.getData().getPosition();
//                                    Log.d("zw", "success: 访问福大北斗的所有位置是" + posLists.toString());
                                                //获取位置后，显示出marker，带有个人信息的marker
                                                Message message = new Message();
                                                message.what = 1;
                                                handlerecOtherloc.sendMessage(message);
                                            }

                                            @Override
                                            public void failed(IOException e) {
                                                Log.d("zw", "failed: 访问福大北斗获取位置信息失败");
                                            }
                                        }, curToken);
                            }
                        });
                        threadPos.start();
                    }catch (Exception exception){
                        Log.d("zw", "testBDRequest: 访问福大北斗位置信息线程崩溃");
                        exception.printStackTrace();
                    }
                }catch (Exception exception){
                    exception.printStackTrace();
                    Log.d("zw", "onActivityResult: 请求名单为空2");
                }
            }
            else if(resultCode == 0)
            {

            }
        }
        else if(requestCode == 2){
            if (resultCode == 1){
                handlerOtherTrace();
                tracerId = data.getStringExtra("tracerID");
                startTime = data.getLongExtra("start", 0);
                endTime = data.getLongExtra("end", System.currentTimeMillis());
                tracsPos tracsPos = new tracsPos(tracerId, startTime, endTime);//为什么是tracsPos，因为不小心将trace写成了tracs，嘿嘿嘿
                String json = JSONUtils.sendJson(tracsPos);
                try {
                    Thread threadTrace = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpUtils.getInstance(getActivity().getApplicationContext()).postBD("http://119.3.130.87:50099/whbdApi/device/pos/getList",
                                    json,
                                    new OkHttpUtils.MyCallback() {
                                        @Override
                                        public void success(Response response) throws IOException {
                                            String trace = response.body().string();
                                            JSONObject object = JSON.parseObject(trace);
                                            JSONObject result = object.getJSONObject("data");
                                            array = result.getJSONArray("position");
//                                            JSONObject position = array.getJSONObject(0);
                                            traceList = new ArrayList<LatLng>();
                                            int num = array.size()-1;
                                            for (int i=num;i>0;){
                                                JSONObject position = array.getJSONObject(i);
                                                String latTemp = position.getString("lat");
                                                String lngTmp = position.getString("lng");

                                                LatLng point = new LatLng(Double.parseDouble(lngTmp), Double.parseDouble(latTemp));
                                                CoordinateConverter converter = new CoordinateConverter();
                                                converter.from(CoordinateConverter.CoordType.GPS);
                                                converter.coord(point);
                                                LatLng desLatlon = converter.convert();
                                                Double DlonTmp = desLatlon.longitude;
                                                Double DlatTmp = desLatlon.latitude;

                                                traceList.add(new LatLng(DlatTmp, DlonTmp));
//                                                traceList.add(new LatLng(Double.parseDouble(lngTmp), Double.parseDouble(latTemp)));
                                                //lat和lng要反着设置，这是百度SDK的锅，打印出来又反过来了
                                                //就是设置是lng：30.400 + lat：115.26，打印的结果却是 lat：30.400 + lng：115.26：
                                                i=i-2;
                                            }
                                            Message message = new Message();
                                            message.what = 1;
                                            handlerOtherTracePos.sendMessage(message);
                                        }

                                        @Override
                                        public void failed(IOException e) {
                                            Log.d("zw", "failed: 访问福大北斗网络连接失败");
                                        }
                                    }, curToken);
                        }
                    });
                    threadTrace.start();
                }catch (Exception e){
                    Log.d("zw", "onActivityResult: 访问福大北斗获取历史位置失败");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理团队位置
     */
    private void handleRecOtherloc() {
        handlerecOtherloc = new Handler(){
            public void handleMessage(Message message){
                switch (message.what){
                    case 1:{
                        Log.d("zw", "handleMessage: 开始绘制其他人的位置点");
                        mMap.clear();
                        int num = posLists.size();
                        try {
                            for(int i = 0;i<num;i++){
                                posFromBD.Position pos = posLists.get(i);
                                String deviceID = pos.getDeviceId();
                                String lat = pos.getLat();
                                String lon = pos.getLng();

//                                LatLng point = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                                //百度sdk的问题，latlon是用lon和lat初始化，不是lat和lon
                                LatLng point = new LatLng(Double.parseDouble(lon), Double.parseDouble(lat));
                                CoordinateConverter converter = new CoordinateConverter();
                                converter.from(CoordinateConverter.CoordType.GPS);
                                converter.coord(point);
                                LatLng desLatlon = converter.convert();
                                Double Dlon = desLatlon.longitude;
                                //这句话其实就是获取那个第一个还是第二个数，也就是说 desLatlon.longitude表示获取latlon中的第二个，等价于lat
                                //下面等价于lon
                                Double Dlat = desLatlon.latitude;
//                                LatLng latlon = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                                //这里Dlat是lng，Dlon是lat
                                LatLng latlon = new LatLng(Dlat, Dlon);
                                markerOptions = new MarkerOptions();

                                markerOptions.position(latlon);
                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark);
                                markerOptions.icon(bitmapDescriptor);
                                markerOptions.zIndex(17);   //层级
                                Bundle bundle = new Bundle();
                                bundle.putString("deviceID", deviceID);
                                markerOptions.extraInfo(bundle);

                                Marker marker = (Marker) mMap.addOverlay(markerOptions);
                            }
                        }catch (Exception e){
                            Log.d("zw", "handleMessage: 绘制其他人位置报错");
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:break;
                }
            }
        };
    }

    /**
     * 处理轨迹
     * 根据福大返回的位置列表，绘制图形
     */
    private void handlerOtherTrace() {
        handlerOtherTracePos = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message message){
                switch (message.what){
                    case 1:{
                        if (mPolyline!=null)
                        {
                            mPolyline.remove();
                        }
                        Log.d("zw", "handleMessage: 此时获取的轨迹列表大小" + traceList.toString());
                        mMap.clear();
                        OverlayOptions mOverlayOptions = new PolylineOptions()
                                .points(traceList)
                                .width(20)
                                .color(0xFF000000)
                                .visible(true)
                                .points(traceList);
//在地图上绘制折线
//mPloyline 折线对象
                        mPolyline = mMap.addOverlay(mOverlayOptions);
                        Bundle bundle = new Bundle();
                        bundle.putString("traceID", tracerId);
                        mPolyline.setExtraInfo(bundle);
                        Log.d("zw", "handleMessage: 绘制的折线是否可见" + mPolyline.isVisible());
                        break;
                    }
                    default:{break;}
                }
            }
        };
    }

    /**
     *
     * @return null
     * @Title
     * @parameter null
     * @Description 初始化处理天气的handler
     * @author chx
     * @data 2022/2/27/027  10:32
     */
    private void handlerMyweatherIni() {
        handlerMyweather = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:{
                        textView1.setText(weatherTemperature);
                        break;
                    } default:break;
                }
            }
        };
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        mlastDir = mCurrentDir;
        mCurrentDir = degree;
        Log.d("zw", "onSensorChanged: 此时的角度为" + degree);
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(10);
        ra.setFillAfter(true);

        if (Math.abs(mCurrentDir - mlastDir) > 2)
        {
            setTextView(degree);
            compass.startAnimation(ra);
        }

        currentDegree = -degree;
    }

    private void setTextView(float x) {
                switch ((int) (x / 45)) {
                    case 0: {
                        textView5.setText(new StringBuilder().append("方向：").append("北偏东").append((int) (x % 45)).append("°").toString());
                        Log.d("directionIs", (int) (x / 45)+ "北偏东" + (int) (x % 45) + "°");
                        break;
                    }
                    case 1: {
                        textView5.setText(new StringBuilder().append("方向：").append("东偏北").append((int) (x % 45)).append("°").toString());
                        Log.d("directionIs", (int) (x / 45)+ "东偏北" + (int) (x % 45) + "°");
                        break;
                    }
                    case 2: {
                        textView5.setText(new StringBuilder().append("方向：").append("东偏南").append((int) (x % 45)).append("°").toString());
                        Log.d("directionIs", (int) (x / 45)+ "东偏南" + (int) (x % 45) + "°");
                        break;
                    }
                    case 3: {
                        textView5.setText(new StringBuilder().append("方向：").append("南偏东").append((int) (x % 45)).append("°").toString());
                        Log.d("directionIs", (int) (x / 45)+ "南偏东" + (int) (x % 45) + "°");
                        break;
                    }
                    case 4: {
                        textView5.setText(new StringBuilder().append("方向：").append("南偏西").append((int) (x % 45)).append("°").toString());
                        Log.d("directionIs", (int) (x / 45)+ "南偏西" + (int) (x % 45) + "°");
                        break;
                    }
                    case 5: {
                        textView5.setText(new StringBuilder().append("方向：").append("西偏南").append((int) (x % 45)).append("°").toString());
                        Log.d("directionIs", (int) (x / 45)+ "西偏南" + (int) (x % 45) + "°");
                        break;
                    }
                    case 6: {
                        textView5.setText(new StringBuilder().append("方向：").append("西偏北").append((int) (x % 45)).append("°").toString());
                        Log.d("directionIs", (int) (x / 45)+ "西偏北" + (int) (x % 45) + "°");
                        break;
                    }
                    case 7: {
                        textView5.setText(new StringBuilder().append("方向：").append("北偏西").append((int) (x % 45)).append("°").toString());
                        Log.d("directionIs", (int) (x / 45)+ "北偏西" + (int) (x % 45) + "°");
                        break;
                    }
                }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}

