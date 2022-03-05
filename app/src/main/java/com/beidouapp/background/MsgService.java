package com.beidouapp.background;

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

import com.alibaba.fastjson.JSON;
import com.beidouapp.model.DataBase.recentMan;
import com.beidouapp.model.FilePacket;
import com.beidouapp.model.messages.HeartbeatMsg;
import com.beidouapp.model.messages.HeartbeatMsg.*;
import com.beidouapp.model.messages.Message4Receive;
import com.beidouapp.model.messages.Message4Send;
import com.beidouapp.model.utils.Bytes2Hex;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.NetworkChangeReceiver;
import com.beidouapp.model.utils.NetworkManager;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.utils.state_request;
import com.beidouapp.ui.DemoApplication;
import com.beidouapp.ui.fragment.MyLocationListener;

import org.java_websocket.handshake.ServerHandshake;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.client.WebSocketClient;

import okhttp3.Response;


/**
 * 消息收发服务
 * 在此服务实现各类网络连接
 * WIFI/MOBILE:WebSocket
 * BLUETOOTH:
 * BEIDOU:
 */

public class MsgService extends Service implements NetworkChangeReceiver.NetStateChangeObserver {
    private NetworkManager  networkManager;
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
        msgLink = new Link("ws://120.27.242.92:8080/chatWS/", uid, NetStatus);
        //http://10.123.254.170:8080/
        msgLink.linkServer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        NetworkChangeReceiver.unRegisterObserver(this);
        NetworkChangeReceiver.unRegisterReceiver(this);
        msgLink.disLinkServer();
    }



    /**
     * 发送消息
     * 根据网络状态
     * @param message
     */
    public boolean sendMessage(String message){
        if (NetStatus == NetworkManager.TYPE_WIFI_MOBILE) {
            try {
                msgLink.webSocketClient.send(message);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        if (NetStatus == NetworkManager.TYPE_BLUETOOTH) {
            try {
                msgLink.webSocketClient.send(message);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        if (NetStatus == NetworkManager.TYPE_NOT_CONNECT) {
            try {
                msgLink.webSocketClient.send(message);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }


    /**
     * 发送文件
     * @param filePath
     * @return
     */
    public boolean sendMessage(Message4Send message4Send, Path filePath) {
        String fileName = filePath.getFileName().toString();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        FilePacket p = FilePacket.constructUpNewFilePacket(suffix);
        msgLink.setUpFilePath(filePath);
        msgLink.setUpFileMessage(message4Send);
        msgLink.webSocketClient.send(p.getBuffer().array());

        return false;
    }

    /**
     * 请求文件
     * @return
     */
    public boolean requestFile (String path) {
        Path serverPath = Paths.get(path);
        FilePacket p = FilePacket.constructDownNewFilePacket(path);
        msgLink.setDownFilePath(Paths.get(FilePacket.createFilePath(MsgService.this),
                serverPath.getFileName().toString()));
        System.out.println(msgLink.downFilePath.toString());
        try {
            msgLink.fileChannel = Files.newByteChannel(msgLink.downFilePath,
                    EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            msgLink.webSocketClient.send(p.getBuffer().array());
            msgLink.md = MessageDigest.getInstance("MD5");
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 连接聊天服务
     */
    public class Link {
        public WebSocketClient webSocketClient;
        private String url;
        private int netStatus;
        private Path upFilePath;
        private Path downFilePath;
        private List<Path> downFilePathList = new ArrayList<>();
        private Message4Send upFileMessage;
        private ByteChannel fileChannel;
        private MessageDigest md;
        private Queue<String> downFilePathQueue = new LinkedList<>();



        public void setUpFilePath(Path upFilePath) {
            this.upFilePath = upFilePath;
        }

        public void setDownFilePath(Path downFilePath) {
            this.downFilePath = downFilePath;
        }

        public Message4Send getUpFileMessage() {
            return upFileMessage;
        }

        public void setUpFileMessage(Message4Send upFileMessage) {
            this.upFileMessage = upFileMessage;
        }

        public Link(String url, String id,int netStatus){
            this.url = url + id;
            this.netStatus = netStatus;

//            filePacketQueue.offer() //ru
//            filePacketQueue.poll() //chu
        }

        public void setNetStatus(int netStatus){
            this.netStatus = netStatus;
        }

        /**
         * WIFI\4G下连接
         * 使用WebSocket
         */
        private void NetLinking(){

            URI uri = URI.create(url);
            Log.d("WebSocket", uri.getHost());
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakes) {
                    Log.d("WebSocket", "onOpen");
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    super.onMessage(bytes);
                    FilePacket p = FilePacket.parseByteBuffer(bytes);
                    int code;
                    switch (p.getType()) {
                        case FilePacket.UP_ACK_NEW_FILE:{
                            code = (int) p.getBuffer().get();
                            if (code == FilePacket.SUCCESS_CODE) {
                                Log.d("WebSocket", "开始发送文件");
                            }

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    msgLink.startSendFileData();
                                }
                            }).start();
                            break;
                        }
                        case FilePacket.UP_ACK_FILE_END:{
                            code = (int) p.getBuffer().get();
                            System.out.println(code);
                            if (code == FilePacket.SUCCESS_CODE) {
                                Log.d("WebSocket", "文件发送完成");
                                String str = FilePacket.getUpFileServerPath(p.getBuffer());
                                Log.d("WebSocket", "返回路径:" + str);
                                msgLink.upFileMessage.setSendText(str);
                                String json = JSON.toJSONString(msgLink.upFileMessage, true);
                                sendMessage(json);
                            }
                            break;
                        }
                        case FilePacket.DOWN_ACK_NEW_FILE:{
                            code = (int) p.getBuffer().get();
                            if (code == FilePacket.SUCCESS_CODE) {
                                Log.d("file", "开始接收文件");
                                FilePacket p4s = FilePacket.constructDownFileRequestPacket(code);
                                webSocketClient.send(p4s.getBuffer());
                            } else if (code == FilePacket.ERROR_CODE) {
                                FilePacket p4s = FilePacket.constructDownFileRequestPacket(code);
                                webSocketClient.send(p4s.getBuffer());
                            }
                            break;
                        }
                        case FilePacket.DOWN_FILE_DATA:{
                            try {
                                Log.d("file", "接收到文件包");
                                p.getBuffer().mark();
                                md.update(p.getBuffer());
                                p.getBuffer().reset();
                                fileChannel.write(p.getBuffer());
                            } catch (IOException e){
                                try {
                                    fileChannel.close();
                                } catch (IOException ignore) {
                                }
                            }
                            break;
                        }
                        case FilePacket.DOWN_FILE_END:{

                                byte[] digest = md.digest();
                                String localDigest = Bytes2Hex.bytes2hex(digest).toUpperCase();
                                System.out.println("local, digest : " + localDigest);
                            try {
                                int digestBytesLen = p.getBuffer().getInt();
                                byte[] digestBytes = new byte[digestBytesLen];
                                p.getBuffer().get(digestBytes);
                                String remoteDigest = new String(digestBytes, StandardCharsets.UTF_8);
                                System.out.println("receive file end, digest : " + remoteDigest);
                                FilePacket ackP;
                                if(localDigest.equals(remoteDigest)){
                                    System.out.println("file digests are same, send success ack code");
                                    ackP = FilePacket.constructDownAckFileEndPacket(FilePacket.SUCCESS_CODE);
                                } else {
                                    System.out.println("file digests are not same, send error ack code");
                                    ackP = FilePacket.constructDownAckFileEndPacket(FilePacket.ERROR_CODE);
                                }
                                webSocketClient.send(ackP.getBuffer());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            downFilePathQueue.poll();

                            if (!downFilePathQueue.isEmpty()) {
                                requestFile(downFilePathQueue.peek());
                            }

                            break;
                        }

                    }
                }

                @Override
                public void onMessage(String text) {
                    Log.d("WebSocket", "onMessage" + text);
                    Intent intent = new Intent("com.beidouapp.callback.content");
//                    intent.putExtra("message", text);
//                    sendBroadcast(intent);

                    Bundle bundle = new Bundle();

                    Message4Receive message4Receive = JSONUtils.receiveJSON(text);

                    String receiveType = message4Receive.getReceiveType();                          //group或single
                    bundle.putString("receiveType", receiveType);

                    if (message4Receive.getType().equals("MSG")) {

                        String msgType = message4Receive.getData().getMsgType();                    //text或img
                        bundle.putString("msgType", msgType);

                        bundle.putString("sendText", message4Receive.getData().getSendText());      //发送内容：文字信息或图片地址
                        bundle.putString("sendUserId", message4Receive.getData().getSendUserId());  //发送者id
                        bundle.putString("sendTime", message4Receive.getData().getSendTime());      //发送时间

                        if (msgType.equals("text")) {
                            if (receiveType.equals("group")) {
                                bundle.putString("groupId", message4Receive.getData().getGroupId());
                            }
                            intent.putExtra("messageBundle", bundle);
                            sendBroadcast(intent);
                        } else if (msgType.equals("img")) {
                            String path = bundle.getString("sendText");

                            if (downFilePathQueue.isEmpty()) {
                                requestFile(path);
                            }
                            downFilePathQueue.offer(path);
                        }

                        Message message = new Message();
                        message.setData(bundle);
                        message.what = 1;
                        write2DBHandler.sendMessage(message);
                    }
                    else if (message4Receive.getType().equals("ERR")) {
                        if (message4Receive.getMsg().equals("用户重复登陆")){

                        }
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("WebSocket", "onClose");
//                    if (NetStatus == NetworkManager.TYPE_WIFI_MOBILE){
//                        linkServer();
//                    }
                }
                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                    if (NetStatus == NetworkManager.TYPE_WIFI_MOBILE){
                        linkServer();
                    }
                }
            };
            try {
                webSocketClient.connectBlocking();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void NetDisLinking() {
            if (webSocketClient != null) {
                webSocketClient.close();
                webSocketClient = null;
            }
        }
        private void BeidouLinking(){}
        private void BluetoothLinking(){}


        public void linkServer(){
            switch (netStatus){
                case NetworkManager.TYPE_NOT_CONNECT:{BeidouLinking();break;}
                case NetworkManager.TYPE_WIFI_MOBILE:{NetLinking();break;}
                case NetworkManager.TYPE_BLUETOOTH:{BluetoothLinking();break;}
                default:break;
            }
        }

        public void disLinkServer() {
            NetDisLinking();
        }

        private void startSendFileData(){

            try {
                ByteChannel fileChannel = Files.newByteChannel(upFilePath, EnumSet.of(StandardOpenOption.READ));
                ByteBuffer buffer = ByteBuffer.allocate(1 + 4096);
                buffer.order(ByteOrder.BIG_ENDIAN);

                MessageDigest md = MessageDigest.getInstance("MD5");

                int bytesRead = -1;

                buffer.clear();//make buffer ready for write
                buffer.put((byte)FilePacket.UP_ACK_FILE_END);

                while((bytesRead = fileChannel.read(buffer)) != -1){
                    buffer.flip();  //make buffer ready for read
                    buffer.mark();
                    buffer.get(); //skip a byte
                    md.update(buffer);
                    buffer.reset();
                    webSocketClient.send(buffer);
                    buffer.clear(); //make buffer ready for write
                    buffer.put((byte)FilePacket.UP_ACK_FILE_END);
                }

                byte[] digest = md.digest();
                String digestInHex = Bytes2Hex.bytes2hex(digest).toUpperCase();
                System.out.println("send file finished, digest: " + digestInHex);
                FilePacket p = FilePacket.constructUpFileEndPacket(digestInHex);
                webSocketClient.send(p.getBuffer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void startReceiveFileData() {

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
        Log.d("zw", "sendHeart: 发送给福大的状态信息为：" + json);
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
        write2DBHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what){
                    case 1:{
                        Bundle bundle = message.getData();

                        if (Objects.equals(bundle.getString("receiveType"), "group")) {
                            String toID = bundle.getString("groupId");
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
                            values.put("flag", bundle.getString("sendUserId"));//别人发的,flag设置为账号（手机号）
                            values.put("contentChat", bundle.getString("sendText"));
                            values.put("message_type", "text");
                            values.put("time", bundle.getString("sendTime"));
                            writableDatabase.insert("chat_group", null, values);//最近获得的群聊消息入库
                            Log.d("websocket", "群聊消息入库成功");
                        }else{
                            if(!Objects.requireNonNull(bundle.getString("sendUserId")).isEmpty()){
                                String toID = bundle.getString("sendUserId");
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
                                values.put("contentChat", bundle.getString("sendText"));
                                values.put("message_type", "text");
                                values.put("time", bundle.getString("sendTime"));
                                writableDatabase.insert("chat", null, values);//最近获得的单聊消息入库
                                Log.d("websocket", "入库成功");
                            }else{
                                Log.d("zw", "onReceive: 此时收到的广播的消息，但是这条广播显示的发送人ID是空的，woc");
                            }
                        }
                        break;
                    } default:{break;}
                }
                return false;
            }
        });

    }



    @Override
    public void onDisconnect() {
        NetStatus = NetworkManager.TYPE_NOT_CONNECT;
        Log.d("zw", "onDisconnect: 此时的状态" + NetStatus);
        //等2秒
        int connectivityStatus = NetworkManager.getConnectivityStatus(this);
        if (connectivityStatus == NetStatus){
            msgLink.setNetStatus(NetStatus);
            if (msgLink.webSocketClient.isClosed()){
                msgLink.linkServer();
            }
        }else{
            switch (connectivityStatus){
                case NetworkManager.TYPE_WIFI_MOBILE:{
                    onWIFIMobileConnect();
                    break;
                }
                case NetworkManager.TYPE_BLUETOOTH:{
                    onBlueToothConnect();
                    break;
                } default:break;
            }
        }
    }

    @Override
    public void onWIFIMobileConnect() {
        NetStatus = NetworkManager.TYPE_WIFI_MOBILE;
        Log.d("zw", "onWIFIMobileConnect: 此时的状态" + NetStatus);
        //等两秒
        int connectivityStatus = NetworkManager.getConnectivityStatus(this);
        if (connectivityStatus == NetStatus){
            msgLink.setNetStatus(NetStatus);
            if (msgLink.webSocketClient.isClosed()){
                msgLink.linkServer();
            }
        }else{
            switch (connectivityStatus){
                case NetworkManager.TYPE_NOT_CONNECT:{
                    onDisconnect();
                    break;
                }
                case NetworkManager.TYPE_BLUETOOTH:{
                    onBlueToothConnect();
                    break;
                } default:break;
            }
        }
    }

    @Override
    public void onBlueToothConnect() {
        NetStatus = NetworkManager.TYPE_BLUETOOTH;
        Log.d("zw", "onBlueToothConnect: 此时的状态" + NetStatus);
        //等两秒
        int connectivityStatus = NetworkManager.getConnectivityStatus(this);
        if (connectivityStatus == NetStatus){
            msgLink.setNetStatus(NetStatus);
            if (msgLink.webSocketClient.isClosed()){
                msgLink.linkServer();
            }
        }else{
            switch (connectivityStatus){
                case NetworkManager.TYPE_NOT_CONNECT:{
                    onDisconnect();
                    break;
                }
                case NetworkManager.TYPE_WIFI_MOBILE:{
                    onWIFIMobileConnect();
                    break;
                } default:break;
            }
        }
    }
}