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
import com.beidouapp.model.DataBase.orgAndUidAndKey;
import com.beidouapp.model.Relation;
import com.beidouapp.model.User;
import com.beidouapp.model.adapters.RelationAdapter;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.adapters.locOthers;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.messages.onlinestetusON;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;


//在首页当中显示的组织架构
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
    private String curToken;
    private orgAndUidAndKey record;
    private String uid;
    private List<orgAndUidAndKey> records;
    private String orgRecord;
    private String org;
    private List<String> deviceIDs;
    private List<Integer> onlineStatus;
    private String pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_loc);

        ini();

//        Log.d("zw", "onCreate: 需要读取状态的其他人" + onlinestetusON.getDeviceId());



        btn_loc = findViewById(R.id.btn_loc);
        btn_loc.setOnClickListener(this);

        recyclerView = findViewById(R.id.rvLocData);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RefreshRecyclerView(this, token);
    }

    private void ini() {
        Bundle bundle = this.getIntent().getExtras();
        token = bundle.getString("token");
        curToken = bundle.getString("curToken");
        bodyOtherLoc = bundle.getString("status");
        uid = bundle.getString("uid");
        pass = bundle.getString("pass");
        org = bundle.getString("org");
        cntDeviceID = 0;

//        Log.d("zw", "onCreate: 从亮哥那儿拿到的消息，显示在other_loc中：" + bodyOtherLoc);
        onlinestetusON = JSONUtils.receiveOnlinestetusONJson(bodyOtherLoc);
        try {
            cntDeviceID = onlinestetusON.getTotalCount();
            deviceIDs = onlinestetusON.getDeviceId();
            onlineStatus = onlinestetusON.getStatus();
        }catch (Exception e){
            cntDeviceID = 0;
            deviceIDs = new ArrayList<String>();
            onlineStatus = new ArrayList<Integer>();
        }
    }

    public void RefreshRecyclerView (Context context, String token) {
        OkHttpUtils.getInstance(context).get("http://139.196.122.222:8080/system/dept/user", token, new OkHttpUtils.MyCallback() {
            @Override
            public void success(Response response) throws IOException {
                orgRecord = response.body().string();
                writeToDb();  //储存至数据库
                JSONObject object = JSON.parseObject(orgRecord);
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
                            Log.d("zw", "run: relationlist里面的数据是" + relationList.toString());
                            initRelationRecyclerView();
                            initRelationListener();
                        }
                    });

                }
            }
            @Override
            public void failed(IOException e) {
                orgRecord = org;
                JSONObject object = JSON.parseObject(orgRecord);
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
                            Log.d("zw", "run: relationlist里面的数据是" + relationList.toString());
                            initRelationRecyclerView();
                            initRelationListener();
                        }
                    });

                }
            }
        });
    }

    private void initRelationRecyclerView() {
        locOthersAdapter = new locOthers(relationList,this, deviceIDs, onlineStatus);
        recyclerView.setAdapter(locOthersAdapter);
    }

    private void initRelationListener() {
        locOthersAdapter.setOnItemClickListener(new locOthers.OnItemClickListener() {
            @Override
            //这里是候选框
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
                }
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

    //这里是将数据储存至数据库当中，从而使得没有网络的时候，也有可操作空间，初始化数据库在MainActivity中，注意这里只有打开这个activity才能储存
    private void writeToDb() {
        records = LitePal.where("uid = ?", uid).find(orgAndUidAndKey.class);
        Log.d("zw", "writeToDb: 此时的记录是" + records);
        if(records.isEmpty())
        {
            record = new orgAndUidAndKey();
            record.setOrg(orgRecord);  //结构应该使用字符串加入进去，直接使用的是服务器回传的response里的body的String
            record.setCurToken(curToken);
            record.setUid(uid);
            record.setPass(pass);
            record.save();
            Log.d("zw", "writeToDb: 此时数据库为空，初次设置库：");
//            record.setPass(); //这里是设置密码，可以用来验证登录
        }else{
            //如果存在此账号，修改该账号下的所有信息
            record = records.get(0);    //首先获取这条记录
            record.setOrg(orgRecord);
            record.setCurToken(curToken);
            record.setPass(pass);
            record.save();
            Log.d("zw", "writeToDb: 此时的记录是：" + record.getPass() + " 用户是 " + record.getUid());
        }
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
