package com.beidouapp.background;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.beidouapp.model.DataBase.recentMan;
import com.beidouapp.model.messages.HeartbeatMsg;
import com.beidouapp.model.messages.HeartbeatMsg.*;
import com.beidouapp.model.messages.Message4Receive;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.NetworkChangeReceiver;
import com.beidouapp.model.utils.NetworkManager;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.utils.state_request;
import com.beidouapp.ui.DemoApplication;
import com.beidouapp.ui.fragment.MyLocationListener;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


/**
 * 消息收发服务
 * 在此服务实现各类网络连接
 * WIFI/MOBILE:WebSocket
 * BLUETOOTH:
 * BEIDOU:
 */

public class MsgService extends Service implements NetworkChangeReceiver.NetStateChangeObserver {
    private NetworkManager networkManager;
    private int NetStatus;
    public Link msgLink;
    private String uid;
    private Timer timer;
    private TimerTask heartbeatMSG;
    private String curToken;
    private List<recentMan> manRecords;
    private recentMan manRecord;
    private DemoApplication application;
    private SQLiteDatabase writableDatabase;
    private Handler write2DBHandler;



    // 通过Binder来保持Activity和Service的通信
    public class MsgBinder extends Binder {
        public MsgService getService() {
            return MsgService.this;
        }
    }
    public MsgBinder binder = new MsgBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public MsgService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        initTimer();
        NetworkChangeReceiver.registerReceiver(this);
        NetworkChangeReceiver.registerObserver(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        application = (DemoApplication) this.getApplicationContext();
        writableDatabase = application.dbHelper.getWritableDatabase();
        uid = application.getUserID();
        curToken = application.getCurToken();
        initHandler();
        Log.d("zw", "onStartCommand:全局的curToken " + curToken);
        NetStatus = NetworkManager.getConnectivityStatus(MsgService.this);
        msgLink = new Link("ws://120.27.242.92:8080/chatWS/" + uid, NetStatus);
        msgLink.linkServer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        NetworkChangeReceiver.unRegisterObserver(this);
        NetworkChangeReceiver.unRegisterReceiver(this);
    }



    /**
     * 发送消息
     * 根据网络状态
     * @param message
     */
    public boolean sendMessage(String message){
        if (NetStatus == NetworkManager.TYPE_WIFI_MOBILE) {
            return msgLink.webSocket.send(message);
        }
        if (NetStatus == NetworkManager.TYPE_BLUETOOTH) {
            return msgLink.webSocket.send(message);
        }
        if (NetStatus == NetworkManager.TYPE_NOT_CONNECT) {
            return msgLink.webSocket.send(message);
        }
        return false;
    }



    /**
     * 连接网络
     */
    public class Link {
        private String url = "";
        private int netStatus;
        private OkHttpClient client;
        public WebSocket webSocket;

        public Link(String url, int netStatus){
            this.url = url;
            this.netStatus = netStatus;
        }

        public void setNetStatus(int netStatus){
            this.netStatus = netStatus;
        }

        public void linkServer(){
            switch (netStatus){
                case NetworkManager.TYPE_NOT_CONNECT:{BeidouLinking();break;}
                case NetworkManager.TYPE_WIFI_MOBILE:{NetLinking();break;}
                case NetworkManager.TYPE_BLUETOOTH:{BluetoothLinking();break;}
                default:break;
            }
        }

        /**
         * WIFI\4G下连接
         */
        private void NetLinking(){
            client = new OkHttpClient.Builder()
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
            if (webSocket != null){
                webSocket.cancel();
            }

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            webSocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);
                    Log.d("WebSocket", "onOpen");
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    super.onMessage(webSocket, text);
                    Log.d("WebSocket", "onMessage" + text);
                    Intent intent = new Intent("com.beidouapp.callback.content");
                    intent.putExtra("message", text);
                    sendBroadcast(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString("message", text);
                    Message message = new Message();
                    message.setData(bundle);
                    message.what = 1;
                    write2DBHandler.sendMessage(message);
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    super.onClosing(webSocket, code, reason);
                    Log.d("WebSocket", "onClosing");
                    if (netStatus == NetworkManager.TYPE_WIFI_MOBILE) {
                        NetLinking();
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                    super.onFailure(webSocket, t, response);
                    Log.d("WebSocket", "onFailure");
                    if (netStatus == NetworkManager.TYPE_WIFI_MOBILE) {
                        NetLinking();
                    }
                }
            });


        }
        private void BeidouLinking(){}
        private void BluetoothLinking(){}

    }

    private void initTimer() {
        final int[] cnt = {0};
        timer = new Timer();
        heartbeatMSG = new TimerTask() {
            @Override
            public void run() {
                cnt[0]++;
//                onHeartbeat();
                sendLoc();
//                Log.d("zw", "run: 发送定位包");
                if(cnt[0]%6 == 0)
                {
                    sendHeart();
//                    Log.d("zw", "run: 发送状态包");
                }
            }
        };
        timer.schedule(heartbeatMSG, 0,1000 * 5);
    }

    private void sendHeart() {
        state_request stateRequest = new state_request(MsgService.this);
        Long timestamp = System.currentTimeMillis();
        String messageType = "deviceLifeCycle";
        int battery = state_request.getBattery(this);

        sysProperty sysProperty = new sysProperty(messageType, "北三手持终端");
//        Log.d("zw", "onHeartbeat: 在发送状态包的时候的用户是：" + uid);
        appProperty appProperty = new appProperty(uid, timestamp);

        body body = new body("online", String.valueOf(battery));

        HeartbeatMsg heartbeatMsg = new HeartbeatMsg(sysProperty,appProperty,body);

        String json = JSONUtils.sendJSON(heartbeatMsg);
        OkHttpUtils.getInstance(MsgService.this).postBD("http://119.3.130.87:50099/whbdApi/device/report/status", json, new OkHttpUtils.MyCallback() {
            @Override
            public void success(Response response) throws IOException {
//                Log.d("zw", "心跳包上传到福大的返回消息是：" + response.body().string());
            }

            @Override
            public void failed(IOException e) {

            }
        }, "f9bddcacc678ea185bf8158d90087fbc");

    }

    private void sendLoc() {
        state_request stateRequest = new state_request(MsgService.this);
        List<String> dataStreams = new ArrayList<>();
        List<String> lonAndLat;
        List<position> positions = new ArrayList<>();
        List<alarm> alarm = new ArrayList<>();
        String messageType = "deviceDatapoint";
        Long timestamp = System.currentTimeMillis();

        dataStreams.add("position");
        dataStreams.add("alarm");

        sysProperty sysProperty = new sysProperty(messageType, "北三手持终端");
        appProperty appProperty = new appProperty(uid, timestamp, dataStreams);

        try {
            lonAndLat = stateRequest.loc(this);
            Log.d("zw", "sendLoc: 此时使用的是gps发送坐标,坐标为" + lonAndLat);
        }catch (Exception e){
            e.printStackTrace();
            List<String> list = new ArrayList<>();
            MyLocationListener myLocationListener = new MyLocationListener();
            lonAndLat = myLocationListener.getLatLng();
        }

        if(lonAndLat.get(0).equals("0.0") && lonAndLat.get(1).equals("0.0"))
        {
            Log.d("zw", "sendLoc: 此时坐标为0，0");
        }else
        {
            Log.d("zw", "sendLoc: 此时坐标不在0，0点" + lonAndLat.get(0) + "  " + lonAndLat.get(1));
            position position1 = new position(lonAndLat.get(0), lonAndLat.get(1), "E", "W", true,
                    Double.parseDouble(lonAndLat.get(2)), 0, Double.parseDouble(lonAndLat.get(3)), timestamp);

            positions.add(position1);

            alarm alarm1 = new alarm("1", "xiaoyu1", timestamp);
            alarm.add(alarm1);

            body body = new body(positions,alarm);
            HeartbeatMsg heartbeatMsg = new HeartbeatMsg(sysProperty,appProperty,body);
            String json = JSONUtils.sendJSON(heartbeatMsg);
//        Log.d("zw", "sendLoc: 上传位置包的Json" + json);
            OkHttpUtils.getInstance(MsgService.this).postBD("http://119.3.130.87:50099/whbdApi/device/report/data", json, new OkHttpUtils.MyCallback() {
                @Override
                public void success(Response response) throws IOException {
//                Log.d("zw", "位置包上传到福大的返回消息是：" + response.body().string());
                }

                @Override
                public void failed(IOException e) {
//                Log.d("callback:failed", e.getMessage());
                }
            }, "f9bddcacc678ea185bf8158d90087fbc");
        }


    }


    private void initHandler() {
        write2DBHandler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message message) {
                switch (message.what){
                    case 1:{
                        String text = message.getData().getString("message");
                        Message4Receive message4Receive = JSONUtils.receiveJSON(text);
                        Log.d("WebSocket", "onMessage" + message4Receive.toString());
                        if (message4Receive.getType().equals("MSG")) {
                            if (message4Receive.getReceiveType().equals("group")) {
                                Log.d("zw", "onReceive: 暂时不处理群聊消息入库，后面再处理");
                                String toID = message4Receive.getData().getGroupId();
                                manRecords = LitePal.where("toID=? and selfID=?", toID, uid).find(recentMan.class);
                                if(manRecords.isEmpty()){
                                    manRecord = new recentMan();
                                    manRecord.setToID(toID);
                                    manRecord.setSelfId(uid);
                                    manRecord.setType("1");
                                    manRecord.save();//最近群聊群名入库
                                }else {
                                    manRecord = manRecords.get(0);
                                }
                                ContentValues values = new ContentValues();
                                values.put("groupID", toID);
                                values.put("selfID", uid);
                                values.put("flag", message4Receive.getData().getSendUserId());//别人发的,flag设置为账号（手机号）
                                values.put("contentChat", message4Receive.getData().getSendText());
                                values.put("message_type", "text");
                                values.put("time", message4Receive.getData().getSendTime());
                                writableDatabase.insert("chat_group", null, values);//最近获得的群聊消息入库
                                Log.d("websocket", "群聊消息入库成功");
                            }else{
                                if(!message4Receive.getData().getSendUserId().isEmpty()){
                                    String toID = message4Receive.getData().getSendUserId();
                                    manRecords = LitePal.where("toID=? and selfID=?", toID, uid).find(recentMan.class);
                                    if(manRecords.isEmpty()){
                                        manRecord = new recentMan();
                                        manRecord.setToID(toID);
                                        manRecord.setSelfId(uid);
                                        manRecord.setType("0");
                                        manRecord.save();//最近单聊用户入库
                                    }else {
                                        manRecord = manRecords.get(0);
                                    }
                                    ContentValues values = new ContentValues();
                                    values.put("toID", toID);
                                    values.put("selfID", uid);
                                    values.put("flag", 0);//别人发的是0
                                    values.put("contentChat", message4Receive.getData().getSendText());
                                    values.put("message_type", "text");
                                    values.put("time", message4Receive.getData().getSendTime());
                                    writableDatabase.insert("chat", null, values);//最近获得的单聊消息入库
                                    Log.d("websocket", "入库成功");
                                }else{
                                    Log.d("zw", "onReceive: 此时收到的广播的消息，但是这条广播显示的发送人ID是空的，woc");
                                }
                            }
                        }
                        break;
                    } default:{break;}
                }
            }
        };

    }



    @Override
    public void onDisconnect() {
        NetStatus = NetworkManager.TYPE_NOT_CONNECT;
        msgLink.setNetStatus(NetStatus);
        msgLink.linkServer();
    }

    @Override
    public void onWIFIMobileConnect() {
        NetStatus = NetworkManager.TYPE_WIFI_MOBILE;
        msgLink.setNetStatus(NetStatus);
        msgLink.linkServer();
    }

    @Override
    public void onBlueToothConnect() {
        NetStatus = NetworkManager.TYPE_BLUETOOTH;
        msgLink.setNetStatus(NetStatus);
        msgLink.linkServer();
    }
}