package com.beidouapp.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beidouapp.R;
import com.beidouapp.model.DataBase.starposDB;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import org.litepal.LitePal;
import com.beidouapp.model.DataBase.starLocFormOtherDB;
import com.beidouapp.model.adapters.otherStarLocAdapter;

import java.util.List;

import stream.customalert.CustomAlertDialogue;

/**
 * 此activity的作用是将页面中选择的发布点添加到收藏收藏数据库中即starposDB，此activity中的数据来源是starLocFormOtherDB数据库‘
 * starLocFormOtherDB数据库数据来源于网络中的post得到
 */
public class otherStarActivity extends AppCompatActivity {


    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List<starLocFormOtherDB> starLocFormOtherDBS;
    private List<starLocFormOtherDB> starLocFormOtherDBSFather;
    private otherStarLocAdapter otherStarLocAdapter;
    private DemoApplication application;
    private Intent intent;
    private int i;
    private int numOfstarLocFormOtherDBSFather;
    private int order;
    private String searchKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherstar);
        searchKey = getIntent().getStringExtra("searchKey");
        Log.d("zw", "onCreate: 搜寻关键字为" + searchKey);
        ini();

    }

    private void ini() {
        i = 1;
        application = (DemoApplication) getApplicationContext();
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(true);
        if (searchKey == null || searchKey.isEmpty()){
        starLocFormOtherDBSFather = LitePal.findAll(starLocFormOtherDB.class);
        }else{
            starLocFormOtherDBSFather = LitePal.where("tag=?", searchKey).find(starLocFormOtherDB.class);
        }
        numOfstarLocFormOtherDBSFather = starLocFormOtherDBSFather.size();
        if (numOfstarLocFormOtherDBSFather < 5){
            starLocFormOtherDBS = starLocFormOtherDBSFather.subList(0,numOfstarLocFormOtherDBSFather);
        }else {
            starLocFormOtherDBS = starLocFormOtherDBSFather.subList(0,i*5);
        }
        order = numOfstarLocFormOtherDBSFather/5;
        otherStarLocAdapter = new otherStarLocAdapter(starLocFormOtherDBS);
        recyclerView.setAdapter(otherStarLocAdapter);
        otherStarLocAdapter.setOnItemClickListener(new otherStarLocAdapter.setOnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                try {
                    CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(otherStarActivity.this)
                            .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                            .setCancelable(false)
                            .setTitle("添加")
                            .setMessage("添加到个人收藏点?")
                            .setPositiveText("确定")
                            .setPositiveColor(R.color.negative)
                            .setPositiveTypeface(Typeface.DEFAULT_BOLD)
                            .setOnPositiveClicked(new CustomAlertDialogue.OnPositiveClicked() {
                                @Override
                                public void OnClick(View view, Dialog dialog) {
                                    //写入到显示个人收藏点的库
                                    List<starposDB> starposDBS = LitePal.where("selfID=? and text=?", application.getUserID(),
                                            starLocFormOtherDBS.get(pos).getText()).find(starposDB.class);
                                    if (starposDBS.isEmpty()){
                                        starposDB starposDB = new starposDB();
                                        starposDB.setLatitude(starLocFormOtherDBS.get(pos).getLatitude());
                                        starposDB.setStatus("1");
                                        starposDB.setLegend(starLocFormOtherDBS.get(pos).getLegend());
                                        starposDB.setLocInfo(starLocFormOtherDBS.get(pos).getLocInfo());
                                        starposDB.setTag(starLocFormOtherDBS.get(pos).getTag());
                                        starposDB.setText(starLocFormOtherDBS.get(pos).getText());
                                        starposDB.setLontitude(starLocFormOtherDBS.get(pos).getLontitude());
                                        starposDB.setSelfID(application.getUserID());
                                        starposDB.setUid(starLocFormOtherDBS.get(pos).getUid());
                                        starposDB.save();
                                    }else{
                                        Toast.makeText(otherStarActivity.this, "已经存储过", Toast.LENGTH_LONG).show();
                                    }

                                    dialog.dismiss();
                                    intent = getIntent();
                                    setResult(1, intent);
                                    finish();
                                }
                            })
                            .setNegativeText("取消")
                            .setNegativeColor(R.color.positive)
                            .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                                @Override
                                public void OnClick(View view, Dialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .setDecorView(getWindow().getDecorView())
                            .build();
                    alert.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                Log.d("zw", "onRefresh: 刷新");
                recyclerView.setAdapter(otherStarLocAdapter);
                refreshLayout.finishRefresh();
                //refresh时，首先post，然后写库，最后重新加载
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //刷新一次，获得后面5个
                if(i<order){
                    Log.d("zw", "onLoadMore: 此时正在加载更多" + i + "  " + numOfstarLocFormOtherDBSFather);
                    i++;
                    Log.d("zw", "onLoadMore: 此时正在加载更多，接下来" + i);
                    starLocFormOtherDBS = starLocFormOtherDBSFather.subList(0,i*5);
//                    otherStarLocAdapter = new otherStarLocAdapter(starLocFormOtherDBS);
//                    recyclerView.setAdapter(otherStarLocAdapter);
                    otherStarLocAdapter.addMore(starLocFormOtherDBS);
                    refreshLayout.finishLoadMore();
                }else if(i == order){
                    i++;
                    starLocFormOtherDBS = starLocFormOtherDBSFather.subList(0,numOfstarLocFormOtherDBSFather);
//                    otherStarLocAdapter = new otherStarLocAdapter(starLocFormOtherDBS);
//                    recyclerView.setAdapter(otherStarLocAdapter);
                    otherStarLocAdapter.addMore(starLocFormOtherDBS);
                    refreshLayout.finishLoadMore();
                }else {
                    refreshLayout.finishLoadMore();
                }
            }
        });
    }
}
