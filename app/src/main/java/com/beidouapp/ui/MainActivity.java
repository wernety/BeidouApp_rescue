package com.beidouapp.ui;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.model.LatLng;
import com.beidouapp.R;
import com.beidouapp.background.MsgService;
import com.beidouapp.model.DataBase.DBHelper;
import com.beidouapp.model.DataBase.orgAndUidAndKey;
import com.beidouapp.model.User;
import com.beidouapp.model.User4Login;
import com.beidouapp.model.Group;
import com.beidouapp.model.messages.regist;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.MD5;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.utils.id2name;
import com.beidouapp.ui.fragment.HomeFragment;
import com.beidouapp.ui.fragment.MessageFragment;
import com.beidouapp.ui.fragment.PosManageFragment;
import com.beidouapp.ui.fragment.RelationFragment;
import com.beidouapp.ui.fragment.SettingsFragment;
import com.beidouapp.ui.fragment.starPos;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.beidouapp.model.utils.NetworkManager;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Response;

/**
 * ?????????
 * ??????4????????????
 * Home?????????
 * Message4Send???????????????
 * Contact????????????????????????
 * Settings?????????\??????
 */

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private static Context mContext;
    private MessageFragment messageFragment;
    private RelationFragment relationFragment;
    private SettingsFragment settingsFragment;
    private PosManageFragment posManageFragment;
    private String token;
    private Bundle bundle;
    private String loginId;
    private orgAndUidAndKey record;
    private List<orgAndUidAndKey> records;
    private String orgRecord;

    private MsgService msgService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msgService = ((MsgService.MsgBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            msgService = null;
        }
    };
    private long timestamp;
    private String curToken;
    private regist regist;
    private Intent intentservice;
    private DemoApplication application;
    private SQLiteDatabase writableDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        Log.d("zw", "onCreate: " + "??????");
        if(NetworkManager.isWIFI(this)||NetworkManager.isMobile(this)) {
            getToken();
//            curToken = "f9bddcacc678ea185bf8158d90087fbc";
            Log.d("zw", "onCreate:curToken " + curToken);
        }
        else
        {
            curToken = "f9bddcacc678ea185bf8158d90087fbc";
            initUserAndMsgNowang();
        }


        iniDbForRecord();
        initUser();
        initUI();
        initListener();
        StartMsgService();
        mContext = getContext();
    }



    private void initUser() {
        application.setOtherLocIDRecord(new ArrayList<String>());
        application.setFlag(true); //????????????????????????????????????
        loginId = application.getUserID();
        token = application.getToken();
        bundle = new Bundle();
        bundle.putString("curToken", curToken);
        bundle.putString("token", token);
        bundle.putString("loginId", loginId);
        bundle.putString("pass", application.getUserPass());  //???????????????
        initFriendGroupOrg();



        id2name.write2DB(writableDatabase,loginId,
                application.getIndexID(),
                loginId,
                application.getNickName(),
                "1");
    }

    private void initUI() {
        bottomNavigationView = findViewById(R.id.mainAct_bnv);
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        relationFragment = new RelationFragment();
        posManageFragment = new PosManageFragment();
        settingsFragment = new SettingsFragment();
        iniPosFragmentListener();
        switchFragment(homeFragment);
    }



    /**
     * LitePal?????????????????????????????????curtoken?????????????????????uid???password
     * ???????????????Sqlite?????? dbHelper
     */
    private void iniDbForRecord() {
        application = (DemoApplication) this.getApplicationContext();
        application.dbForRecord = Connector.getDatabase(); //????????????????????????????????????
        application.dbHelper = new DBHelper(this.getApplicationContext(), "chatRecord.db", null, 6);
        writableDatabase = application.dbHelper.getWritableDatabase();
    }

    //????????????????????????curToken??????
    private void initUserAndMsgNowang() {
        Log.d("zw", "initUserAndMsgNowang: ?????????curToken????????????" + curToken);

//        intentservice.putExtra("curToken", curToken);
    }


    public static Context getContext(){
        return mContext;
    }

    private void initListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.mainAct_bottom_home:
                        switchFragment(homeFragment);
                        break;
                    case R.id.mainAct_bottom_message:
                        switchFragment(messageFragment);
                        break;
                    case R.id.mainAct_bottom_relation:
                        switchFragment(relationFragment);
                        break;
                    case R.id.mainAct_bottom_posManage:
                        switchFragment(posManageFragment);
                        break;
                    case R.id.mainAct_bottom_settings:
                        switchFragment(settingsFragment);
                        break;
                }
                return true;
            }
        });
    }

    //Fragment?????????
    private Fragment lastFragment = null;
    private void switchFragment(Fragment currentFragment) {

        while (bundle.getString("curToken") == null)
        {
            Log.d("zw", "switchFragment: ??????fragment?????????bundle??????" + bundle.toString());
        }
        application.setCurToken(bundle.getString("curToken"));
        currentFragment.setArguments(bundle);
        Log.d("zw", "switchFragment: ??????fragment?????????bundle??????" + bundle.toString());
        fragmentTransaction = fragmentManager.beginTransaction();
        if (!currentFragment.isAdded()) {
            fragmentTransaction.add(R.id.mainAct_frag,currentFragment);
        }else {
            fragmentTransaction.show(currentFragment);
        }
        if (lastFragment !=null && lastFragment!= currentFragment){
            fragmentTransaction.hide(lastFragment);
        }
        lastFragment = currentFragment;
//        mTransaction.replace(R.id.frag1,currentFragment);
        fragmentTransaction.commit();
    }



    /**
     * ?????????????????????????????????PosManagerFragment?????????????????????Fragment???item?????????????????????MainActivity????????????????????????
     */
    private void iniPosFragmentListener() {
        posManageFragment.setBackToMainListener(new PosManageFragment.BackToMainListener() {
            @Override
            public void changeMap(starPos selfPos) {
                Log.d("zw", "changeMap: ?????????????????????");
                bundle.putSerializable("selfPos", selfPos);
                bundle.putInt("delete",0); //0?????????
                switchFragment(homeFragment);
                lastFragment = posManageFragment;
                bottomNavigationView.setSelectedItemId(R.id.mainAct_bottom_home);
//                bundle.putString();
            }

            @Override
            public void deleteMapMarker(starPos selfPos) {
                Log.d("zw", "deleteMapMarker: ??????marker????????????????????????");
                bundle.putSerializable("selfPos", selfPos);
                bundle.putInt("delete", 1); //delete???1??????????????????
                switchFragment(homeFragment);
                lastFragment = posManageFragment;
                bottomNavigationView.setSelectedItemId(R.id.mainAct_bottom_home);
            }


        });
    }

    private void StartMsgService(){
        intentservice = new Intent(MainActivity.this, MsgService.class);
        intentservice.putExtra("uid", loginId);
        bindService(intentservice, serviceConnection, BIND_AUTO_CREATE);
        startService(intentservice);
    }

    private void StopMsgService() {
        unbindService(serviceConnection);
        stopService(intentservice);
    }

    @Override
    protected void onResume() {
        super.onResume();
        }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
//        mSensorManager.unregisterListener(this);
    }

//    @Override
//    public void onSensorChanged(SensorEvent event) {
//
//        // get the angle around the z-axis rotated
//        float degree = Math.round(event.values[0]);
//        Log.d("????????????????????????", "onSensorChanged: "+Float.toString(degree) + " degrees");
//
////        // create a rotation animation (reverse turn degree degrees)
////        RotateAnimation ra = new RotateAnimation(
////                currentDegree,
////                -degree,
////                Animation.RELATIVE_TO_SELF, 0.5f,
////                Animation.RELATIVE_TO_SELF,
////                0.5f);
////
////        // how long the animation will take place
////        ra.setDuration(210);
////
////        // set the animation after the end of the reservation status
////        ra.setFillAfter(true);
////
////        // Start the animation
////        image.startAnimation(ra);
////        currentDegree = -degree;
//
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // not in use
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopMsgService();
    }

    /**
     * ???????????????????????????token
     * ??????????????????Sting???????????????
     */
    private void getToken() {
        timestamp = System.currentTimeMillis();

        generateToken(timestamp,this);


    }


    private void generateToken(long timestampTmp, Context context) {
        String appId = "AppId5358eeb9b2ee399d308b6b98e98fa5c6";
        // secrerKey ??????????????????
        String secretKey = "c68bec2934c5b7295a66ab707cc955fd1640314984233nhQ";
        //?????????
//        Long timestamp = System.currentTimeMillis();
        List<String> tokenParams = new ArrayList<>();
        tokenParams.add(appId);
        tokenParams.add(secretKey);
        tokenParams.add(timestampTmp + "");
        //????????????
        Collections.sort(tokenParams);
        //??????token ??????
        String tokenSignature = MD5.MD5Encode(tokenParams.toString());
        Log.d("zw", "main: " + tokenSignature);
        regist = new regist(appId, String.valueOf(timestamp), tokenSignature);
        String json = JSONUtils.sendJson(regist);
        Log.d("zw", "generate: " + json);
        Handler handler = new Handler() {
            //           @Override
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    curToken = message.getData().getString("curToken");
                    Log.d("zw", "handleMessage: ????????????curtoken???" + curToken);
                    bundle.putString("curToken", curToken);
                    Log.d("zw", "initUser: bundle?????????curtoken???"+ curToken);
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("zw", "run: ???????????????,context????????????");
                    OkHttpUtils.getInstance(context).post("http://119.3.130.87:50099/openApi/openAuthApi/generateToken", json, new OkHttpUtils.MyCallback() {
                        @Override
                        public void success(Response response) throws IOException {
                            Log.d("zw", "success: ??????????????????");
                            JSONObject object = JSON.parseObject(response.body().string());
                            Log.d("zw", "success: ?????????json??????" + object);
                            JSONObject data = object.getJSONObject("data");
                            String token1 = data.getString("token");
                            Log.d("zw", "success: " + token1);
                            bundle.putString("curToken", token1);
                            
                            Message message = new Message();
                            message.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("curToken", token1);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }

                        @Override
                        public void failed(IOException e) {
                            Log.d("zw", "failed: ??????????????????????????????");
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("zw", "run: ??????Token??????????????????");
                }
            }
        }).start();
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     */


    /**
     * ???????????????????????????????????????
     * ???????????????
     */
    private void initFriendGroupOrg() {
        try {
            OkHttpUtils.getInstance(MainActivity.this).get("http://139.196.122.222:8080/system/user/" + application.getIndexID(),
                    token, new OkHttpUtils.MyCallback() {
                        @Override
                        public void success(Response response) throws IOException {
                            String body = response.body().string();
                            JSONObject object = JSON.parseObject(body);
                            Log.d("zzzml", body);
                            int code = object.getInteger("code");
                            if (code == 200) {
                                JSONArray groupArray = (JSONArray) object.get("selfGroup");
                                List<Group> groups = (List<Group>) JSONArray.parseArray(groupArray.toString(), Group.class);
                                JSONArray friendArray = (JSONArray) object.get("friends");
                                List<User> friends = (List<User>) JSONArray.parseArray(friendArray.toString(), User.class);
                                Log.d("zzzml", friends.toString());
                                application.setFriendList(friends);
                                application.setGroupList(groups);

                                User friend;
                                int size = friends.size();
                                for (int i = 0; i < size; i++) {
                                    friend = friends.get(i);
                                    id2name.write2DB(writableDatabase,loginId,
                                            friend.getUserId(),
                                            friend.getUserName(),
                                            friend.getNickName(),
                                            "1");
                                }
                                Group group;
                                size = groups.size();
                                for (int i = 0; i < size; i++) {
                                    group = groups.get(i);
                                    id2name.write2DB(writableDatabase,loginId,
                                        "GROUP",
                                        group.getSelfGroupId(),
                                        group.getSelfGroupName(),
                                        "1");
                                }
                            }
                        }
                        @Override
                        public void failed(IOException e) {

                        }
                    });

            OkHttpUtils.getInstance(MainActivity.this).get("http://139.196.122.222:8080/system/dept/user",
                    application.getToken(), new OkHttpUtils.MyCallback() {
                        @Override
                        public void success(Response response) throws IOException {
                            orgRecord = response.body().string();
                            JSONObject object = JSON.parseObject(orgRecord);
                            int code = object.getInteger("code");
                            if (code == 200) {
                                OrgWriteToDb(orgRecord,curToken,application.getUserID(),application.getUserPass());
                                application.setOrg(orgRecord);
                            }
                        }
                        @Override
                        public void failed(IOException e) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    private void OrgWriteToDb(String orgRecord, String curToken, String uid, String pass) {
        records = LitePal.where("uid = ?", uid).find(orgAndUidAndKey.class);
        Log.d("zw", "writeToDb: ??????????????????" + records);
        if(records.isEmpty())
        {
            record = new orgAndUidAndKey();
            record.setOrg(orgRecord);  //??????????????????????????????????????????????????????????????????????????????response??????body???String
            record.setCurToken(curToken);
            record.setUid(uid);
            record.setPass(pass);
            record.save();
            Log.d("zw", "writeToDb: ??????????????????????????????????????????");
//            record.setPass(); //????????????????????????????????????????????????
        }else{
            //?????????????????????????????????????????????????????????
            record = records.get(0);    //????????????????????????
            record.setOrg(orgRecord);
            record.setCurToken(curToken);
            record.setPass(pass);
            record.save();
            Log.d("zw", "writeToDb: ?????????????????????" + record.getPass() + " ????????? " + record.getUid());
        }
    }
}