package com.beidouapp.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beidouapp.R;
import com.beidouapp.model.DataBase.Pos;
import com.beidouapp.model.adapters.selfPosAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class selfFragment extends Fragment {

    private RecyclerView selfRv;
    private List<Pos> posRecords;
    private Pos posRecord;
    private List<starPos> list = new ArrayList<starPos>();
    private selfPosAdapter selfPosAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selfloc, container, false);
        ini(view);
        iniData();
        return view;
    }

    private void ini(View view) {
        selfRv = view.findViewById(R.id.rv_selfloc);
        selfRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext().getApplicationContext(),
                RecyclerView.VERTICAL, false));
        selfRv.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void iniData() {
        posRecords = LitePal.findAll(Pos.class);
        int num = posRecords.size();
        for(int i=0;i<num;i++){
            posRecord = posRecords.get(i);
            starPos starPos = new starPos(posRecord.getText(),
                    posRecord.getUid(),posRecord.getStatus(),posRecord.getTag(),posRecord.getLatitude(),posRecord.getLontitude());
            list.add(starPos);
        }
        Log.d("zw", "iniData: 在初始化selfFragment的时候的list是：" + list.toString());
        selfPosAdapter = new selfPosAdapter(list);
        selfRv.setAdapter(selfPosAdapter);
    }
}
