package com.beidouapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
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
import com.beidouapp.model.adapters.MessageAdapter;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.ui.ChatActivity;

import java.io.IOException;
import java.util.ArrayList;
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

    @Nullable
    @Override
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
    public void onResume() {
        super.onResume();
        isGetData = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        OkHttpUtils.getInstance(context)
                .get("http://120.27.242.92:8080/users", new OkHttpUtils.MyCallback() {
                    @Override
                    public void success(Response response) throws IOException {
                        JSONObject object = JSON.parseObject(response.body().string());
                        int code = object.getInteger("code");
                        if (code == 200) {
                            JSONArray array = (JSONArray) object.get("data");
                            List<String> list = (List<String>) JSONArray.parseArray(array.toString(),String.class);
                            int size = list.size();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ContactList.clear();
                                    for (int i = 0; i < size; i++) {
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("title", list.get(i));
                                        map.put("content", "How are u");
                                        map.put("time", "2022:1:11");
                                        ContactList.add(map);
                                        Log.d("contactlist", ContactList.toString());
                                    }
                                    initContactListView();
                                }
                            });
                        }
                    }

                    @Override
                    public void failed(IOException e) {
                        Log.d("getmsg", e.getMessage());
                    }
                });

    }

    /**
     * 初始化ListView
     */
    private void initContactListView () {
        BaseAdapter adapter = new MessageAdapter(context, ContactList);
        listView.setAdapter(adapter);
        listView.setSelection(ContactList.size());
    }



}