package com.beidouapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.beidouapp.R;
import com.beidouapp.ui.DemoApplication;
import com.beidouapp.ui.LoginActivity;
import com.beidouapp.ui.Setting.ActivityBluetooth;
import com.beidouapp.ui.Setting.ActivityMap;
import com.beidouapp.ui.Setting.ActivityMemoryclear;
import com.beidouapp.ui.Setting.ActivitySafe;
import com.beidouapp.ui.Setting.ActivityShortmessage;
import com.beidouapp.ui.Setting.ActivityUser;
import com.beidouapp.ui.Setting.ActivityPermission;


public class SettingsFragment extends Fragment {

    private View root;
    private DemoApplication application;

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
        application = (DemoApplication) getActivity().getApplicationContext();
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
                application.setFlag(false);
                Intent i = new Intent(getActivity(), LoginActivity.class);
                //启动
                startActivity(i);
                getActivity().finish();
            }
        });
        return root;
    }

}