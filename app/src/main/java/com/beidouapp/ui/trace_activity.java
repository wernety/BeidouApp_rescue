package com.beidouapp.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

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
import com.beidouapp.model.adapters.locOthers;
import com.beidouapp.model.adapters.traceAdapter;
import com.beidouapp.model.messages.onlinestetusON;
import com.beidouapp.model.utils.OkHttpUtils;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Response;

public class trace_activity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private String token;
    private List<Relation> relationList;
    private List<String> idList = new ArrayList<String>();
    //    private RelationAdapter relationAdapter;
    private locOthers locOthersAdapter;
    private Button btn_confirmed;
    private String bodyOtherLoc; //状态信息
    private com.beidouapp.model.messages.onlinestetusON onlinestetusON;
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
    private traceAdapter traceAdapter;
    private Context context;
    private CardDatePickerDialog cardDatePickerDialog;
    private CardDatePickerDialog cardDatePickerDialog2;
    private Handler handler1;
    private Bundle bundleForIntent;
    private Intent intentForTrace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_loc);
        context = this;
        ini();

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
        //不显示状态信息，没必要对bodyOtherloc进行解析

        btn_confirmed = findViewById(R.id.btn_loc);
        btn_confirmed.setOnClickListener(this);
        recyclerView = findViewById(R.id.rvLocData);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void RefreshRecyclerView(Context context, String token) {
        OkHttpUtils.getInstance(context).get("http://139.196.122.222:8080/system/dept/user", token, new OkHttpUtils.MyCallback() {
            @Override
            public void success(Response response) throws IOException {
                orgRecord = response.body().string();
                //在第四个图标当中，没必要写库，运涵那边将结构何时写入库中
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

    /**
     * 设置Adapter
     */
    private void initRelationRecyclerView() {
        //使用的Adapter同locOther相似，区别在于不会显示在线或者离线情况，并且点击一个check的时候，直接在HomeFragment执行一个回调函数，显示轨迹
        //点击确定按钮则退出当前activity
        Log.d("zw", "initRelationRecyclerView: 加载Adapter");
        traceAdapter = new traceAdapter(relationList, this);
        recyclerView.setAdapter(traceAdapter);
    }

    /**
     * 重写回调函数
     */
    private void initRelationListener() {
        Log.d("zw", "initRelationListener: 此时开始执行回调函数");
        traceAdapter.setOnItemClickListener(new traceAdapter.OnItemClickListener() {
            @Override
            public void onCheckClick(View v, int pos) {
                //单击事件设置选中状态，然后执行onActivityResult,其中requestCode为2，resultCode为1
                Relation relation = relationList.get(pos);
                intentForTrace = getIntent();
                bundleForIntent = new Bundle();
                bundleForIntent.putString("tracerID", relation.getId());
//                Log.d("zw", "onCheckClick: 此时传输的ID是" + relation.getId());
//                intent.putExtras(bundle);
//                setResult(1, intent);
                traceAdapter.checkOrUncheck(relationList, pos);
                traceAdapter.notifyDataSetChanged();
                if(relation.isCheck()){
                    //当选中时候，弹出一个选择框，确定选择日期，转换成时间戳，传回HomeFragment
                    handlerIni();
                    CardDatePickerDialog.Builder builder = new CardDatePickerDialog.Builder(context);
                    builder.setTitle("选择起始日期");
                    builder.showFocusDateInfo(true);
                    builder.setOnChoose("确定", aLong->{
                        Log.d("zw", "onCheckClick: 开始日期选择成功");
                        Log.d("zw", "onCheckClick: 此时选择的开始日期是(精确到ms)" + aLong);
                        bundleForIntent.putLong("start", aLong);
                        Message message = new Message();
                        message.what = 1;
                        handler1.sendMessage(message);
                    });
                    builder.setOnCancel("取消", new CardDatePickerDialog.OnCancelListener() {
                        @Override
                        public void onCancel() {

                        }
                    });
                    builder.showBackNow(false);
                    cardDatePickerDialog = builder.build();
                    cardDatePickerDialog.show();


//

//                    onStop();
//                    finish();
                }
            }

            @Override
            public void onOpenChildClick(View v, int pos) {
                Relation relation = relationList.get(pos);
                if (relation.getChildren() != null) {
                    traceAdapter.setOpenOrClose(relationList, pos);
                    traceAdapter.notifyDataSetChanged();
                } else {
                    onCheckClick(v, pos);
                }
            }
        });
        traceAdapter.notifyDataSetChanged();
    }

    /**
     * 第二步，选择终止时间
     */
    private void handlerIni() {
        handler1 = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message message) {
                switch (message.what){
                    case 1:{
                        CardDatePickerDialog.Builder builder = new CardDatePickerDialog.Builder(context);
                        builder.setTitle("选择结束日期");
                        builder.showFocusDateInfo(true);
                        builder.setOnChoose("确定", aLong->{
                            Log.d("zw", "onCheckClick: 结束日期选择成功");
                            Log.d("zw", "onCheckClick: 此时选择的结束日期是" + aLong);
                            bundleForIntent.putLong("end", aLong);
                            intentForTrace.putExtras(bundleForIntent);
                            setResult(1, intentForTrace);
                            onStop();
                            finish();
                        });
                        builder.setOnCancel("取消", new CardDatePickerDialog.OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        });
                        builder.showBackNow(false);
                        cardDatePickerDialog2 = builder.build();
                        cardDatePickerDialog2.show();
                    }
                    default:{break;}
                }
            }
        };
    }

    private void transform(Relation rel) {
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
    public void onClick(View v) {
        
    }
}
