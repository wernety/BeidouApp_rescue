package com.beidouapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.model.adapters.Add2GroupAdapter;
import com.beidouapp.model.messages.Friend;
import com.beidouapp.model.utils.OkHttpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class add_group extends AppCompatActivity {
    private ListView listView;
    private List<Friend> personList = new ArrayList<Friend>();
    private Add2GroupAdapter adapter;
    private ImageView back;
    private EditText editText;
    private Button confirm;
    private String loginId;
    private String groupName;
    private String groupId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_group);
        Intent intent = getIntent();
        loginId = intent.getStringExtra("id");


        initUI();
        RefreshFriendListView(this);
        initListener();
    }


    /**
     * 初始化UI
     */
    private void initUI() {
        listView = (ListView) findViewById(R.id.lv_add_group);
        back = (ImageView) findViewById(R.id.iv_back_add_group);
        confirm = (Button) findViewById(R.id.btn_add2group);
        editText = (EditText) findViewById(R.id.edt_add_group);
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = personList.size();
                JSONArray jsonArray = new JSONArray();

                Handler handler = new Handler() {
                    //           @Override
                    @SuppressLint("HandlerLeak")
                    public void handleMessage(Message message) {
                        if (message.what == 1) {
                            try {
                                OkHttpUtils.getInstance(add_group.this).post("http://120.27.242.92:8080/groupusers",
                                        jsonArray.toJSONString(), new OkHttpUtils.MyCallback() {
                                            @Override
                                            public void success(Response response) throws IOException {
                                                JSONObject object = JSON.parseObject(response.body().string());
                                                int code = object.getInteger("code");
                                                if (code == 200) {
                                                    finish();
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
                    }
                };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            groupName = editText.getText().toString();
                            JSONObject object = new JSONObject();
                            object.put("name", groupName);
                            object.put("adminId", loginId);
                            Log.d("zz",groupName);
                            Log.d("zz",loginId);
                            Log.d("zz",object.toJSONString());
                            OkHttpUtils.getInstance(add_group.this).post("http://120.27.242.92:8080/groups",
                                    object.toJSONString(), new OkHttpUtils.MyCallback() {
                                        @Override
                                        public void success(Response response) throws IOException {
                                            Log.d("zz","创建群聊中");
                                            JSONObject object = JSON.parseObject(response.body().string());
                                            int code = object.getInteger("code");
                                            if (code == 200) {
                                                groupId = object.getString("data");
                                                JSONObject me = new JSONObject();
                                                me.put("groupId", groupId);
                                                me.put("userId", loginId);
                                                jsonArray.add(me);
                                                for (int i = 0; i < size; i++) {
                                                    Friend temp = personList.get(i);
                                                    if (temp.isChecked()) {
                                                        JSONObject jsonObject = new JSONObject();
                                                        jsonObject.put("groupId", groupId);
                                                        jsonObject.put("userId", temp.getFriendId());
                                                        jsonObject.put("userName", temp.getFriendName());
                                                        jsonArray.add(jsonObject);
                                                    }
                                                }
                                                Message message = new Message();
                                                message.what = 1;
                                                handler.sendMessage(message);
                                            }
                                        }

                                        @Override
                                        public void failed(IOException e) {

                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("zz", "run: 线程炸了");
                        }


                    }
                }).start();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                boolean status = personList.get(position).isChecked();
                personList.get(position).setChecked(!status);
            }
        });
    }

    /**
     * 初始化listview
     */
    private void initListView() {
        adapter = new Add2GroupAdapter(add_group.this, personList);
        listView.setAdapter(adapter);
        listView.setSelection(personList.size());
    }


    public void RefreshFriendListView(Context context) {
        OkHttpUtils.getInstance(context).get("http://120.27.242.92:8080/friends/" + loginId, new OkHttpUtils.MyCallback() {
            @Override
            public void success(Response response) throws IOException {
                JSONObject object = JSON.parseObject(response.body().string());
                int code = object.getInteger("code");
                if (code == 200) {
                    JSONArray array = (JSONArray) object.get("data");
                    Log.d("zzzz", array.toString());
                    List<Friend> friends = (List<Friend>) JSONArray.parseArray(array.toString(), Friend.class);
                    int size = friends.size();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            personList = friends;
                            for (int i=0;i<size;i++){
                                Log.d("zzzz", personList.get(i).getFriendName());
                            }
                            initListView();
                        }
                    });

                }
                Log.d("zzzz","zzzzzz");


            }

            @Override
            public void failed(IOException e) {
            }
        });
    }
}