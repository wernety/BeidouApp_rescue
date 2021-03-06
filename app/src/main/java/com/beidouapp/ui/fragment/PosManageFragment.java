package com.beidouapp.ui.fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.icu.util.BuddhistCalendar;
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
import com.baidu.mapapi.model.LatLng;
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
public class PosManageFragment extends Fragment implements View.OnClickListener, SensorEventListener {


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
                        Log.d("zw", "mapNeedChange: starPos?????????????????????");
                        if(BackToMainListener != null){
                            Log.d("zw", "mapNeedChange: starPos?????????????????????");
                            BackToMainListener.changeMap(starPos);
//                    Log.d("zw", "mapNeedChange: ??????????????????????????????" + selfPos.toString());
                        }
                    }
                });
                starFragment.setOnFragmentLongClick(new starFragment.OnFragmentLongClick() {
                    @Override
                    public void mapNeedDeleteMarker(starPos selfPos) {
                        Log.d("zw", "mapNeedDeleteMarker: ??????marker????????????????????????");
                        if (BackToMainListener != null){
                            Log.d("zw", "mapNeedDeleteMarker: ?????????????????????");
                            BackToMainListener.deleteMapMarker(selfPos);
                        }
                    }
                });
                Log.d("zw", "onClick: ?????????????????????");
                replaceFragement(starFragment);
                break;
            }
            case R.id.btn_self:{
                Log.d("zw", "onClick: ?????????????????????");
                selfFragment = new selfFragment();
                selfFragment.setOnFragmentClick(new selfFragment.OnFragmentClick() {
                    @Override
                    public void mapNeedChange(starPos selfPos) {
                        Log.d("zw", "mapNeedChange: ?????????????????????");
                        if(BackToMainListener != null){
                            Log.d("zw", "mapNeedChange: ?????????????????????");
                            BackToMainListener.changeMap(selfPos);
//                    Log.d("zw", "mapNeedChange: ??????????????????????????????" + selfPos.toString());
                        }
                    }
                });
                selfFragment.setOnFragmentLongClick(new selfFragment.OnFragmentLongClick() {
                    @Override
                    public void mapNeedDeleteMarker(starPos selfPos) {
                        Log.d("zw", "mapNeedDeleteMarker: ??????marker????????????????????????");
                        if (BackToMainListener != null){
                            Log.d("zw", "mapNeedDeleteMarker: ?????????????????????");
                            BackToMainListener.deleteMapMarker(selfPos);
                        }
                    }
                });
                replaceFragement(selfFragment);
                break;
            }
            case R.id.add_starLoc:{
//                Intent intent = new Intent(getActivity(), otherStarActivity.class);
//                startActivityForResult(intent, 3);
//                if (!s.isEmpty()){
                    //????????????activity
                    handlerForGetOtherLocIni();
                    threadForOtherLoc = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpUtils.getInstance(getActivity().getApplicationContext()).get("http://120.27.249.235:8081/getPositionByStatus/1",
                                    new OkHttpUtils.MyCallback() {
                                @Override
                                public void success(Response response) throws IOException {
                                    stringOtherLoc = response.body().string();
                                    Log.d("zw", "success: ?????????????????????????????????????????????????????????" + stringOtherLoc);
//?????????????????????Json??????
                                    otherStarLocFromLiang = JSONUtils.reciveOtherStarLocFromLiang(stringOtherLoc);
                                    List<com.beidouapp.model.messages.otherStarLocFromLiang.Data> data = otherStarLocFromLiang.getData();
                                    int num = data.size();
                                    for (int i=0;i<num;i++){
                                        //?????????????????????????????????????????????????????????,?????????????????????????????????????????????
                                        com.beidouapp.model.messages.otherStarLocFromLiang.Data data1 = data.get(i);
//                                        Log.d("zw", "success:??????????????????????????? " + data1.getCoordLat());
//                                        Log.d("zw", "success:??????????????????????????? " + data1.getCoordLng());
                                        List<starLocFormOtherDB> starLocFormOtherDBS = LitePal.where("latitude=? and lontitude=?",
                                                data1.getCoordLat(), data1.getCoordLng())
                                                .find(starLocFormOtherDB.class);
                                        try {
                                        Log.d("zw", "success: ??????????????????" + starLocFormOtherDBS );
                                            if (starLocFormOtherDBS.isEmpty()){
                                                starLocFormOtherDB = new starLocFormOtherDB();
                                                starLocFormOtherDB.setLatitude(data1.getCoordLat());
                                                Log.d("zw", "success:??????????????????????????? " + data1.getCoordLat());
                                                starLocFormOtherDB.setLontitude(data1.getCoordLng());
                                                Log.d("zw", "success:??????????????????????????? " + data1.getCoordLng());
                                                starLocFormOtherDB.setLocInfo(data1.getCoordDesc());
                                                Log.d("zw", "success:??????????????????????????? " + data1.getCoordDesc());
                                                starLocFormOtherDB.setStatus("1");
                                                starLocFormOtherDB.setUid(data1.getReleaseUserID());
                                                Log.d("zw", "success:??????????????????????????? " + data1.getReleaseUserID());
                                                starLocFormOtherDB.setLegend(data1.getCoordLegend());
                                                Log.d("zw", "success:??????????????????????????? " + data1.getCoordLegend());
//                                            starLocFormOtherDB.setTag();//????????????????????????
                                                starLocFormOtherDB.setText(data1.getCoordName());
                                                Log.d("zw", "success:??????????????????????????? " + data1.getCoordName());
                                                starLocFormOtherDB.setTag(data1.getCoordTag());
                                                starLocFormOtherDB.save();
                                            }else{
                                                starLocFormOtherDB = starLocFormOtherDBS.get(0);
                                                starLocFormOtherDB.setLocInfo(data1.getCoordDesc());
                                                starLocFormOtherDB.setStatus("1");
                                                starLocFormOtherDB.setUid(data1.getReleaseUserID());
                                                starLocFormOtherDB.setLegend(data1.getCoordLegend());
//                                            starLocFormOtherDB.setTag();//????????????????????????
                                                starLocFormOtherDB.setText(data1.getCoordName());
                                                starLocFormOtherDB.save();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            Log.d("zw", "success: ??????????????????????????????????????????????????????");}
                                    }
                                    Message messageForOtherLoc = new Message();
                                    messageForOtherLoc.what = 1;
                                    handlerForGetOtherLoc.sendMessage(messageForOtherLoc);
                                }

                                @Override
                                public void failed(IOException e) {
                                    Log.d(TAG, "failed: ?????????????????????????????????");
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
     * ??????????????????????????????handler
     */
    private void handlerForGetOtherLocIni() {
        handlerForGetOtherLoc = new Handler(){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:{
                        //??????activity
                        String s = searchLoc.getText().toString();
                        Intent intent = new Intent(getActivity(), otherStarActivity.class);
                        intent.putExtra("searchKey", s);
                        startActivityForResult(intent, 3);
                        break;
                    } default:break;
                }
            }
        };
    }

    /**
     * ??????Fragment???????????????Fragment???????????????????????????????????????????????????????????????
     * @param fragment ?????????Fragment
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
            Log.d("zw", "replaceFragement: ??????Fragment??????");
        }
    }


    /**
     *
     * @param requestCode ???????????????3????????????otherStarActivity
     * @param resultCode  ???????????????0????????????????????????????????????1??????2??????????????????
     * @param data ???other_loc??????data??????????????????????????????
     *             ???otherStarActivity??????data????????????
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3){
            Log.d("zw", "onActivityResult: ????????????otherStarActivity????????????" + resultCode);
            if(resultCode == 1){
                starFragment = new starFragment();
                starFragment.setOnFragmentClick(new starFragment.OnFragmentClick() {
                    @Override
                    public void mapNeedChange(starPos starPos) {
                        Log.d("zw", "mapNeedChange: starPos?????????????????????");
                        if(BackToMainListener != null){
                            Log.d("zw", "mapNeedChange: starPos?????????????????????");
                            BackToMainListener.changeMap(starPos);
//                    Log.d("zw", "mapNeedChange: ??????????????????????????????" + selfPos.toString());
                        }
                    }
                });
                starFragment.setOnFragmentLongClick(new starFragment.OnFragmentLongClick() {
                    @Override
                    public void mapNeedDeleteMarker(starPos selfPos) {
                        Log.d("zw", "mapNeedDeleteMarker:star ??????marker????????????????????????");
                        if (BackToMainListener != null){
                            Log.d("zw", "mapNeedDeleteMarker:star ?????????????????????");
                            BackToMainListener.deleteMapMarker(selfPos);
                        }
                    }
                });
                Log.d("zw", "onActivityResult: ??????????????????starFragment????????????????????????????????????????????????");
                replaceFragement(starFragment);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    //????????????????????????MainActivity
    public interface BackToMainListener{
        void changeMap(starPos selfPos);
        void deleteMapMarker(starPos selfPos);
    }


    public void setBackToMainListener(BackToMainListener backToMainListener){
        this.BackToMainListener = backToMainListener;
    }
}