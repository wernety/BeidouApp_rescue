package com.beidouapp.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.map.InfoWindow;
import com.beidouapp.R;
import com.beidouapp.model.DataBase.Pos;
import com.beidouapp.model.adapters.locOthers;
import com.beidouapp.model.adapters.selfPosAdapter;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.utils.selfPosJson;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class selfFragment extends Fragment {

    private RecyclerView selfRv;
    private List<Pos> posRecords;
    private Pos posRecord;
    private List<starPos> list = new ArrayList<starPos>();
    private selfPosAdapter selfPosAdapter;
    private starPos selfPos;
    private OnFragmentClick onFragmentClick;
    private List<Pos> selfPosRecords;
    private Pos selfPosRecord;
    private Context context;


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
        selfRv = view.findViewById(R.id.rv_selfloc);
        selfRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext().getApplicationContext(),
                RecyclerView.VERTICAL, false));
        selfRv.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
    }

    /**
     * 数据初始化
     */
    private void iniData() {
        posRecords = LitePal.findAll(Pos.class);
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
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.uploadSelfPos:{
                                uploadSelfPos(selfPos);
                                break;
                            }
                            case R.id.deleteSelfPos:{
                                deleteDbRecord(selfPos);
//                                selfPosAdapter.notifyItemRemoved(pos);
//                                selfPosAdapter.notifyItemRangeChanged(pos, selfPosAdapter.getItemCount());
                                Log.d("zw", "onMenuItemClick: 此时删除的位置应该是:" + pos);
//                                selfPosAdapter.notifyDataSetChanged();
                                selfPosAdapter.deleteData(pos);
                                break;
                            }
                            case R.id.watchLocInfo:{
                                showInfo(selfPos);
                                break;
                            }
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
     * 上传自建点坐标
     */
    private void uploadSelfPos(starPos selfPos) {
//先查到这个数据，然后上传这个数据的所有，如果上传成功，我们就改变状态（tag标签），并且写库,上传失败则弹出对话框
        selfPosRecords = LitePal.where("latitude=? or lontitude=?",
                selfPos.getLatitude(), selfPos.getLontitude()).find(Pos.class);
        if (!selfPosRecords.isEmpty()){
            selfPosRecord = selfPosRecords.get(0);
                //发送到亮哥那边去，然后记得在显示的时候要根据设计的图例来显示自建点，先留白
            try {
                selfPosJson selfPosJson = new selfPosJson(selfPosRecord.getUid(), selfPosRecord.getLontitude(),
                        selfPosRecord.getLatitude(), (int) selfPosRecord.getLegend(), selfPosRecord.getText(), selfPosRecord.getLocInfo());
                String json = JSONUtils.sendJson(selfPosJson);
                OkHttpUtils.getInstance(getActivity().getApplicationContext()).post("", json, new OkHttpUtils.MyCallback() {
                    @Override
                    public void success(Response response) throws IOException {
                        //将数据库发送状态修改成已发送
                        selfPosRecord.setStatus("1");
                        selfPosRecord.save();
                    }

                    @Override
                    public void failed(IOException e) {

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
    private void deleteDbRecord(starPos selfPos) {
        Log.d("zw", "deleteDbRecord: 开始删库");
        LitePal.deleteAll(Pos.class, "latitude = ? and lontitude=?", selfPos.getLatitude(), selfPos.getLontitude());
        //设置回调，自动创建新的。。。但是recycleview里面写了刷新了的，并没有真正的执行
    }

    /**
     * 显示位置点详细信息
     */
    private void showInfo(starPos selfPos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.locinfo, null);
        EditText content = v.findViewById(R.id.et_locInfo2);
        Button update = v.findViewById(R.id.btn_update);
        Button cancel = v.findViewById(R.id.btn_cancel_locInfo);
        selfPosRecords = LitePal.where("latitude=? or lontitude=?",
                selfPos.getLatitude(), selfPos.getLontitude()).find(Pos.class);
        if (!selfPosRecords.isEmpty()){
            selfPosRecord = selfPosRecords.get(0);
            content.setText(selfPos.getLocInfo());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getWindow().setContentView(v);
            alertDialog.getWindow().setGravity(Gravity.CENTER);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selfPosRecord.setLocInfo(content.getText().toString());
                    selfPosRecord.save();
                    alertDialog.dismiss();
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
}
