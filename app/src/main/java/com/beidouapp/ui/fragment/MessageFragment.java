package com.beidouapp.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.model.DataBase.recentMan;
import com.beidouapp.model.adapters.MessageAdapter;
import com.beidouapp.model.messages.ChatMessage;
import com.beidouapp.model.messages.Message4Receive;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.ListViewUtils;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.ui.ChatActivity;
import com.beidouapp.ui.DemoApplication;

import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

/**
 * Fragment：
 * 会话列表
 */

public class MessageFragment extends Fragment {
    private String uid;
    private ListView listView;
    private View view;
    private List<Map<String, Object>> ContactList = new ArrayList<Map<String, Object>>();
    private Context context;
    private boolean isGetData = false;
    private DemoApplication application;
    private SQLiteDatabase writableDatabase;
    private List<recentMan> manRecords;
    private ChatReceiver chatReceiver;

    @Nullable
    @Override
    //刷新
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter && !isGetData && ContactList.isEmpty()) {
            isGetData = true;
            RefreshContactList(getActivity().getApplicationContext());
        } else {
            isGetData = false;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (DemoApplication) getActivity().getApplicationContext();
        writableDatabase = application.dbHelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.fragment_message, container, false);
        initUI();
        initListener();
        RefreshContactList(context);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        RefreshContactList(getActivity().getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        isGetData = false;
        RefreshContactList(getActivity().getApplicationContext());
        initReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(chatReceiver);
    }

    private void initUI () {
        listView = view.findViewById(R.id.msg_frag_listView);
    }

    private void initListener () {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> contact = ContactList.get(position);
                uid = contact.get("title").toString();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("nickname", uid);
                intent.putExtra("type", "single");
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


    /**
     * 刷新消息列表
     * @param context
     */
    private void RefreshContactList(Context context) {
        ContactList.clear();

        try {
            manRecords = LitePal.findAll(recentMan.class);
            int num = manRecords.size();
            for(int i=0;i<num;i++){
                recentMan manRecord = manRecords.get(i);
                Cursor query = writableDatabase.query("chat", null, "toID=?",
                        new String[]{manRecord.getUid()}, null, null, "time desc");
                query.moveToFirst();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", manRecord.getUid());
                map.put("content", query.getString(query.getColumnIndex("contentChat")));
                map.put("time", formatTime(query.getString(query.getColumnIndex("time"))));
                ContactList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        initContactListView();
/*现在要改的是要将各个数据库的初始化环节整理一下，
    对于chat表，建议单独在MainActivity写一个广播接受类，在接受的时候，就能够写库
    对于recentMan表，也是如此，在广播接受的时候，写库
*/
    }

    /**
     * 将时间戳转换成分秒时
     * @param timeMillis
     * @return
     */
    private String formatTime(String timeMillis) {
        long timeMillisl=Long.parseLong(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillisl);
        return simpleDateFormat.format(date);
    }

    /**
     * 初始化ListView
     */
    private void initContactListView () {
        BaseAdapter adapter = new MessageAdapter(context, ContactList);
        listView.setAdapter(adapter);
        listView.setSelection(ContactList.size());
        ListViewUtils.setListViewHeightBasedOnChildren(listView);
    }


    /**
     * 初始化广播接收器
     */
    private void initReceiver(){
        chatReceiver = new ChatReceiver();
        IntentFilter filter = new IntentFilter("com.beidouapp.callback.content");
        getActivity().registerReceiver(chatReceiver, filter);
    }
    /**
     * 聊天消息广播接收器
     */
    private class ChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String message = intent.getStringExtra("message");
//            Log.d("WebSocket", "onReceive" + message);
//            Message4Receive message4Receive = JSONUtils.receiveJSON(message);
//            if (message4Receive.getType().equals("MSG")) {
//                if (message4Receive.getReceiveType().equals("group")) {
//                    initContactListView();
//
//                } else {
//                    //插入数据库
//                    ContentValues values = new ContentValues();
//                    long timeMillis1 = System.currentTimeMillis();
//                    values.put("toID", message4Receive.getData().getSendUserId());
//                    values.put("flag", 0);//别人发的是0
//                    values.put("contentChat", message4Receive.getData().getSendText());
//                    values.put("message_type", "text");
//                    values.put("time", String.valueOf(timeMillis1));
//                    writableDatabase.insert("chat", null, values);
            RefreshContactList(getActivity().getApplicationContext());
        }
//
//            }
//        }
    }

}