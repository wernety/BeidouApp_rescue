package com.beidouapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.beidouapp.model.Group;
import com.beidouapp.model.Relation;
import com.beidouapp.model.User;
import com.beidouapp.model.adapters.Add2GroupAdapter;
import com.beidouapp.model.adapters.Add2GroupRelAdapter;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.ListViewUtils;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.utils.id2name;
import com.loper7.date_time_picker.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class add_group extends AppCompatActivity {
    private ListView listView;
    private RecyclerView recyclerView;
    private List<User> friendList = new ArrayList<User>();
    private List<Relation>  relationList = new ArrayList<>();
    private Add2GroupRelAdapter relationAdapter;
    private Add2GroupAdapter adapter;
    private ImageView back;
    private EditText editText;
    private Button confirm;
    private String loginId;
    private String groupName;
    private String groupId;
    private String token;
    private String org;
    private DemoApplication application;
    private SQLiteDatabase writableDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_group);
        application = (DemoApplication) this.getApplicationContext();
        Intent intent = getIntent();
        loginId = intent.getStringExtra("id");
        token = application.getToken();
        friendList = application.getFriendList();
        org = application.getOrg();
        writableDatabase = application.dbHelper.getWritableDatabase();

        initUI();
        initView();
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
        recyclerView = findViewById(R.id.rv_add_group);
        recyclerView.setLayoutManager(new LinearLayoutManager(add_group.this, RecyclerView.VERTICAL, false));// 设定类型样式
        recyclerView.addItemDecoration(new DividerItemDecoration(add_group.this, DividerItemDecoration.VERTICAL));// 设定顶类型样式
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
                int size_friend = friendList.size();
                int size_relation = relationList.size();
                HashMap<String, String> dataMap = new HashMap<>();

                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        if (message.what == 1) {
                            try {
                                OkHttpUtils.getInstance(add_group.this).put("http://139.196.122.222:8080/beisan/selfgroup/addSelfGroupUserList",
                                        dataMap, token, new OkHttpUtils.MyCallback() {
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
                                                e.printStackTrace();
                                            }
                                        });
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                        return false;
                    }
                });
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            groupName = editText.getText().toString();
                            JSONObject object = new JSONObject();
                            object.put("selfGroupName", groupName);
                            OkHttpUtils.getInstance(add_group.this).post("http://139.196.122.222:8080/beisan/selfgroup",
                                    object.toJSONString(), new OkHttpUtils.MyCallback() {
                                        @Override
                                        public void success(Response response) throws IOException {
                                            JSONObject object = JSON.parseObject(response.body().string());
                                            int code = object.getInteger("code");
                                            if (code == 200) {
                                                groupId = object.getString("data");
                                                dataMap.put("groupId", groupId);

                                                List<String> userList = new ArrayList<>();
                                                for (int i = 0; i < size_friend; i++) {
                                                    User temp = friendList.get(i);
                                                    if (temp.isChecked()) {
                                                        userList.add(temp.getUserId());
                                                    }
                                                }
                                                for (int i = 0; i < size_relation; i++) {
                                                    Relation temp = relationList.get(i);
                                                    if (temp.isCheck()) {
                                                        userList.add(temp.getUserId());
                                                    }
                                                }
                                                dataMap.put("userIds", userList.toString().replace("[", "").replace("]", ""));
                                                Message message = new Message();
                                                message.what = 1;
                                                handler.sendMessage(message);
                                            }
                                        }

                                        @Override
                                        public void failed(IOException e) {

                                        }
                                    }, token);

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
                View temp = listView.getChildAt(position);
                Add2GroupAdapter.Add2GroupViewHolder viewHolder = (Add2GroupAdapter.Add2GroupViewHolder) temp.getTag();
                boolean isChecked = friendList.get(position).isChecked();
                if (isChecked) {
                    friendList.get(position).setChecked(false);
                    viewHolder.check.setImageResource(R.mipmap.road_check);
                } else {
                    friendList.get(position).setChecked(true);
                    viewHolder.check.setImageResource(R.mipmap.road_checked);
                }
            }
        });
    }

    /**
     * 初始化relation监听器
     */
    private void initRelationListener() {
        relationAdapter.setOnItemClickListener(new Add2GroupRelAdapter.OnItemClickListener() {
            @Override
            public void onCheckClick(View v, int pos) {
                Relation relation = relationList.get(pos);
                relationAdapter.checkOrUncheck(relationList, pos);
                relationAdapter.notifyDataSetChanged();

            }

            @Override
            public void onOpenChildClick(View v, int pos) {
                Relation relation = relationList.get(pos);
                if (relation.getChildren() != null) {
                    relationAdapter.setOpenOrClose(relationList, pos);
                    relationAdapter.notifyDataSetChanged();
                } else {
                    onCheckClick(v, pos);
                }
            }
        });
        relationAdapter.notifyDataSetChanged();
    }


    private void initView() {
        if (friendList==null) {
            RefreshFriendListView(add_group.this);
        } else {
            initListView();
        }
        if (org==null){
            RefreshRecyclerView(add_group.this, token);
        } else {
            Log.d("ZZJY", org);
            JSONObject object = JSON.parseObject(org);
            int code = object.getInteger("code");
            if (code == 200) {
                JSONArray array = (JSONArray) object.get("data");
                List<Relation> list = (List<Relation>) JSONArray.parseArray(array.toString(),Relation.class);
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    JSONUtils.transform(list.get(i));
                }
                relationList = list;
                initRelationRecyclerView();
                initRelationListener();
            }
        }
    }

    /**
     * 初始化listview
     */
    private void initListView() {
        adapter = new Add2GroupAdapter(add_group.this, friendList);
        listView.setAdapter(adapter);
        listView.setSelection(friendList.size());
        ListViewUtils.setListViewHeightBasedOnChildren(listView);
    }

    /**
     * 初始化组织架构View
     */
    private void initRelationRecyclerView() {
        relationAdapter = new Add2GroupRelAdapter(relationList, add_group.this);
        recyclerView.setAdapter(relationAdapter);
    }


    public void RefreshFriendListView(Context context) {
        OkHttpUtils.getInstance(context).get("http://139.196.122.222:8080/system/user/" + application.getIndexID(), token,
                new OkHttpUtils.MyCallback() {
                    @Override
                    public void success(Response response) throws IOException {
                        String body = response.body().string();
                        JSONObject object = JSON.parseObject(body);
                        Log.d("zzzml", body);
                        int code = object.getInteger("code");
                        if (code == 200) {
                            JSONArray friendArray = (JSONArray) object.get("friends");
                            List<User> friends = (List<User>) JSONArray.parseArray(friendArray.toString(), User.class);
                            Log.d("zzzml", friends.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    friendList = friends;
                                    initListView();
                                }
                            });

                            User friend;
                            int size = friends.size();
//                            writableDatabase.delete("friend",null,null);
                            for (int i = 0; i < size; i++) {
                                friend = friends.get(i);
                                id2name.write2DB(writableDatabase,loginId,
                                        friend.getUserId(),
                                        friend.getUserName(),
                                        friend.getNickName(),
                                        "1");
                            }

                        }
                    }

                    @Override
                    public void failed(IOException e) {

                    }
                });
    }

    /**
     * 刷新组织关系View
     * @param context
     * @param token
     */
    public void RefreshRecyclerView (Context context, String token) {
        OkHttpUtils.getInstance(context).get("http://139.196.122.222:8080/system/dept/user", token, new OkHttpUtils.MyCallback() {
            @Override
            public void success(Response response) throws IOException {
                JSONObject object = JSON.parseObject(response.body().string());
                int code = object.getInteger("code");
                if (code == 200) {
                    JSONArray array = (JSONArray) object.get("data");
                    List<Relation> list = (List<Relation>) JSONArray.parseArray(array.toString(),Relation.class);
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        JSONUtils.transform(list.get(i));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            relationList = list;
                            initRelationRecyclerView();
                            initRelationListener();
                        }
                    });

                }
            }

            @Override
            public void failed(IOException e) {

            }
        });
    }


}