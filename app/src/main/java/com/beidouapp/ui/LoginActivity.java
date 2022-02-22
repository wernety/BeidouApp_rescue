package com.beidouapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.model.DataBase.orgAndUidAndKey;
import com.beidouapp.model.User;
import com.beidouapp.model.User4Login;
import com.beidouapp.model.messages.Message4Receive;
import com.beidouapp.model.utils.JSONUtils;
import com.beidouapp.model.utils.OkHttpUtils;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * 登录活动
 * 完成注册及登录
 * 使用http：post
 */

public class LoginActivity extends AppCompatActivity {

    private EditText userNameEditText, userPasswordEditText;
    private Button loginButton;
    private String token;
    private String username;
    private String password;
    private DemoApplication application;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        application = (DemoApplication) this.getApplicationContext();
        checkPermissions();
        initUI();                                                                                   //初始化UI
        initListener();                                                                             //初始化监听器
    }

    private void initUI() {
        userNameEditText = (EditText) findViewById(R.id.edt_username);
        userPasswordEditText = (EditText) findViewById(R.id.edt_password);
        loginButton = (Button) findViewById(R.id.btn_login);
    }

    private void initListener(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = userNameEditText.getText().toString();
                password = userPasswordEditText.getText().toString();
                if (TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "进入开发者状态", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    loginByPost(username, password);
                }
            }
        });
    }

    private void checkPermissions(){
        List<String> permissionList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            //启动程序后询问读写权限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            //启动程序后询问读写权限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //启动程序后询问读写权限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(LoginActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 200);
            } else {
                // 如果有权限做些什么
            }

        }

    }


    /**
     * Post方式登录
     * @param username
     * @param password
     */
    public void loginByPost(String username, String password){
        try {
            Handler handler = new Handler() {
                //           @Override
                @SuppressLint("HandlerLeak")
                public void handleMessage(Message message) {
                    if (message.what == 1) {
                        try {
                            OkHttpUtils.getInstance(LoginActivity.this).get("http://139.196.122.222:8080/getInfo",
                                    token, new OkHttpUtils.MyCallback() {
                                        @Override
                                        public void success(Response response) throws IOException {
                                            JSONObject object = JSON.parseObject(response.body().string());
                                            int code = object.getInteger("code");
                                            if (code == 200) {
                                                User user = JSON.parseObject(object.getString("user"), User.class);
                                                application.setIndexID(user.getUserId());
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.putExtra("uid", username);
                                                intent.putExtra("upw", password);
                                                intent.putExtra("token", token);
                                                startActivity(intent);
                                                finish();
                                            } else if (code == 500) {
                                            } else
                                                Toast.makeText(LoginActivity.this, "请求错误", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void failed(IOException e) {

                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            User4Login user4Login = new User4Login(username, password);
            String json = JSONUtils.sendJSON(user4Login);
            Log.d("json", json);
            OkHttpUtils.getInstance(LoginActivity.this).post("http://139.196.122.222:8080/login", json,
                    new OkHttpUtils.MyCallback() {
                        @Override
                        public void success(Response response) throws IOException {
                            JSONObject object = JSON.parseObject(response.body().string());
                            int code = object.getInteger("code");
                            if (code == 200) {
                                token = object.getString("token");
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } else if (code == 500) {
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "用户名或者密码错误", Toast.LENGTH_LONG).show();
                                        userNameEditText.setText("");
                                        userPasswordEditText.setText("");
                                    }
                                });
                            } else
                                Toast.makeText(LoginActivity.this, "请求错误", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void failed(IOException e) {
                            Log.d("zw", "failed: 没有网络状态，此时使用本地数据库加载数据");
                            List<orgAndUidAndKey> records = LitePal.where("uid = ?", username).find(orgAndUidAndKey.class);
                            //查询数据库，如果有此人，则跳转页面
                            if(records.isEmpty()){

                            }else{
                                Log.d("zw", "failed: 开始检测");
                                orgAndUidAndKey record = records.get(0);
                                Log.d("zw", "failed: 数据库中的密码是" + record.getPass());
                                Log.d("zw", "failed: 检测结果应该是" + password);
                                if(record.getPass().equals(password)){
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("uid", username);
                                    intent.putExtra("upw", password);

                                    startActivity(intent);
                                    finish();
                                }
                            }
                            Log.d("login", e.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.d("zw", "loginByPost: 遇到问题直接退出了");
            e.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {                         //若无读写权限，结束程序
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"谢谢！",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "未授权！", Toast.LENGTH_SHORT).show();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
                break;
            default:
        }
    }



}