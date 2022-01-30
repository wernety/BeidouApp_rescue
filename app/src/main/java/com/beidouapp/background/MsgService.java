package com.beidouapp.background;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.beidouapp.model.messages.HeartbeatMsg;
import com.beidouapp.model.messages.HeartbeatMsg.*;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.LocationUtils;
import com.beidouapp.model.utils.NetworkManager;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.utils.state_request;

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

public class MsgService extends Service {
    private NetworkManager networkManager;
    private int NetStatus;
    public Link msgLink;
    private String uid;
    private Timer timer;
    private TimerTask heartbeatMSG;
    private String curToken;


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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uid = intent.getStringExtra("uid");
        curToken = intent.getStringExtra("curToken");
        networkManager = new NetworkManager();
        NetStatus = networkManager.NetworkDetect(this);
        msgLink = new Link("ws://120.27.242.92:8080/chatWS/" + uid, NetStatus);
        msgLink.linkServer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }



    /**
     * 发送消息
     * 根据网络状态
     * @param message
     */
    public void sendMessage(String message){
        if (NetStatus == 1) {
            msgLink.webSocket.send(message);
        }
        if (NetStatus == 2) {
            msgLink.webSocket.send(message);
        }

        if (NetStatus == 3) {
        }
        if (NetStatus == 4) {
        }
        if (NetStatus == 0) {
        }
    }



    /**
     * 发送消息通知
     * @param content
     */
    private void SendNotification(String content) {
    }



    /**
     * 连接网络
     */
    public class Link {
        private String IP = "";
        //private int PORT = 0;
        private int netStatus = 0;
        private OkHttpClient client;
        public WebSocket webSocket;

        public Link(String IP, int netStatus){
            this.IP = IP;
            //this.PORT = PORT;
            this.netStatus = netStatus;
        }

        public void setNetStatus(int netStatus){
            this.netStatus = netStatus;
        }

        public void linkServer(){
            switch (netStatus){
                case 0:{break;}
                case 1:{NetLinking();break;}
                case 2:{BluetoothLinking();break;}
                case 3:{BeidouLinking();break;}
                default:break;
            }
        }

        public void dislinkServer(){
            switch (netStatus){
                case 0:{break;}
                case 1:{NetDislink();break;}
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
                    .url(IP)
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
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    super.onClosing(webSocket, code, reason);
                    Log.d("WebSocket", "onClosing");
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                    super.onFailure(webSocket, t, response);
                    Log.d("WebSocket", "onFailure");
                }
            });


        }
        private void NetDislink(){

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
                onHeartbeat();
                sendLoc();
                Log.d("zw", "run: 发送定位包");
                if(cnt[0] == 6)
                {
                    sendHeart();
                    Log.d("zw", "run: 发送状态包");
                }
            }
        };
        timer.schedule(heartbeatMSG, 0,1000 * 60);
    }

    private void sendHeart() {
        state_request stateRequest = new state_request(MsgService.this);
        Long timestamp = System.currentTimeMillis();
        String messageType = "deviceLifeCycle";
        int battery = state_request.getBattery(this);

        sysProperty sysProperty = new sysProperty(messageType, "北三手持终端");
        appProperty appProperty = new appProperty("13886415060", timestamp);

        body body = new body("online", String.valueOf(battery));

        HeartbeatMsg heartbeatMsg = new HeartbeatMsg(sysProperty,appProperty,body);

        String json = JSONUtils.sendJSON(heartbeatMsg);
        OkHttpUtils.getInstance(MsgService.this).postBD("http://119.3.130.87:50099/whbdApi/device/report/status", json, new OkHttpUtils.MyCallback() {
            @Override
            public void success(Response response) throws IOException {
                Log.d("zw", "心跳包上传到福大的返回消息是：" + response.body().string());
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
        appProperty appProperty = new appProperty("13886415060", timestamp, dataStreams);

        lonAndLat = stateRequest.loc(this);

        position position1 = new position(lonAndLat.get(1), lonAndLat.get(2), "E", "W", true,
                Double.parseDouble(lonAndLat.get(2)), 0, Double.parseDouble(lonAndLat.get(3)), timestamp);

        positions.add(position1);

        alarm alarm1 = new alarm("1", "xiaoyu1", timestamp);
        alarm.add(alarm1);

        body body = new body(positions,alarm);
        HeartbeatMsg heartbeatMsg = new HeartbeatMsg(sysProperty,appProperty,body);
        String json = JSONUtils.sendJSON(heartbeatMsg);
        OkHttpUtils.getInstance(MsgService.this).postBD("http://119.3.130.87:50099/whbdApi/device/report/status", json, new OkHttpUtils.MyCallback() {
            @Override
            public void success(Response response) throws IOException {
                Log.d("zw", "位置包上传到福大的返回消息是：" + response.body().string());
            }

            @Override
            public void failed(IOException e) {
                Log.d("callback:failed", e.getMessage());
            }
        }, "f9bddcacc678ea185bf8158d90087fbc");

    }

    private void onHeartbeat() {
        state_request stateRequest = new state_request(MsgService.this);
        Long timestamp = System.currentTimeMillis();
        //String messageType = "deviceLifeCycle";

        String messageType = "deviceDatapoint";
        List<String> dataStreams = new ArrayList<>();
        dataStreams.add("position");
        dataStreams.add("alarm");

        sysProperty sysProperty = new sysProperty(messageType, "北三手持终端");
        appProperty appProperty = new appProperty(uid, timestamp, dataStreams);
        //body body = new body("online", String.valueOf(state_request.getBattery(MsgService.this)));

        position position1 = new position("1", "2", "3", "4", true,
                36, 146, 120.56, timestamp);
        position position2 = new position("5", "6", "7", "8", true,
                39, 46, 120.98, timestamp);
        List<position> position = new ArrayList<>();
        position.add(position1);
        position.add(position2);

        alarm alarm1 = new alarm("1", "xiaoyu1", timestamp);
        List<alarm> alarm = new ArrayList<>();
        alarm.add(alarm1);

        body body = new body(position, alarm);




        HeartbeatMsg heartbeatMsg = new HeartbeatMsg(sysProperty,appProperty,body);
        String json = JSONUtils.sendJSON(heartbeatMsg);
        Log.d("heartbeat", json);
        OkHttpUtils.getInstance(MsgService.this).post("http://119.3.130.87:50099/whbdApi/device/report/status", json, new OkHttpUtils.MyCallback() {
            @Override
            public void success(Response response) throws IOException {
                Log.d("callback:success", response.message());
            }

            @Override
            public void failed(IOException e) {
                Log.d("callback:failed", e.getMessage());
            }
        });
    }

}