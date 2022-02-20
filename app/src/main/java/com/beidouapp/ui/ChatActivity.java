package com.beidouapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.background.MsgService;
import com.beidouapp.model.DataBase.recentMan;
import com.beidouapp.model.adapters.ChatAdapter;
import com.beidouapp.model.messages.ChatMessage;
import com.beidouapp.model.messages.Group;
import com.beidouapp.model.messages.Message4Receive;
import com.beidouapp.model.messages.Message4Send;
import com.beidouapp.model.utils.JSONUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天活动
 * 完成聊天窗口
 * 消息收发列表显示
 */

public class ChatActivity extends AppCompatActivity {
    private Context context; //
    private ChatAdapter adapter;
    private List<ChatMessage> chatMessageList = new ArrayList<>();
    private List<ChatMessage> templeList = new ArrayList<>();
    private ChatReceiver chatReceiver;
    private String toID;//对方ID
    private String toNickname;//昵称
    private String toType;  //消息类型
    private String loginId;  //自己的ID
    private long timeMillis;
    private Map<String, String> userMap = new HashMap<String, String>();
    private TextView title;
    private ListView listView;
    private EditText input;
    private Button btn_send;
    private ImageButton btn_back;
    private DemoApplication application;
    private SQLiteDatabase writableDatabase;
    private List<recentMan> manRecords;
    private recentMan manRecord;



    /**
     * 绑定消息服务与聊天活动
     */
    private MsgService.Link link;
    private MsgService msgService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msgService = ((MsgService.MsgBinder)service).getService();
            link = msgService.msgLink;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            msgService = null;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        application = (DemoApplication) this.getApplicationContext();
        writableDatabase = application.dbHelper.getWritableDatabase();
        getInfo();
//        Log.d("uid, chat", toID);
        initUI();
        initChatMessageList(toType);
        initListener();
        BindMsgService();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(chatReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }



    /**
     * 获取上下文信息
     */
    private void getInfo() {
        context = ChatActivity.this;
        Intent intent = getIntent();
        toID = intent.getStringExtra("uid");
        toNickname = intent.getStringExtra("nickname");
        toType = intent.getStringExtra("type"); //single or group
        loginId = application.getUserID();
//        if (toType.equals("group")) {
//            String groupInfo = intent.getStringExtra("groupInfo");
//            Log.d("group", groupInfo);
//            JSONObject object = JSON.parseObject(groupInfo);
//            JSONArray array = (JSONArray) object.get("data");
//            List<Group.GroupUsers> groupUsers = (List<Group.GroupUsers>) JSONArray.parseArray(array.toString(), Group.GroupUsers.class);
//            int size = groupUsers.size();
//            for (int i = 0; i < size; i++) {
//                Group.GroupUsers user = groupUsers.get(i);
//                userMap.put(user.getUserId(), user.getUserName());
//            }
//        }

    }


    /**
     * 初始化UI控件
     */
    private void initUI(){
        listView = (ListView) findViewById(R.id.chatmsg_listView);
        btn_send = (Button) findViewById(R.id.btn_send);
        input = (EditText) findViewById(R.id.et_content);
        btn_back = (ImageButton) findViewById(R.id.chat_return);
        title = (TextView) findViewById(R.id.tv_groupOrContactName);
        title.setText(toNickname);
    }


    /**
     * 初始化广播接收器
     */
    private void initReceiver(){
        chatReceiver = new ChatReceiver();
        IntentFilter filter = new IntentFilter("com.beidouapp.callback.content");
        registerReceiver(chatReceiver, filter);
    }

    /**
     * 添加监听器
     * btn_send：发送按钮，点击则将输入框内容存入消息列表并发送给服务器
     * input：输入框，同步更新输入
     */
    private void initListener(){
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = input.getText().toString();  //获取自己发送消息
                if (content.length() <= 0){
                    Toast.makeText(ChatActivity.this, "输入不能为空", Toast.LENGTH_LONG).show();
                }

                if (msgService.msgLink.webSocket != null) {
//                if (true) {

                    if (toType.equals("group")) {
                        Message4Send message4Send = new Message4Send(toID,"group", "text", content);
                        Log.d("string", message4Send.toString());
                        String json = JSON.toJSONString(message4Send,true);
                        msgService.sendMessage(json);
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setContent(content);
                        chatMessage.setIsMeSend(1);
                        chatMessage.setTime(System.currentTimeMillis()+"");
                        chatMessageList.add(chatMessage);
                        timeMillis = System.currentTimeMillis();
                        Log.d("zw", "onClick: 群ID" + toID);
                        ContentValues values = new ContentValues();
                        values.put("groupID", toID);
                        values.put("selfID", loginId);
                        values.put("flag", "1");//自己发的是1
                        values.put("contentChat", content);
                        values.put("message_type", "text");
                        values.put("time", String.valueOf(timeMillis));
                        writableDatabase.insert("chat_group", null, values);
                        manRecords = LitePal.where("toID=? and selfID=?", toID, loginId).find(recentMan.class);
                        if(manRecords.isEmpty()){
                            manRecord = new recentMan();
                            manRecord.setToID(toID);
                            manRecord.setSelfId(loginId);
                            manRecord.setType("1");
                            manRecord.save();
                        }else {
                            manRecord = manRecords.get(0);
                        }
                    } else {

//                        Log.d("uid, sendto", toID); //toID 对方ID
                        Message4Send message4Send = new Message4Send(toID, "single", "text", content);
                        Log.d("string", message4Send.toString());
                        String json = JSON.toJSONString(message4Send, true);

                        msgService.sendMessage(json);

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setContent(content);
                        chatMessage.setIsMeSend(1);
                        timeMillis = System.currentTimeMillis();
                        chatMessage.setTime(String.valueOf(timeMillis)+"");
                        chatMessageList.add(chatMessage);
                        //插入数据
                        ContentValues values = new ContentValues();
                        values.put("toID", toID);
                        values.put("selfID", loginId);
                        values.put("flag", 1);//自己发的是1
                        values.put("contentChat", content);
                        values.put("message_type", "text");
                        values.put("time", String.valueOf(timeMillis));
                        writableDatabase.insert("chat", null, values);

                        //将最近的一次消息写入数据库
                        manRecords = LitePal.where("toID=? and selfID=?", toID, loginId).find(recentMan.class);
                        if(manRecords.isEmpty()){
                            manRecord = new recentMan();
                            manRecord.setToID(toID);
                            manRecord.setSelfId(loginId);
                            manRecord.setType("0");//0是单聊
                            manRecord.save();
                        }else {
                            manRecord = manRecords.get(0);
                        }

                    }
                    initChatMsgListView();
                    input.setText("");

                } else {
                    Toast.makeText(ChatActivity.this, "网络连接失败", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input.getText().toString().length() > 0) {
                    btn_send.setVisibility(View.VISIBLE);
                } else {
                    btn_send.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * 聊天消息广播接收器
     */
    private class ChatReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.d("WebSocket", "onReceive" + message);
            Message4Receive message4Receive = JSONUtils.receiveJSON(message);
            if (message4Receive.getType().equals("MSG")) {
                if (message4Receive.getReceiveType().equals("group")) {

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setContent(message4Receive.getData().getSendText());
                    chatMessage.setIsMeSend(0);
                    chatMessage.setTime(System.currentTimeMillis()+"");
                    chatMessage.setName(userMap.get(message4Receive.getData().getSendUserId()));
                    chatMessageList.add(chatMessage);
                    initChatMsgListView();

                } else if (message4Receive.getData().getSendUserId().equals(toID)) {

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setContent(message4Receive.getData().getSendText());
                    chatMessage.setIsMeSend(0);
                    long timeMillis1 = System.currentTimeMillis();
                    chatMessage.setTime(String.valueOf(timeMillis1)+"");
                    chatMessage.setName(toNickname);
                    chatMessageList.add(chatMessage);
                    initChatMsgListView();

                }
            }
        }
    }

    /**
     * 更新消息列表
     */
    private void initChatMsgListView() {
        adapter = new ChatAdapter(context, chatMessageList);
        listView.setAdapter(adapter);
        listView.setSelection(chatMessageList.size());
    }


    /**
     * 绑定服务
     */
    private void BindMsgService(){
        Intent bindIntent = new Intent(ChatActivity.this, MsgService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
        Log.d("service", "connected");
    }


    /**
     * 初始化聊天记录
     */
    private void initChatMessageList(String str) {
        if(str.equals("single")){
            String flag;
            String contentChat;
            String time;
            String message_type;
            ChatMessage chatMessage;
            int i = 0;
            Log.d("zw", "initChatMessageList: 此时初始化入库");
            try {
                Cursor query = writableDatabase.query("chat", null, "toID=? and selfID=?",
                        new String[]{toID,loginId}, null, null, "time desc");
                query.moveToFirst();
                do{
                    i++;
                    flag = query.getString(query.getColumnIndex("flag"));
                    contentChat = query.getString(query.getColumnIndex("contentChat"));
                    time = query.getString(query.getColumnIndex("time"));
                    message_type = query.getString(query.getColumnIndex("message_type"));
                    if (flag.equals("0")){
                        chatMessage = new ChatMessage(toNickname, contentChat, time, 0);
                    }else{
                        chatMessage = new ChatMessage(loginId, contentChat, time, 1);
                    }
                    templeList.add(chatMessage);
                    if (i == 11){break;}
                }while (query.moveToNext());

            }catch (Exception e){
                e.printStackTrace();
                Log.d("zw", "initChatMessageList: 取消息出错");
            }
            if (templeList.size() == 0){
                Log.d("zw", "initChatMessageList: 此时数据库关于此人聊天记录为空");
            }
            try {
                Collections.reverse(templeList);
                chatMessageList = templeList;
            }catch (Exception e){
                e.printStackTrace();
                Log.d("zw", "initChatMessageList: 倒序发生异常");
            }
        }else {
            String flag;
            String contentChat;
            String time;
            String message_type;
            ChatMessage chatMessage; //加载
            int i = 0;
            Log.d("zw", "initChatMessageList: 此时初始化入库");
            try {
                Cursor query = writableDatabase.query("chat_group", null, "groupID=? and selfID=?",
                        new String[]{toID,loginId}, null, null, "time desc");
                query.moveToFirst();
                do{
                    i++;
                    flag = query.getString(query.getColumnIndex("flag"));
                    contentChat = query.getString(query.getColumnIndex("contentChat"));
                    time = query.getString(query.getColumnIndex("time"));
                    message_type = query.getString(query.getColumnIndex("message_type"));
                    if (flag.equals("1")){
                        chatMessage = new ChatMessage(loginId, contentChat, time, 1);
                    }else{
//                        Cursor cursor = writableDatabase.query("friend",null,"friend_id=? AND selfID=?",
//                                new String[]{flag, loginId}, null, null, null); //将电话转换成名字
                        chatMessage = new ChatMessage(flag, contentChat, time, 0);
                    }
                    templeList.add(chatMessage);
                    if (i == 11){break;}
                }while (query.moveToNext());

            }catch (Exception e){
                e.printStackTrace();
                Log.d("zw", "initChatMessageList: 取消息出错");
            }
            if (templeList.size() == 0){
                Log.d("zw", "initChatMessageList: 此时数据库关于此人聊天记录为空");
            }
            try {
                Collections.reverse(templeList);
                chatMessageList = templeList;
            }catch (Exception e){
                e.printStackTrace();
                Log.d("zw", "initChatMessageList: 倒序发生异常");
            }
        }

        initChatMsgListView();
    }


}