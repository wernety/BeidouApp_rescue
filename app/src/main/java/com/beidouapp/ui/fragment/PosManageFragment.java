package com.beidouapp.ui.fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.model.messages.otherStarLocFromLiang;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.ui.otherStarActivity;
import com.beidouapp.model.DataBase.starLocFormOtherDB;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class PosManageFragment extends Fragment implements View.OnClickListener {


    private Button btnSelfLoc;
    private Button btnStarLoc;
    private Fragment curFragment;
    private selfFragment selfFragment;
    private Fragment lastFragment = null;
    private BackToMainListener BackToMainListener;
    private starFragment starFragment;
    private ImageButton btnAddStarLoc;
    private EditText searchLoc;
    private Thread threadForOtherLoc;
    private String stringOtherLoc;
    private Handler handlerForGetOtherLoc;
    private starLocFormOtherDB starLocFormOtherDB;
    private otherStarLocFromLiang otherStarLocFromLiang;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pos_manage, container, false);
       ini(view);
       curFragment = selfFragment;
       replaceFragement(curFragment);
       return view;
    }

    private void ini(View view) {
        btnStarLoc = view.findViewById(R.id.btn_star);
        btnSelfLoc = view.findViewById(R.id.btn_self);
        btnAddStarLoc = view.findViewById(R.id.add_starLoc);
        searchLoc = view.findViewById(R.id.search_loc);
        btnStarLoc.setOnClickListener(this);
        btnSelfLoc.setOnClickListener(this);
        btnAddStarLoc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_star:{
                starFragment = new starFragment();
                starFragment.setOnFragmentClick(new starFragment.OnFragmentClick() {
                    @Override
                    public void mapNeedChange(starPos starPos) {
                        Log.d("zw", "mapNeedChange: starPos第二次回调成功");
                        if(BackToMainListener != null){
                            Log.d("zw", "mapNeedChange: starPos第三次回调开始");
                            BackToMainListener.changeMap(starPos);
//                    Log.d("zw", "mapNeedChange: 第三次回调的参数是：" + selfPos.toString());
                        }
                    }
                });
                Log.d("zw", "onClick: 切换收藏位置点");
                replaceFragement(starFragment);
                break;
            }
            case R.id.btn_self:{
                Log.d("zw", "onClick: 切换自建位置点");
                selfFragment = new selfFragment();
                selfFragment.setOnFragmentClick(new selfFragment.OnFragmentClick() {
                    @Override
                    public void mapNeedChange(starPos selfPos) {
                        Log.d("zw", "mapNeedChange: 第二次回调成功");
                        if(BackToMainListener != null){
                            Log.d("zw", "mapNeedChange: 第三次回调开始");
                            BackToMainListener.changeMap(selfPos);
//                    Log.d("zw", "mapNeedChange: 第三次回调的参数是：" + selfPos.toString());
                        }
                    }
                });
                replaceFragement(selfFragment);
                break;
            }
            case R.id.add_starLoc:{
                String s = searchLoc.getText().toString();
//                Intent intent = new Intent(getActivity(), otherStarActivity.class);
//                startActivityForResult(intent, 3);
//                if (!s.isEmpty()){
                    //打开新的activity
                    handlerForGetOtherLocIni();
                    threadForOtherLoc = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpUtils.getInstance(getActivity().getApplicationContext()).post("http://120.27.249.235:8081/getPositionByStatus/1",
                                    new OkHttpUtils.MyCallback() {
                                @Override
                                public void success(Response response) throws IOException {
                                    stringOtherLoc = response.body().string();
                                    Log.d("zw", "success: 此时从亮哥那里获取到的收藏点的数据为：" + stringOtherLoc);
//将字符串转换成Json对象
                                    otherStarLocFromLiang = JSONUtils.reciveOtherStarLocFromLiang(stringOtherLoc);
                                    List<com.beidouapp.model.messages.otherStarLocFromLiang.Data> data = otherStarLocFromLiang.getData();
                                    int num = data.size();
                                    for (int i=0;i<num;i++){
                                        //判断记录是否在库中，如果不在库中，写库,如果在库中，则会修改原来的内容
                                        com.beidouapp.model.messages.otherStarLocFromLiang.Data data1 = data.get(i);
//                                        Log.d("zw", "success:此时储存的数据应为 " + data1.getCoordLat());
//                                        Log.d("zw", "success:此时储存的数据应为 " + data1.getCoordLng());
                                        List<starLocFormOtherDB> starLocFormOtherDBS = LitePal.where("latitude=? and lontitude=?",
                                                data1.getCoordLat(), data1.getCoordLng())
                                                .find(starLocFormOtherDB.class);
                                        try {
                                        Log.d("zw", "success: 此时的数据是" + starLocFormOtherDBS );
                                            if (starLocFormOtherDBS.isEmpty()){
                                                starLocFormOtherDB = new starLocFormOtherDB();
                                                starLocFormOtherDB.setLatitude(data1.getCoordLat());
                                                Log.d("zw", "success:此时储存的数据应为 " + data1.getCoordLat());
                                                starLocFormOtherDB.setLontitude(data1.getCoordLng());
                                                Log.d("zw", "success:此时储存的数据应为 " + data1.getCoordLng());
                                                starLocFormOtherDB.setLocInfo(data1.getCoordDesc());
                                                Log.d("zw", "success:此时储存的数据应为 " + data1.getCoordDesc());
                                                starLocFormOtherDB.setStatus("1");
                                                starLocFormOtherDB.setUid(data1.getReleaseUserID());
                                                Log.d("zw", "success:此时储存的数据应为 " + data1.getReleaseUserID());
                                                starLocFormOtherDB.setLegend(data1.getCoordLegend());
                                                Log.d("zw", "success:此时储存的数据应为 " + data1.getCoordLegend());
//                                            starLocFormOtherDB.setTag();//暂时没有这种描述
                                                starLocFormOtherDB.setText(data1.getCoordName());
                                                Log.d("zw", "success:此时储存的数据应为 " + data1.getCoordName());
                                                starLocFormOtherDB.setTag(data1.getCoordTag());
                                                starLocFormOtherDB.save();
                                            }else{
                                                starLocFormOtherDB = starLocFormOtherDBS.get(0);
                                                starLocFormOtherDB.setLocInfo(data1.getCoordDesc());
                                                starLocFormOtherDB.setStatus("1");
                                                starLocFormOtherDB.setUid(data1.getReleaseUserID());
                                                starLocFormOtherDB.setLegend(data1.getCoordLegend());
//                                            starLocFormOtherDB.setTag();//暂时没有这种描述
                                                starLocFormOtherDB.setText(data1.getCoordName());
                                                starLocFormOtherDB.save();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            Log.d("zw", "success: 写从亮哥那里拿来数据进行储存的库失败");}
                                    }
                                    Message messageForOtherLoc = new Message();
                                    messageForOtherLoc.what = 1;
                                    handlerForGetOtherLoc.sendMessage(messageForOtherLoc);
                                }

                                @Override
                                public void failed(IOException e) {
                                    Log.d(TAG, "failed: 获取收藏点网络连接失败");
                                    e.printStackTrace();
                                }
                            });
                        }
                    });
                    threadForOtherLoc.start();
//                }
            }
            default:break;
        }
    }

    /**
     * 初始化获取其他位置的handler
     */
    private void handlerForGetOtherLocIni() {
        handlerForGetOtherLoc = new Handler(){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:{
                        //开启activity
                        Intent intent = new Intent(getActivity(), otherStarActivity.class);
                        startActivityForResult(intent, 3);
                        break;
                    } default:break;
                }
            }
        };
    }

    /**
     * 切换Fragment，当当前的Fragment不为要显示的类容时，判断是否添加，然后切换
     * @param fragment 传入的Fragment
     */
    private void replaceFragement(Fragment fragment) {
        try {
           FragmentManager fragmentManager = getFragmentManager();
           FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!fragment.isAdded()) {
                transaction.add(R.id.down_fragment,fragment);
            }else {
                transaction.show(fragment);
            }
            if (lastFragment !=null){
                transaction.hide(lastFragment);
            }
            lastFragment = fragment;
            transaction.commit();
        }catch (Exception e){
            e.printStackTrace();
            Log.d("zw", "replaceFragement: 切换Fragment出错");
        }
    }


    /**
     *
     * @param requestCode 请求代码是3表示进入otherStarActivity
     * @param resultCode  返回代码是0表示异常情况，返回代码是1或者2表示正常返回
     * @param data 在other_loc当中data数据包含的是请求列表
     *             在otherStarActivity当中data数据为空
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3){
            Log.d("zw", "onActivityResult: 此时是由otherStarActivity传回来的" + resultCode);
            if(resultCode == 1){
                starFragment = new starFragment();
                starFragment.setOnFragmentClick(new starFragment.OnFragmentClick() {
                    @Override
                    public void mapNeedChange(starPos starPos) {
                        Log.d("zw", "mapNeedChange: starPos第二次回调成功");
                        if(BackToMainListener != null){
                            Log.d("zw", "mapNeedChange: starPos第三次回调开始");
                            BackToMainListener.changeMap(starPos);
//                    Log.d("zw", "mapNeedChange: 第三次回调的参数是：" + selfPos.toString());
                        }
                    }
                });
                Log.d("zw", "onActivityResult: 此时重新加载starFragment数据，将前一步选择的数据加载出来");
                replaceFragement(starFragment);
            }
        }
    }



    //回调接口，回调给MainActivity
    public interface BackToMainListener{
        void changeMap(starPos selfPos);
    }


    public void setBackToMainListener(BackToMainListener backToMainListener){
        this.BackToMainListener = backToMainListener;
    }
}