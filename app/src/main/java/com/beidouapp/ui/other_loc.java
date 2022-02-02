package com.beidouapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.model.Relation;
import com.beidouapp.model.User;
import com.beidouapp.model.adapters.RelationAdapter;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.adapters.locOthers;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.messages.onlinestetusON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class other_loc extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private String token;
    private List<Relation> relationList;
    private List<String> idList = new ArrayList<String>();
//    private RelationAdapter relationAdapter;
    private locOthers locOthersAdapter;
    private Button btn_loc;
    private String bodyOtherLoc;
    private onlinestetusON onlinestetusON;
    private int cntDeviceID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_loc);
        Bundle bundle = this.getIntent().getExtras();
        token = bundle.getString("token");
        bodyOtherLoc = bundle.getString("status");
        cntDeviceID = 0;
        Log.d("zw", "onCreate: 从亮哥那儿拿到的消息，显示在other_loc中：" + bodyOtherLoc);
        onlinestetusON = JSONUtils.receiveOnlinestetusONJson(bodyOtherLoc);
        cntDeviceID = onlinestetusON.getTotalCount();
//        Log.d("zw", "onCreate: 需要读取状态的其他人" + onlinestetusON.getDeviceId());



        btn_loc = findViewById(R.id.btn_loc);
        btn_loc.setOnClickListener(this);

        recyclerView = findViewById(R.id.rvLocData);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RefreshRecyclerView(this, token);
    }

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
//                            int i,j;
//                            int num;
//                            num = relationList.size();
                            //
                            Log.d("zw", "run: relationlist里面的数据是" + relationList.toString());
//                            for(i=0;i<cntDeviceID;i++){
//                                for(j=0;j<num;j++)
//                                {
//                                    if(onlinestetusON.getDeviceId().get(i) == relationList.get(j).getId())
//                                    {
//                                        if(onlinestetusON.getStatus().get(i) == 1)
//                                        Log.d("zw", "run: 在状态栏里面改变状态");
//                                        Log.d("zw", "run: 改变状态的ID是" + onlinestetusON.getDeviceId().get(i));
//                                    }
//                                }
//                            }

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

    private void initRelationRecyclerView() {
//        relationAdapter = new RelationAdapter(relationList, this);
//        locOthersAdapter = new locOthers(relationList,this);
        locOthersAdapter = new locOthers(relationList,this, onlinestetusON.getDeviceId(), onlinestetusON.getStatus());
        recyclerView.setAdapter(locOthersAdapter);
    }

    private void initRelationListener() {
        locOthersAdapter.setOnItemClickListener(new locOthers.OnItemClickListener() {
            @Override
            public void onCheckClick(View v, int pos) {

                Relation relation = relationList.get(pos);
                locOthersAdapter.checkOrUncheck(relationList, pos);
                locOthersAdapter.notifyDataSetChanged();
//                Log.d("zw", "onCheckClick: 现在的relation是：" + relation.isCheck());
                if(relation.isCheck()){
                    //这里的逻辑是，当选中的item是check的状态时候，就加入list当中
//                    Log.d("zw", "onCheckClick: 现在的relation是：");
//                    Log.d("zw", "onCheckClick: 现在的relation的id是：" + relation.getId());
                    boolean flag = false;
                    int num = idList.size();
                    for ( int i=0;i<num;i++)
                    {
                        if(idList.get(i) == relation.getId())
                        {
                            flag = true;
                        }
                        else {
                            flag = false;
                        }
                    }
                    if(!flag){
                        idList.add(relation.getId());
                    }
//                    Log.d("zw", "onCheckClick: 现在的idList中的数据有：" + idList.toString());
                }
                else{
//这里设置的是，当选中的目标是uncheck的状态时候，查询list中的数据，然后，如果list中已经有的话，就删除，没有就不管
                    boolean flag = false;
                    int num = idList.size();
                    int delPos = 0;
                    for(int i=0;i<num;i++){
                        if(idList.get(i) == relation.getId())
                        {
                            flag = true;
                            delPos = i;
                        }
                        else {
                            flag = false;
                        }
                    }
                    if(flag){
                        idList.remove(delPos);
                    }
//                    Log.d("zw", "onCheckClick: 删除后现在的idList中的数据有：" + idList.toString());
                }
//                String id = relation.getId();
//                String nickname = relation.getLabel();
//                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra("uid", id);
//                intent.putExtra("nickname", nickname);
//                intent.putExtra("type", "single");
//                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            }

            @Override
            public void onOpenChildClick(View v, int pos) {
//                Log.d("zw", "onOpenChildClick: 点击事件里面的relationlist是：" + relationList.toString());
                Relation relation = relationList.get(pos);
                if (relation.getChildren() != null) {
                    locOthersAdapter.setOpenOrClose(relationList, pos);
                    locOthersAdapter.notifyDataSetChanged();
                } else {
                    onCheckClick(v, pos);
                }
            }
        });
        locOthersAdapter.notifyDataSetChanged();
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

    @Override
    protected void onPause() {
        Log.d("zw", "onDestroy: other_loc的activity得到暂停");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("zw", "onDestroy: other_loc的activity得到终止");
    }

    @Override
    protected void onDestroy() {
        Log.d("zw", "onDestroy: other_loc的activity得到销毁");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loc:{
                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                ArrayList<String> arrayList = new ArrayList<String>(idList);
                bundle.putStringArrayList("pos", arrayList);
                intent.putExtras(bundle);
//                intent.putExtra("pos", "小梓薇");
                setResult(2,intent);
//              下一阶段点击这个按钮实现退出当前页面
//                onPause();
                onStop();
//                onDestroy();
//                this.finish();
                break;
            }
            default:break;
        }
    }
}
