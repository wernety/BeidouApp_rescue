package com.beidouapp.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beidouapp.R;
import com.beidouapp.model.DataBase.Pos;
import com.beidouapp.model.DataBase.starposDB;
import com.beidouapp.model.adapters.starPosAdapter;
import com.beidouapp.ui.DemoApplication;

import org.litepal.LitePal;

import java.util.List;

/**
 * 收藏点的fragment
 */
public class starFragment extends Fragment {

    private RecyclerView starRv;
    private DemoApplication application;
    private List<starposDB> starPosDBS;
    private starPosAdapter starPosAdapter;
    private OnFragmentClick onFragmentClick;
    private starposDB starPosdb;
    private starPos starPos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starloc, container, false);
        ini(view);
        iniItemListener();
//        接下来是添加Adapter，在Adapter里添加我要显示的数据，收藏坐标是从服务器获取的
        return view;
    }

    private void ini(View view) {
        application = (DemoApplication) getActivity().getApplicationContext();
        starRv = view.findViewById(R.id.rv_starloc);
        starRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                RecyclerView.VERTICAL, false));
        starRv.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        //初始化数据，就是查库
        starPosDBS = LitePal.where("selfID=?", application.getUserID()).find(starposDB.class);
        starPosAdapter = new starPosAdapter(starPosDBS);
        starRv.setAdapter(starPosAdapter);
    }

    //启用回调函数
    private void iniItemListener() {
        starPosAdapter.setOnItemClickListener(new starPosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                starPosdb = starPosDBS.get(pos);
                starPos = new starPos(starPosdb.getText(),starPosdb.getUid(), starPosdb.getStatus(), starPosdb.getTag(), starPosdb.getLatitude(),
                        starPosdb.getLontitude(), starPosdb.getLocInfo(), starPosdb.getLegend());
                Log.d("zw", "onItemClick: 此时第一次回调成功，得到的结果是" + starPos.toString());
                if (onFragmentClick != null){
                    Log.d("zw", "onItemClick: 此时starFragment的第二次回调开始");
                    onFragmentClick.mapNeedChange(starPos);
                }
            }

            @Override
            public void onItemLongClick(View v, int pos) {
                PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_starfragment,popupMenu.getMenu());
                starPosdb = starPosDBS.get(pos);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.deleteStarPos:{
                                Log.d("zw", "onMenuItemClick: 开始删除收藏点，需要删除数据库");
                                deleteDB(starPosdb);
                                starPosAdapter.deleteStarLocData(pos);
                            } default:break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    /**
     * @param
     * @return null
     * @Title
     * @parameter
     * @Description 删除starPosDB数据库
     * @author chx
     * @data 2022/2/27/027  17:42
     */
    private void deleteDB(starposDB starPosdb) {
        Log.d("zw", "deleteDB: 开始删除starPosDB数据库，用户是:" + application.getUserID());
        LitePal.deleteAll(starposDB.class, "latitude = ? and lontitude=? and selfID=?",
                starPosdb.getLatitude(), starPosdb.getLontitude(), application.getUserID());
    }


    /**
     *回调，回调给PosManagerFragment
     * 此时是starFragment里面的recycleView 的item点击后，首先回调给starFragment
     * 接下来将由starFragment回调给PosManagerFragment
     * @param onFragmentClick
     */
    public void setOnFragmentClick(OnFragmentClick onFragmentClick){
        this.onFragmentClick = onFragmentClick;
    }

    public interface OnFragmentClick{
        void mapNeedChange(starPos selfPos);
    }
}
