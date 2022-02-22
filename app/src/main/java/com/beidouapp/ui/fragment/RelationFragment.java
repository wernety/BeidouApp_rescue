package com.beidouapp.ui.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.model.Relation;
import com.beidouapp.model.User;
import com.beidouapp.model.adapters.FriendListAdapter;
import com.beidouapp.model.adapters.GroupListAdapter;
import com.beidouapp.model.adapters.RelationAdapter;
import com.beidouapp.model.messages.Friend;
import com.beidouapp.model.messages.Group;
import com.beidouapp.model.utils.ListViewUtils;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.utils.id2name;
import com.beidouapp.ui.ChatActivity;
import com.beidouapp.ui.DemoApplication;
import com.beidouapp.ui.add_friend;
import com.beidouapp.ui.add_group;
import com.beidouapp.ui.friend_info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * Fragment：
 * 组织架构及联系人
 */
public class RelationFragment extends Fragment {

    private Context context;
    private View view;
    private ImageView more;
    private RecyclerView recyclerView;
    private ListView friendListView;
    private ListView groupListView;
    private List<Relation>  relationList = new ArrayList<>();
    private List<Friend> friendList = new ArrayList<Friend>();
    private List<Group> groupList = new ArrayList<Group>();
    private RelationAdapter relationAdapter;
    private FriendListAdapter friendListAdapter;
    private GroupListAdapter groupListAdapter;
    private DemoApplication application;
    private String token;
    private String loginId;
    private SQLiteDatabase writableDatabase;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        application = (DemoApplication) getActivity().getApplicationContext();
        token = application.getToken();
        loginId = application.getUserID();
        writableDatabase = application.dbHelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.fragment_relation, container, false);
        Log.d("token", token);
        initUI();
        initListener();
        RefreshView(context, token);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshView(context, token);
    }

    /**
     * 初始化UI控件
     */
    private void initUI() {
        more = (ImageView) view.findViewById(R.id.iv_more_relation);
        recyclerView = view.findViewById(R.id.rvData);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));// 设定类型样式
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));// 设定顶类型样式
        friendListView = view.findViewById(R.id.friendList);
        groupListView = view.findViewById(R.id.groupList);
    }


    private void initListener() {
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Friend friend = friendList.get(position);
                String id = friend.getUserName();
                String nickname = friend.getNickName();
                Intent intent = new Intent(context, friend_info.class);
                intent.putExtra("uid", id);
                intent.putExtra("nickname", nickname);
                intent.putExtra("type", "single");
                intent.putExtra("loginId", loginId);
                startActivity(intent);
            }
        });
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Group group = groupList.get(position);
                String id = group.getSelfGroupId();
                String nickname = group.getSelfGroupName();
                OkHttpUtils.getInstance(context).get("http://120.27.242.92:8080/groupusers/" + id, new OkHttpUtils.MyCallback() {
                    @Override
                    public void success(Response response) throws IOException {
                        String groupInfo = response.body().string();
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("uid", id);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("type", "group");
                        intent.putExtra("groupInfo", groupInfo);
                        startActivity(intent);
                    }
                    @Override
                    public void failed(IOException e) {

                    }
                });

            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.pop_out, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.add_friends: {
                                Intent intent = new Intent(context, add_friend.class);
                                intent.putExtra("id", loginId);
                                intent.putExtra("token", token);
                                startActivity(intent);
                                break;
                            }
                            case R.id.add_group: {
                                Intent intent = new Intent(context, add_group.class);
                                intent.putExtra("id", loginId);
                                intent.putExtra("token", token);
                                startActivity(intent);
                                break;
                            }
                        }
                        return true;
                    }
                });

                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu popupMenu) {

                    }
                });

                popupMenu.show();
            }
        });
    }


    /**
     * 初始化relation监听器
     */
    private void initRelationListener() {
        relationAdapter.setOnItemClickListener(new RelationAdapter.OnItemClickListener() {
            @Override
            public void onCheckClick(View v, int pos) {
                Relation relation = relationList.get(pos);
                String id = relation.getId();
                String nickname = relation.getLabel();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("uid", id);
                intent.putExtra("nickname", nickname);
                intent.putExtra("type", "single");
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
     * 初始化组织架构View
     */
    private void initRelationRecyclerView() {
        relationAdapter = new RelationAdapter(relationList, context);
        recyclerView.setAdapter(relationAdapter);
    }

    /**
     * 初始化好友列表View
     */
    private void initFriendListView() {
        friendListAdapter = new FriendListAdapter(context, friendList);
        friendListView.setAdapter(friendListAdapter);
        friendListView.setSelection(friendList.size());
        ListViewUtils.setListViewHeightBasedOnChildren(friendListView);
    }

    /**
     * 初始化群组列表View
     */
    private void initGroupListView() {
        groupListAdapter = new GroupListAdapter(context, groupList);
        groupListView.setAdapter(groupListAdapter);
        groupListView.setSelection(groupList.size());
        ListViewUtils.setListViewHeightBasedOnChildren(groupListView);
    }


    /**
     * 组织关系json格式转换
     * @param rel
     */
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


    /**
     * 刷新所有View
     * @param context
     * @param token
     */
    public void RefreshView(Context context, String token) {
        RefreshListView(context,token);
        RefreshRecyclerView(context, token);
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
                    getActivity().runOnUiThread(new Runnable() {
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


    public void RefreshListView(Context context, String token) {
        OkHttpUtils.getInstance(context).get("http://139.196.122.222:8080/system/user/3", token,
                new OkHttpUtils.MyCallback() {
                    @Override
                    public void success(Response response) throws IOException {
                        String body = response.body().string();
                        JSONObject object = JSON.parseObject(body);
                        Log.d("zzzml", body);
                        int code = object.getInteger("code");
                        if (code == 200) {
                            JSONArray groupArray = (JSONArray) object.get("selfGroup");
                            List<Group> groups = (List<Group>) JSONArray.parseArray(groupArray.toString(), Group.class);
                            JSONArray friendArray = (JSONArray) object.get("friends");
                            List<Friend> friends = (List<Friend>) JSONArray.parseArray(friendArray.toString(), Friend.class);
                            Log.d("zzzml", friends.toString());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    friendList = friends;
                                    groupList = groups;
                                    initFriendListView();
                                    initGroupListView();
                                }
                            });

                            Friend friend;
                            int size = friends.size();
//                            writableDatabase.delete("friend",null,null);
                            for (int i = 0; i < size; i++) {
                                friend = friends.get(i);
                                id2name.write2DB(writableDatabase,loginId,
                                        friend.getUserName(),
                                        friend.getNickName(),
                                        "1");
                            }
                            Group group;
                            size = groups.size();
                            for (int i = 0; i < size; i++) {
                                group = groups.get(i);
                                id2name.write2DB(writableDatabase,loginId,
                                        group.getSelfGroupId(),
                                        group.getSelfGroupName(),
                                        "1");
                            }

                        }
                    }

                    @Override
                    public void failed(IOException e) {

                    }
                });
    }

    public void RefreshFriendListView(Context context) {
        OkHttpUtils.getInstance(context).get("http://120.27.242.92:8080/friends/" + loginId, new OkHttpUtils.MyCallback() {
            @Override
            public void success(Response response) throws IOException {
                JSONObject object = JSON.parseObject(response.body().string());
                int code = object.getInteger("code");
                if (code == 200) {
                    JSONArray array = (JSONArray) object.get("data");
                    List<Friend> friends = (List<Friend>) JSONArray.parseArray(array.toString(), Friend.class);
                    int size = friends.size();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            friendList = friends;
                            for (int i=0;i<size;i++){
                                Log.d("ok", friendList.get(i).getNickName());
                            }
                            initFriendListView();
                        }
                    });
                }


            }

            @Override
            public void failed(IOException e) {
            }
        });
    }

    public void RefreshGroupListView(Context context) {
        OkHttpUtils.getInstance(context).get("http://139.196.122.222:8080/beisan/selfgroup/list/3", token,new OkHttpUtils.MyCallback() {
            @Override
            public void success(Response response) throws IOException {
                //Log.d("group", response.body().string());
                String body = response.body().string();
                JSONObject object = JSON.parseObject(body);
                Log.d("zzzml", body);
                int code = object.getInteger("code");
                if (code == 200) {
                    JSONArray array = (JSONArray) object.get("data");
                    Log.d("group", array.toString());
                    List<Group> groups = (List<Group>) JSONArray.parseArray(array.toString(), Group.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            groupList = groups;
                            initGroupListView();
                        }
                    });
                }
            }

            @Override
            public void failed(IOException e) {
                e.printStackTrace();
                Log.d("zzzml", "failed");
            }
        });
    }

}