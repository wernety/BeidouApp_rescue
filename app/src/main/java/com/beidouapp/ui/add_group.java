package com.beidouapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.beidouapp.model.Relation;
import com.beidouapp.model.User;
import com.beidouapp.model.adapters.Add2GroupAdapter;
import com.beidouapp.model.adapters.Add2GroupRelAdapter;
import com.beidouapp.model.messages.Friend;
import com.beidouapp.model.utils.ListViewUtils;
import com.beidouapp.model.utils.OkHttpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class add_group extends AppCompatActivity {
    private ListView listView;
    private RecyclerView recyclerView;
    private List<Friend> friendList = new ArrayList<Friend>();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_group);
        Intent intent = getIntent();
        loginId = intent.getStringExtra("id");
        token = intent.getStringExtra("token");


        initUI();
        RefreshFriendListView(this);
        RefreshRecyclerView(this, token);
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
                            OkHttpUtils.getInstance(add_group.this).post("http://120.27.242.92:8080/friends",
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
                                                for (int i = 0; i < size_friend; i++) {
                                                    Friend temp = friendList.get(i);
                                                    if (temp.isChecked()) {
                                                        JSONObject jsonObject = new JSONObject();
                                                        jsonObject.put("groupId", groupId);
                                                        jsonObject.put("userId", temp.getUserName());
                                                        jsonObject.put("userName", temp.getNickName());
                                                        jsonArray.add(jsonObject);
                                                    }
                                                }
                                                for (int i = 0; i < size_relation; i++) {
                                                    Relation temp = relationList.get(i);
                                                    if (temp.isCheck()) {
                                                        JSONObject jsonObject = new JSONObject();
                                                        jsonObject.put("groupId", groupId);
                                                        jsonObject.put("userId", temp.getId());
                                                        jsonObject.put("userName", temp.getLabel());
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
                            friendList = friends;
                            for (int i=0;i<size;i++){
                                Log.d("zzzz", friendList.get(i).getNickName());
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
                        transform(list.get(i));
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

    public void transform(Relation rel) {
        //判断是否做过转换
        if (!rel.isTransformed()) {
            //判断有没有成员
            if (rel.getMember() != null && rel.getMember().size() > 0) {
                //如果没有叶子列表，就创建叶子列表
                if (rel.getChildren() == null) {
                    List <Relation> children = new ArrayList<>();
                    rel.setChildren(children);
                }
                //将成员作为叶子加入叶子列表中
                List<User> memberList = rel.getMember();
                int size_m = memberList.size();
                User temp;
                Relation relation;
                String Id;
                String parentId;
                String label;
                List<Relation> list = rel.getChildren();
                for (int i = 0; i < size_m; i++) {
                    temp = memberList.get(i);
                    Id = temp.getUserName();
                    parentId = temp.getDeptId();
                    label = temp.getNickName();
                    relation = new Relation(Id, parentId, label);
                    relation.setTransformed(true);
                    list.add(relation);
                    Log.d("relation", label + parentId + Id);
                }
                rel.setChildren(list);
            }
            rel.setTransformed(true);
        }
        if (rel.getChildren() != null && rel.getChildren().size() > 0) {
            int size_c = rel.getChildren().size();
            for (int i = 0; i < size_c; i++) {
                transform(rel.getChildren().get(i));
            }
        }
    }
}