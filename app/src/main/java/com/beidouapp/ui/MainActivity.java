package com.beidouapp.ui;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.background.MsgService;
import com.beidouapp.model.User;
import com.beidouapp.model.User4Login;
import com.beidouapp.model.messages.regist;
import com.beidouapp.model.utils.GenerateTokenDemo;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.MD5;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.ui.fragment.HomeFragment;
import com.beidouapp.ui.fragment.MessageFragment;
import com.beidouapp.ui.fragment.RelationFragment;
import com.beidouapp.ui.fragment.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.beidouapp.model.utils.NetworkManager;
import com.beidouapp.model.messages.regist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Response;

/**
 * 主活动
 * 分为4个碎片：
 * Home：地图
 * Message4Send：消息列表
 * Contact：联系人以及组织
 * Settings：我的\管理
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
    private String token;
    private Bundle bundle;
    private User4Login user4Login;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Log.d("zw", "onCreate: " + "开始");
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
        initUser();
        initUI();
        initListener();
        StartMsgService();
        mContext = getContext();
    }

    private void initUserAndMsgNowang() {
        bundle.putString("curToken", curToken);
        intentservice.putExtra("curToken", curToken);
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
//                    case R.id.mainAct_bottom_posManage:
//                        switchFragment(posManageFragment);
//                        break;
                    case R.id.mainAct_bottom_settings:
                        switchFragment(settingsFragment);
                        break;
                }
                return true;
            }
        });
    }

    //Fragment切换器
    private Fragment lastFragment = null;
    private void switchFragment(Fragment currentFragment) {

        while (bundle.getString("curToken") == null)
        {
            Log.d("zw", "switchFragment: 设置fragment里面的bundle是：" + bundle.toString());
        }
        currentFragment.setArguments(bundle);
        Log.d("zw", "switchFragment: 设置fragment里面的bundle是：" + bundle.toString());
        fragmentTransaction = fragmentManager.beginTransaction();
        if (!currentFragment.isAdded()) {
            fragmentTransaction.add(R.id.mainAct_frag,currentFragment);
        }else {
            fragmentTransaction.show(currentFragment);
        }
        if (lastFragment !=null){
            fragmentTransaction.hide(lastFragment);
        }
        lastFragment = currentFragment;
//        mTransaction.replace(R.id.frag1,currentFragment);
        fragmentTransaction.commit();
    }

    private void initUser() {
        Intent intent = getIntent();
        user4Login = new User4Login(intent.getStringExtra("uid"),
                "upw");
        token = intent.getStringExtra("token");
        bundle = new Bundle();
        bundle.putString("token", token);
        bundle.putString("loginId", user4Login.getUsername());
    }

    private void initUI() {
        bottomNavigationView = findViewById(R.id.mainAct_bnv);
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        relationFragment = new RelationFragment();
        //posManageFragment = new PosManageFragment();
        settingsFragment = new SettingsFragment();
        switchFragment(homeFragment);
    }

    private void StartMsgService(){
        intentservice = new Intent(MainActivity.this, MsgService.class);
        intentservice.putExtra("uid", user4Login.getUsername());
//        Log.d("zw", "StartMsgService: 传入service里面的curToken为：" + curToken);
//        intentservice.putExtra("curToken", curToken);
//        while(intentservice.getStringExtra("curToken") == null)
//        {
//            Log.d("zw", "StartMsgService: 传入service里面的curToken为：" + curToken);
//        }
//        Log.d("zw", "StartMsgService: 传入service里面的curToken为：" + curToken);
        bindService(intentservice, serviceConnection, BIND_AUTO_CREATE);
        startService(intentservice);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    /**
     * 有网络的情况下获取token
     * 其实更想改成Sting返回的对象
     */
    private void getToken() {
        timestamp = System.currentTimeMillis();
//        String token = GenerateTokenDemo.generate(timestamp, this);
        // post福大获得token
        generateToken(timestamp,this);


    }

    private void generateToken(long timestampTmp, Context context) {
        String appId = "AppId5358eeb9b2ee399d308b6b98e98fa5c6";
        // secrerKey 授权码的密钥
        String secretKey = "c68bec2934c5b7295a66ab707cc955fd1640314984233nhQ";
        //时间戳
//        Long timestamp = System.currentTimeMillis();
        List<String> tokenParams = new ArrayList<>();
        tokenParams.add(appId);
        tokenParams.add(secretKey);
        tokenParams.add(timestampTmp + "");
        //自然排序
        Collections.sort(tokenParams);
        //生成token 签名
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
                    Log.d("zw", "handleMessage: 线程里的curtoken：" + curToken);
                    bundle.putString("curToken", curToken);
                    Log.d("zw", "initUser: bundle添加了curtoken："+ curToken);
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("zw", "run: 进入子线程,context传入成功");
                    OkHttpUtils.getInstance(context).post("http://119.3.130.87:50099/openApi/openAuthApi/generateToken", json, new OkHttpUtils.MyCallback() {
                        @Override
                        public void success(Response response) throws IOException {
                            Log.d("zw", "success: 成功传入福大");
//                            String body =response.body().toString();
//                            Log.d("zw", "success: " + response.toString());
//                            Log.d("zw", "success: " + response.body().toString());
//                            Log.d("zw", "success: " + response.message());
                            JSONObject object = JSON.parseObject(response.body().string());
                            Log.d("zw", "success: 福大的json对象" + object);
                            JSONObject data = object.getJSONObject("data");
                            String token1 = data.getString("token");
//                            registToken registToken = JSONUtils.receiveTokenJson(body);
//                            Log.d("zw", "success: " + registToken);
                            Log.d("zw", "success: " + token1);
                            bundle.putString("curToken", token1);
//                            String token2 = token1;
//                            Log.d("zw", "success: " + token1);
//                            intentservice.putExtra("curToken", token1);
//                            Log.d("zw", "success: 在子线程里面传入后，又得到的intenservice.putextra：" + intentservice.getStringExtra("curToken"));
                            
                            Message message = new Message();
                            message.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("curToken", token1);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }

                        @Override
                        public void failed(IOException e) {
                            Log.d("zw", "failed: 失败，但是能连接福大");
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("zw", "run: 获取Token时的线程炸了");
                }
            }
        }).start();
    }
}