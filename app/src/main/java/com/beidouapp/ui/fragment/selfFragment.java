package com.beidouapp.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.model.LatLng;
import com.baidu.platform.comapi.basestruct.Point;
import com.beidouapp.R;
import com.beidouapp.model.DataBase.Pos;
import com.beidouapp.model.DataBase.starposDB;
import com.beidouapp.model.adapters.locOthers;
import com.beidouapp.model.adapters.selfPosAdapter;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.utils.selfPosJson;
import com.beidouapp.ui.DemoApplication;
import com.beidouapp.ui.MainActivity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class selfFragment extends Fragment {

    private RecyclerView selfRv;
    private List<Pos> posRecords;
    private List<starposDB> starPosRecords;
    private Pos posRecord;
    private List<starPos> list = new ArrayList<starPos>();
    private selfPosAdapter selfPosAdapter;
    private starPos selfPos;
    private OnFragmentClick onFragmentClick;
    private List<Pos> selfPosRecords;
    private Pos selfPosRecord;
    private Context context;
    private DemoApplication application;
    private starposDB starposDB;
    private OnFragmentLongClick onFragmentLongClick;
    private RefreshLayout rlForSlefLoc;
    private Handler handlerUpload;
    private Handler handlerCancelUpLoad;
    private Handler handlerDelete;
    private Handler handlerWatchLocInfo;
    private AlertDialog alertDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selfloc, container, false);
        ini(view);
        iniData();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    /**
     *
     * @param view
     */
    private void ini(View view) {
        application = (DemoApplication) getActivity().getApplicationContext();
        rlForSlefLoc = view.findViewById(R.id.refreshLayoutForSelfLoc);
        selfRv = view.findViewById(R.id.rv_selfloc);
        selfRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext().getApplicationContext(),
                RecyclerView.VERTICAL, false));
        selfRv.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        rlForSlefLoc.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                refreshLayout.finishRefresh(5);
                refreshLayout.closeHeaderOrFooter();
            }
        });
        handlerUpload = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case  1:{
                        Log.d("zw", "handleMessage: 此时msg自带的数据是" + msg.arg1);
                        selfPosAdapter.uploadSuccess(msg.arg1);
                        break;
                    } default:break;
                }
                return false;
            }
        });
        handlerCancelUpLoad = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1:{
                        Log.d("zw", "handleMessage: 此时的msg参数1是" + msg.arg1);
                        selfPosAdapter.cancelUpload(msg.arg1);
                        break;
                    } default:break;
                }
                return false;
            }
        });
        handlerDelete = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1:{
                        selfPosAdapter.deleteData(msg.arg1);
                        break;
                    }
                }
                return false;
            }
        });
        handlerWatchLocInfo = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1:{
                        Log.d("zw", "handleMessage: " + msg.getData().getString("Info"));
                        selfPosAdapter.changeInfo( msg.getData().getString("Info"), msg.arg1);
                        alertDialog.dismiss();
                        break;
                    } default:break;
                }
                return false;
            }
        });
    }

    /**
     * 数据初始化
     */
    private void iniData() {
        posRecords = LitePal.where("uid=?",application.getUserID()).find(Pos.class);
        //注意这里可能为空哦
        int num = posRecords.size();
        for(int i=0;i<num;i++){
            posRecord = posRecords.get(i);
            starPos starPos = new starPos(posRecord.getText(), posRecord.getUid(),posRecord.getStatus(),
                    posRecord.getTag(),posRecord.getLatitude(),posRecord.getLontitude(), posRecord.getLocInfo(), posRecord.getLegend());
            list.add(starPos);
        }
        Log.d("zw", "iniData: 在初始化selfFragment的时候的list是：" + list.toString());
        selfPosAdapter = new selfPosAdapter(list);
        iniItemListener();
        selfRv.setAdapter(selfPosAdapter);
    }

    /**
     * item的监听事件
     */
    private void iniItemListener() {
        selfPosAdapter.setOnItemClickListener(new selfPosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Log.d("zw", "onItemClick: 第一次回调成功");
                selfPos = list.get(pos);
                if(onFragmentClick != null){
                    Log.d("zw", "onItemClick: 第二次回调");
                    onFragmentClick.mapNeedChange(selfPos);
                }
            }

            @Override
            public void onItemLongClick(View v, int pos) {
//                LayoutInflater layoutInflater = LayoutInflater.from(getActivity().getApplicationContext());
//                View inflate = layoutInflater.inflate(R.layout.selfpos_setting, null);
//                Button btn_upload = inflate.findViewById(R.id.btn_uploadSelfPos); //这三个是使用infowindow做的，想同map那边显示相同
                Log.d("zw", "onItemLongClick: 长按item成功");
                PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.selfposconfig_menu,popupMenu.getMenu());
                selfPos = list.get(pos);
                Log.d("zw", "onItemLongClick: 此时的状态" + selfPos.getStatus());
                if(selfPos.getStatus().equals("0")){
                    popupMenu.getMenu().findItem(R.id.cancleUpLoad).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.uploadSelfPos).setVisible(true);
                }else{
                    popupMenu.getMenu().findItem(R.id.cancleUpLoad).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.uploadSelfPos).setVisible(false);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.uploadSelfPos:{
                                uploadSelfPos(selfPos, pos);
                                break;
                            }
                            case R.id.deleteSelfPos:{
                                //后面的数据记录删除后，前面的数据记录也得删除
                                deleteDbRecord(selfPos, pos);
//                                selfPosAdapter.notifyItemRemoved(pos);
//                                selfPosAdapter.notifyItemRangeChanged(pos, selfPosAdapter.getItemCount());
                                Log.d("zw", "onMenuItemClick: 此时删除的位置应该是:" + pos);
//                                selfPosAdapter.notifyDataSetChanged();
                                deleteMapMarker(selfPos);

                                break;
                            }
                            case R.id.watchLocInfo:{
                                showInfo(selfPos, pos);
                                break;
                            }
                            case R.id.cancleUpLoad:
                                cancleUpload(selfPos, pos);
                            default:break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    /**
     * 发布自建点坐标
     */
    private void uploadSelfPos(starPos selfPos, int pos) {
//先查到这个数据，然后上传这个数据的所有，如果上传成功，我们就改变状态（tag标签），并且写库,此时写两个库，一个是自建点的库，需要将状态改成1；另一个是收藏点的库，将全部的信息写入进去
        selfPosRecords = LitePal.where("latitude=? and lontitude=? and uid=?",
                selfPos.getLatitude(), selfPos.getLontitude(), application.getUserID()).find(Pos.class);
        starPosRecords = LitePal.where("latitude=? and lontitude=? and selfID=?",
                selfPos.getLatitude(), selfPos.getLontitude(), application.getUserID()).find(starposDB.class);
        if (!selfPosRecords.isEmpty()){
            selfPosRecord = selfPosRecords.get(0);
                //发送到亮哥那边去，然后记得在显示的时候要根据设计的图例来显示自建点，先留白
            try {
                selfPosJson selfPosJson = new selfPosJson(selfPosRecord.getUid(), selfPosRecord.getLontitude(),
                        selfPosRecord.getLatitude(), (int) selfPosRecord.getLegend(), selfPosRecord.getText(), selfPosRecord.getLocInfo(),1, selfPosRecord.getTag());
                String json = JSONUtils.sendJson(selfPosJson);
                Log.d("zw", "uploadSelfPos: 准备发给亮哥的格式是：" + json);
                OkHttpUtils.getInstance(getActivity().getApplicationContext()).put("http://120.27.249.235:8081/publishPosition",
                        json,
                        new OkHttpUtils.MyCallback() {
                    @Override
                    public void success(Response response) throws IOException {
                        //将数据库发送状态修改成已发送
                        selfPosRecord.setStatus("1");
                        selfPosRecord.save();
                        if (starPosRecords.isEmpty()){
                            starposDB = new starposDB();
                            starposDB.setUid(application.getUserID());
                            starposDB.setSelfID(application.getUserID());
                            starposDB.setLontitude(selfPosRecord.getLontitude());
                            starposDB.setTag(selfPosRecord.getTag());
                            starposDB.setLegend(selfPosRecord.getLegend());
                            starposDB.setText(selfPosRecord.getText());
                            starposDB.setStatus("1");
                            starposDB.setLatitude(selfPosRecord.getLatitude());
                            starposDB.setLocInfo(selfPosRecord.getLocInfo());
                            starposDB.save();
                        }else{
                            starposDB = starPosRecords.get(0);
                            starposDB.setUid(application.getUserID());
                            starposDB.setSelfID(application.getUserID());
                            starposDB.setLontitude(selfPosRecord.getLontitude());
                            starposDB.setTag(selfPosRecord.getTag());
                            starposDB.setLegend(selfPosRecord.getLegend());
                            starposDB.setText(selfPosRecord.getText());
                            starposDB.setStatus("1");
                            starposDB.setLatitude(selfPosRecord.getLatitude());
                            starposDB.setLocInfo(selfPosRecord.getLocInfo());
                            starposDB.save();
                        }
                        Message message = new Message();
                        message.what = 1;
                        message.arg1 = pos;
                        handlerUpload.sendMessage(message);
                    }

                    @Override
                    public void failed(IOException e) {
                        Toast.makeText(getActivity().getApplicationContext(), "上传失败", Toast.LENGTH_LONG).show();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
                Log.d("zw", "uploadSelfPos: 发送自建位置点失败");
            }
        }
    }

    /**
     * 删除该条记录
     */
    private void deleteDbRecord(starPos selfPos, int pos) {
        Log.d("zw", "deleteDbRecord: 开始删库");
        selfPosRecords = LitePal.where("latitude=? and lontitude=? and uid=?",
                selfPos.getLatitude(), selfPos.getLontitude(), application.getUserID()).find(Pos.class);

        if (!selfPosRecords.isEmpty()){
            selfPosRecord = selfPosRecords.get(0);
            //发送到亮哥那边去，然后记得在显示的时候要根据设计的图例来显示自建点，先留白
            try {
                selfPosJson selfPosJson = new selfPosJson(selfPosRecord.getUid(), selfPosRecord.getLontitude(),
                        selfPosRecord.getLatitude(), (int) selfPosRecord.getLegend(), selfPosRecord.getText(), selfPosRecord.getLocInfo(),1, selfPosRecord.getTag());
                String json = JSONUtils.sendJson(selfPosJson);
                Log.d("zw", "uploadSelfPos: 准备发给亮哥的格式是：" + json);
                OkHttpUtils.getInstance(getActivity().getApplicationContext()).del("http://120.27.249.235:8081/deletePositionBySelfPosJson",
                        json,
                        new OkHttpUtils.MyCallback() {
                            @Override
                            public void success(Response response) throws IOException {
                                //将数据库发送状态修改成已发送
                                Log.d("zw", "success: 网络也删除成功");
                                Log.d("zw", "success: 删除自建点返回的信息是：" + response.body().string());
                                LitePal.deleteAll(Pos.class, "latitude = ? and lontitude=? and uid=?", selfPos.getLatitude(), selfPos.getLontitude(), application.getUserID());
                                Message message3 = new Message();
                                message3.what = 1;
                                message3.arg1 = pos;
                                handlerDelete.sendMessage(message3);
                            }

                            @Override
                            public void failed(IOException e) {
                                Toast.makeText(getActivity().getApplicationContext(), "没有网络，删除失败", Toast.LENGTH_LONG).show();
                            }
                        });
            }catch (Exception e){
                e.printStackTrace();
                Log.d("zw", "uploadSelfPos: 删除自建位置点失败");
            }
        }


        //设置回调，自动创建新的。。。但是recycleview里面写了刷新了的，并没有真正的执行
    }


    /**
     * @param
     * @return null
     * @Title
     * @parameter
     * @Description 取消上传，先查库，查库提交，然后改本地库
     * @author chx
     * @data 2022/3/4/004  18:21
     */
    private void cancleUpload(starPos selfPos, int pos) {
        selfPosRecords = LitePal.where("latitude=? and lontitude=? and uid=?",
                selfPos.getLatitude(), selfPos.getLontitude(), application.getUserID()).find(Pos.class);
        starPosRecords = LitePal.where("latitude=? and lontitude=? and selfID=?",
                selfPos.getLatitude(), selfPos.getLontitude(), application.getUserID()).find(starposDB.class);
        if (!selfPosRecords.isEmpty()){
            selfPosRecord = selfPosRecords.get(0);
            selfPosJson selfPosJson = new selfPosJson(selfPosRecord.getUid(), selfPosRecord.getLontitude(),
                    selfPosRecord.getLatitude(), (int) selfPosRecord.getLegend(), selfPosRecord.getText(), selfPosRecord.getLocInfo(),0, selfPosRecord.getTag());
            String json = JSONUtils.sendJson(selfPosJson);
            Log.d("zw", "uploadSelfPos: 准备发给亮哥的格式是：" + json);
            try {
                OkHttpUtils.getInstance(getActivity().getApplicationContext()).put("http://120.27.249.235:8081/reclaimPosition", json, new OkHttpUtils.MyCallback() {
                    @Override
                    public void success(Response response) throws IOException {
                        selfPosRecord.setStatus("0");
                        selfPosRecord.save();
                        if (starPosRecords.isEmpty()){
                            starposDB = new starposDB();
                            starposDB.setUid(application.getUserID());
                            starposDB.setSelfID(application.getUserID());
                            starposDB.setLontitude(selfPosRecord.getLontitude());
                            starposDB.setTag(selfPosRecord.getTag());
                            starposDB.setLegend(selfPosRecord.getLegend());
                            starposDB.setText(selfPosRecord.getText());
                            starposDB.setStatus("1");
                            starposDB.setLatitude(selfPosRecord.getLatitude());
                            starposDB.setLocInfo(selfPosRecord.getLocInfo());
                            starposDB.save();
                        }else{
                            starposDB = starPosRecords.get(0);
                            starposDB.setUid(application.getUserID());
                            starposDB.setSelfID(application.getUserID());
                            starposDB.setLontitude(selfPosRecord.getLontitude());
                            starposDB.setTag(selfPosRecord.getTag());
                            starposDB.setLegend(selfPosRecord.getLegend());
                            starposDB.setText(selfPosRecord.getText());
                            starposDB.setStatus("1");
                            starposDB.setLatitude(selfPosRecord.getLatitude());
                            starposDB.setLocInfo(selfPosRecord.getLocInfo());
                            starposDB.save();
                        }
                        Message message2 = new Message();
                        message2.what = 1;
                        message2.arg1 = pos;
                        handlerCancelUpLoad.sendMessage(message2);
                    }

                    @Override
                    public void failed(IOException e) {
                        Toast.makeText(getActivity().getApplicationContext(), "修改失败", Toast.LENGTH_LONG).show();
                    }
                });
            }catch (Exception e){
                Log.d("zw", "cancleUpload: 取消发布这个动作失败");
            }

        }

    }



    /**
     * @return null
     * @Title
     * @parameter
     * @Description 删除地图上的marker
     * @author chx
     * @data 2022/2/28/028  10:53
     */
    private void deleteMapMarker(starPos selfPos) {
        if (onFragmentLongClick != null){
            onFragmentLongClick.mapNeedDeleteMarker(selfPos);
        }
    }

    /**
     * 显示位置点详细信息
     */
    private void showInfo(starPos selfPos, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.locinfo, null);
        EditText content = v.findViewById(R.id.et_locInfo2);
        Button update = v.findViewById(R.id.btn_update);
        Button cancel = v.findViewById(R.id.btn_cancel_locInfo);
        selfPosRecords = LitePal.where("latitude=? and lontitude=? and uid=?",
                selfPos.getLatitude(), selfPos.getLontitude(), application.getUserID()).find(Pos.class);
        if (!selfPosRecords.isEmpty()){
            selfPosRecord = selfPosRecords.get(0);
            content.setText(selfPos.getLocInfo());
            alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getWindow().setContentView(v);
            alertDialog.getWindow().setGravity(Gravity.CENTER);
            content.setFocusable(true);
            content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        //dialog弹出软键盘
                        alertDialog.getWindow()
                                .clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    }
                }
            });
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selfPosJson selfPosJson = new selfPosJson(selfPosRecord.getUid(), selfPosRecord.getLontitude(),
                            selfPosRecord.getLatitude(), (int) selfPosRecord.getLegend(), content.getText().toString(), selfPosRecord.getLocInfo(),0, selfPosRecord.getTag());
                    Log.d("zw", "onClick: 此时要改变的详细信息是" + content.getText().toString());
                    String json = JSONUtils.sendJson(selfPosJson);
                    OkHttpUtils.getInstance(getActivity().getApplicationContext()).put("http://120.27.249.235:8081/updatePosition", json, new OkHttpUtils.MyCallback() {
                        @Override
                        public void success(Response response) throws IOException {
                            Log.d("zw", "success: 修改成功");
                            Message message4 = new Message();
                            Bundle bundle4 = new Bundle();
                            bundle4.putString("Info", content.getText().toString());
                            message4.what = 1;
                            message4.arg1 = pos;
                            message4.setData(bundle4);
                            handlerWatchLocInfo.sendMessage(message4);
                            selfPosRecord.setLocInfo(content.getText().toString());
                            selfPosRecord.save();
                        }

                        @Override
                        public void failed(IOException e) {

                        }
                    });
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }
        selfPosAdapter.notifyDataSetChanged();
    }

    /**
     *回调，回调给PosManagerFragment
     * 此时是selfFragment里面的recycleView 的item点击后，首先回调给selfFragment
     * 接下来将由selfFragment回调给PosManagerFragment
     * @param onFragmentClick
     */
    public void setOnFragmentClick(OnFragmentClick onFragmentClick){
        this.onFragmentClick = onFragmentClick;
    }

    public interface OnFragmentClick{
        void mapNeedChange(starPos selfPos);
    }


    /**
     * @param
     * @return null
     * @Title
     * @Description 回调接口，用于在地图上删除marker
     * @author chx
     * @data 2022/2/28/028  10:51
     */
    public void setOnFragmentLongClick(OnFragmentLongClick onFragmentLongClick){
        this.onFragmentLongClick = onFragmentLongClick;
    }

    public interface OnFragmentLongClick{
        void mapNeedDeleteMarker(starPos selfPos);
    }



}
