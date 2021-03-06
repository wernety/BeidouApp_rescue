package com.beidouapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.model.utils.OkHttpUtils;
import com.beidouapp.model.utils.id2name;

import java.io.IOException;

import okhttp3.Response;

public class  friend_info extends AppCompatActivity {
    private TextView nickname;
    private TextView uid;
    private Button toChat;
    private Button Append_or_Delete;
    private ImageView back;
    private String ID;
    private String Nickname;
    private String Type;
    private String loginId;
    private DemoApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_info);
        application = (DemoApplication) friend_info.this.getApplicationContext();


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
        loginId = intent.getStringExtra("loginId");
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
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Append_or_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase writableDatabase = application.dbHelper.getWritableDatabase();
                try {
                    OkHttpUtils.getInstance(friend_info.this).delete("http://139.196.122.222:8080/beisan/relation/"
                            + application.getIndexID() + "/" + id2name.findIndexId(writableDatabase,loginId,ID),
                            application.getToken(), new OkHttpUtils.MyCallback() {
                                @Override
                                public void success(Response response) throws IOException {
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
                }
            }
        });
    }
}