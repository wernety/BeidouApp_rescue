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
import com.beidouapp.model.utils.id2name;
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
    private String loginId;
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
        loginId = application.getUserID();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                uid = id2name.reverse(writableDatabase,loginId,uid);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("nickname", uid);
                manRecords = LitePal.where("toID=?", uid).find(recentMan.class);
                recentMan manRecord = manRecords.get(0);
                Log.d("zw", "onItemClick: 此时传入到chatActivity的类型是："+manRecord.getType());
                if(manRecord.getType().equals("0")){
                    intent.putExtra("type", "single");
                }else {
                    intent.putExtra("type", "group");
                }
                startActivity(intent);
            }
        });
    }


    /**
     * 刷新消息列表
     * @param context
     */
    private void RefreshContactList(Context context) {
        Cursor query;
        String transId;
        ContactList.clear();

        try {
            manRecords = LitePal.where("selfID=?",application.getUserID()).find(recentMan.class);
            int num = manRecords.size();
            Log.d("zw", "RefreshContactList: litepal数据库记录条数：" + num);
            for(int i=0;i<num;i++){
                recentMan manRecord = manRecords.get(i);
                Log.d("zw", "RefreshContactList: 此时好友类型为" + manRecord.getType());
                if(manRecord.getType().equals("0")){
                    query = writableDatabase.query("chat", null, "toID=? and selfID=?",
                            new String[]{manRecord.getToID(), application.getUserID()}, null, null, "time desc");
                }else{
                    query = writableDatabase.query("chat_group", null, "groupID=? and selfID=?",
                            new String[]{manRecord.getToID(), application.getUserID()}, null, null, "time desc");
                }
                query.moveToFirst();
                Map<String, Object> map = new HashMap<String, Object>();

                transId = id2name.transform(writableDatabase, loginId, manRecord.getToID());
                map.put("title", transId);
                map.put("content", query.getString(query.getColumnIndex("contentChat")));
                map.put("time", formatTime(query.getString(query.getColumnIndex("time"))));
                ContactList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("zw", "RefreshContactList: 加载消息Fragment失败");
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
            Log.d("zz","fragment message received");
            RefreshContactList(getActivity().getApplicationContext());
        }
    }

}