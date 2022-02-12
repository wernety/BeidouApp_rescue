package com.beidouapp.ui.fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.beidouapp.R;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pos_manage, container, false);
       ini(view);
       curFragment = selfFragment;
       replaceFragement(curFragment);
       return view;
    }

    private void ini(View view) {
        btnStarLoc = view.findViewById(R.id.btn_star);
        btnSelfLoc = view.findViewById(R.id.btn_self);
        btnStarLoc.setOnClickListener(this);
        btnSelfLoc.setOnClickListener(this);
        starFragment = new starFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_star:{
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
            } default:break;
        }
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

    public interface BackToMainListener{
        void changeMap(starPos selfPos);
    }

    public void setBackToMainListener(BackToMainListener backToMainListener){
        this.BackToMainListener = backToMainListener;
    }
}