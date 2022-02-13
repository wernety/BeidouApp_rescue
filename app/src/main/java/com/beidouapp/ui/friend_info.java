package com.beidouapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.model.messages.Group;

import java.util.List;

public class  friend_info extends AppCompatActivity {
    private TextView nickname;
    private TextView uid;
    private Button toChat;
    private Button Append_or_Delete;
    private ImageView back;
    private String ID;
    private String Nickname;
    private String Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_info);


        initUI();
        getInfo();
        initListener();
    }




    /**
     * 初始化UI控件
     */
    private void initUI() {
        nickname = (TextView) findViewById(R.id.tv_friend_nick);
        uid = (TextView) findViewById(R.id.tv_friend_id);
        toChat = (Button) findViewById(R.id.btn_message);
        Append_or_Delete = (Button) findViewById(R.id.btn_delete);
        back = (ImageView) findViewById(R.id.iv_back_friend);
    }

    /**
     * 获取上下文信息
     */
    private void getInfo() {
        Intent intent = getIntent();
        ID = intent.getStringExtra("uid");
        Nickname = intent.getStringExtra("nickname");
        String Type = intent.getStringExtra("type");

        nickname.setText(Nickname);
        uid.setText(ID);
        Append_or_Delete.setText("删除好友");

    }


    /**
     * 初始化监听器
     */
    private void initListener() {
        toChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(friend_info.this, ChatActivity.class);
                intent.putExtra("uid", ID);
                intent.putExtra("nickname", Nickname);
                intent.putExtra("type", "single");
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}