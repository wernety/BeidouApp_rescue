package com.beidouapp.ui.fragment;

import android.os.Bundle;
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

public class starFragment extends Fragment {

    private RecyclerView starRv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starloc, container, false);
        ini(view);
//        接下来是添加Adapter，在Adapter里添加我要显示的数据，收藏坐标是从服务器获取的
        return view;
    }

    private void ini(View view) {
        starRv = view.findViewById(R.id.rv_starloc);
        starRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),
                RecyclerView.VERTICAL, false));
        starRv.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL));
    }
}
