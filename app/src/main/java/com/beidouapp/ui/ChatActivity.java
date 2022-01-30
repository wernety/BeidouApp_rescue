package com.beidouapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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
import com.beidouapp.model.adapters.ChatAdapter;
import com.beidouapp.model.messages.ChatMessage;
import com.beidouapp.model.messages.Group;
import com.beidouapp.model.messages.Message4Receive;
import com.beidouapp.model.messages.Message4Send;
import com.beidouapp.model.utils.JSONUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天活动
 * 完成聊天窗口
 * 消息收发列表显示
 */

public class ChatActivity extends AppCompatActivity {
    private Context context;
    private ChatAdapter adapter;
    private List<ChatMessage> chatMessageList = new ArrayList<>();
    private ChatReceiver chatReceiver;
    private String toID;
    private String toNickname;
    private String toType;
    private Map<String, String> userMap = new HashMap<String, String>();
    private TextView title;
    private ListView listView;
    private EditText input;
    private Button btn_send;
    private ImageButton btn_back;


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

        getInfo();
        Log.d("uid, chat", toID);
        initUI();
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
    }


    /**
     * 获取上下文信息
     */
    private void getInfo() {
        context = ChatActivity.this;
        Intent intent = getIntent();
        toID = intent.getStringExtra("uid");
        toNickname = intent.getStringExtra("nickname");
        toType = intent.getStringExtra("type");
        if (toType.equals("group")) {
            String groupInfo = intent.getStringExtra("groupInfo");
            Log.d("group", groupInfo);
            JSONObject object = JSON.parseObject(groupInfo);
            JSONArray array = (JSONArray) object.get("data");
            List<Group.GroupUsers> groupUsers = (List<Group.GroupUsers>) JSONArray.parseArray(array.toString(), Group.GroupUsers.class);
            int size = groupUsers.size();
            for (int i = 0; i < size; i++) {
                Group.GroupUsers user = groupUsers.get(i);
                userMap.put(user.getUserId(), user.getUserName());
            }
        }

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
                String content = input.getText().toString();
                if (content.length() <= 0){
                    Toast.makeText(ChatActivity.this, "输入不能为空", Toast.LENGTH_LONG).show();
                }

                if (msgService.msgLink.webSocket != null) {

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

                    } else {
                        Log.d("uid, sendto", toID);
                        Message4Send message4Send = new Message4Send(toID, "single", "text", content);
                        Log.d("string", message4Send.toString());
                        String json = JSON.toJSONString(message4Send, true);

                        msgService.sendMessage(json);

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setContent(content);
                        chatMessage.setIsMeSend(1);
                        chatMessage.setTime(System.currentTimeMillis()+"");
                        chatMessageList.add(chatMessage);

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
                    chatMessage.setTime(System.currentTimeMillis()+"");
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


}