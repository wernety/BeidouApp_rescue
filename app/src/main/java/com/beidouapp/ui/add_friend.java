package com.beidouapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.model.utils.OkHttpUtils;

import java.io.IOException;

import okhttp3.Response;

public class add_friend extends AppCompatActivity {
    private EditText input_id;
    private EditText input_name;
    private ImageView back;
    private Button confirm;
    private String loginId;
    private String friendId;
    private String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_friend);
        Intent intent = getIntent();
        loginId = intent.getStringExtra("id");

        initUI();
        initListener();
    }

    private void initUI() {
        back = (ImageView) findViewById(R.id.iv_back_add_friend);
        confirm = (Button) findViewById(R.id.btn_add_friend);
        input_id = (EditText) findViewById(R.id.edt_add_friend);
        input_name = (EditText) findViewById(R.id.edt_add_friend_name);
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendId = input_id.getText().toString();
                friendName = input_name.getText().toString();
                JSONObject object = new JSONObject();
                object.put("userId", loginId);
                object.put("friendId", friendId);
                object.put("friendName", friendName);
                Log.d("zz",object.toJSONString());
                try {
                    OkHttpUtils.getInstance(add_friend.this).post("http://120.27.242.92:8080/friends",
                            object.toJSONString(), new OkHttpUtils.MyCallback() {
                                @Override
                                public void success(Response response) throws IOException {
                                    Log.d("zz","添加好友中");
                                    String body = response.body().string();
                                    JSONObject jsonObject = JSON.parseObject(body);
                                    int code = jsonObject.getInteger("code");
                                    if (code == 200) {
                                        finish();
                                    }
                                }

                                @Override
                                public void failed(IOException e) {
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("zz","添加好友失败");
                }

            }
        });
    }
}