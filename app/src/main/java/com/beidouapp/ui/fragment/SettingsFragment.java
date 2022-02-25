package com.beidouapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beidouapp.R;
import com.beidouapp.model.User;
import com.beidouapp.model.utils.SharePerferenceUtils;
import com.beidouapp.ui.LoginActivity;
import com.beidouapp.ui.Setting.ActivityBluetooth;
import com.beidouapp.ui.Setting.ActivityMap;
import com.beidouapp.ui.Setting.ActivityMemoryclear;
import com.beidouapp.ui.Setting.ActivitySafe;
import com.beidouapp.ui.Setting.ActivityShortmessage;
import com.beidouapp.ui.Setting.ActivityUser;
import com.beidouapp.ui.Setting.ActivityPermission;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsFragment extends Fragment {

    private View root;
    CircleImageView mImgHead;
    TextView mTvName;
    private String userId;
    private User user;
    private Gson gson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(root==null) {
            root = inflater.inflate(R.layout.fragment_settings, container, false);
        }
        mImgHead = root.findViewById(R.id.mImgHead);
        mTvName = root.findViewById(R.id.mTvName);
        Button user = root.findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ActivityUser.class);
                //启动
                startActivity(i);
            }
        });

        Button safe = root.findViewById(R.id.safe);
        safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ActivitySafe.class);
                //启动
                startActivity(i);
            }
        });
        Button map = root.findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ActivityMap.class);
                //启动
                startActivity(i);
            }
        });

        /*Button permission = root.findViewById(R.id.permission);
        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Activitypermission.class);
                //启动
                startActivity(i);
            }
        });

        Button memory = root.findViewById(R.id.memory);
        memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ActivityMemoryclear.class);
                //启动
                startActivity(i);
            }
        });

        Button bluetooth = root.findViewById(R.id.bluetooth);
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ActivityBluetooth.class);
                //启动
                startActivity(i);
            }
        });
        Button message = root.findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ActivityShortmessage.class);
                //启动
                startActivity(i);
            }
        });*/
        Button exit = root.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                //启动
                startActivity(i);
            }
        });
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        userId = SharePerferenceUtils.getString(getActivity(), "userId","");
        String temp = SharePerferenceUtils.getString(getActivity(), userId+"_info","");
        if(!TextUtils.isEmpty(temp)){
            user = gson.fromJson(temp, User.class);
            if(!TextUtils.isEmpty(user.getUserName())){
                mTvName.setText(user.getUserName());
            }
            if(!TextUtils.isEmpty(user.getHead())){
                Glide.with(this).load(user.getHead()).into(mImgHead);
            }
        }
    }
}