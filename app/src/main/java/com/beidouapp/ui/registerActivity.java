package com.beidouapp.ui;

import static com.beidouapp.ui.LoginActivity.isMobileNO;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.model.DataBase.orgAndUidAndKey;
import com.beidouapp.model.User;
import com.beidouapp.model.utils.OkHttpUtils;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public class registerActivity extends AppCompatActivity {
    private EditText userNameEditText, userPasswordEditText, smsCodeEditText;
    private Button passwordHideButton, sendSMSButton, registerButton, toLoginButton;
    private boolean ifHide = true;
    Handler handler = new Handler(); //验证码发送等待倒计时
    int wait = 60; //等待60秒
    private String token;
    private String username;
    private String password;
    private String smsCode; //验证码
    private String uuid; //验证码登录的唯一标识

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        passwordHideButton = findViewById(R.id.btn_switch_psw);
        sendSMSButton = findViewById(R.id.btn_send_code);
        registerButton = findViewById(R.id.btn_register);
        toLoginButton = findViewById(R.id.btn_to_login);

        userNameEditText = findViewById(R.id.edt_username);
        smsCodeEditText = findViewById(R.id.edt_smsCode);
        userPasswordEditText = findViewById(R.id.edt_password);

        passwordHideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_switch_psw) {
                    ifHide = !ifHide; //从隐藏变显示
                    if (ifHide) {
                        userPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        //为了点击之后输入框光标不变
                        userPasswordEditText.setSelection(userPasswordEditText.getText().length());
                        passwordHideButton.setBackgroundResource(R.drawable.no_eye);
                    } else {
                        userPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        //为了点击之后输入框光标不变
                        userPasswordEditText.setSelection(userPasswordEditText.getText().length());
                        passwordHideButton.setBackgroundResource(R.drawable.eye);
                    }
                }
            }
        });

        sendSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() ==R.id.btn_send_code){
                    username = userNameEditText.getText().toString();
                    boolean isPhoneNum = isMobileNO(userNameEditText.getText().toString());
                    if (TextUtils.isEmpty(username)){
                        Toast.makeText(registerActivity.this, "手机号不能为空", Toast.LENGTH_LONG).show();
                    }else if (!isPhoneNum){
                        userNameEditText.setText("");
                        Toast.makeText(registerActivity.this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
                    }
                    else {
                        //如果手机号输入正确
                        //先设置按钮不可用，然后执行倒计时，给后端提示发送验证码
                        v.setEnabled(false);
                        handler.postDelayed(myRunable,1000);
                        JSONObject needSMSCode = new JSONObject();
                        Toast.makeText(registerActivity.this, "成功发送至"+username, Toast.LENGTH_LONG).show();
                        needSMSCode.put("mobile",username);
                        OkHttpUtils.getInstance(registerActivity.this).post("http://139.196.122.222:8080/sms/code",needSMSCode.toString(),
                                new OkHttpUtils.MyCallback() {
                                    @Override
                                    public void success(Response response) throws IOException {
                                        JSONObject object = JSON.parseObject(response.body().string());
                                        Log.d("登录", "success: "+ object);;
                                        int code = object.getInteger("code");
                                        if (code == 200) {
                                            uuid = object.getString("uuid");
                                            Log.d("登录", "success: 验证码成功发送至"+username);;
                                        } else
                                            Log.d("登录", "success: 请求错误");;
                                    }
                                    @Override
                                    public void failed(IOException e) {
                                        Log.d("登录", "fail: 请求失败");;
                                    }
                                });
                    }
                }
            }
        });

        toLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(registerActivity.this, LoginActivity.class);
                startActivity(toLogin);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = userNameEditText.getText().toString();
                smsCode = smsCodeEditText.getText().toString();
                password = userPasswordEditText.getText().toString();
                registerBySMS(username,smsCode,password,uuid);
            }
        });
    }

    private Handler registerHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(registerActivity.this, "您已经成功完成注册", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void registerBySMS(String username, String smsCode, String password,String uuid) {
        JSONObject SMSRegister = new JSONObject();
        SMSRegister.put("username",username);
        SMSRegister.put("smsCode",smsCode);
        SMSRegister.put("password",password);
        SMSRegister.put("uuid",uuid);
        OkHttpUtils.getInstance(registerActivity.this).post("http://139.196.122.222:8080/register", SMSRegister.toString(),
                new OkHttpUtils.MyCallback() {
                    @Override
                    public void success(Response response) throws IOException {
                        Log.d("注册", "注册信息为 "+SMSRegister.toString());
                        JSONObject object = JSON.parseObject(response.body().string());
                        int code = object.getInteger("code");
                        if (code == 200) {
                            Log.d("注册", "success: 注册成功");
                            Message message = new Message();
                            message.what = 1;
                            registerHandle.sendMessage(message);
                        } else if(code==500)
                            Log.d("注册", "手机号码不一致"+object);
                    }
                    @Override
                    public void failed(IOException e) {
                        Log.d("注册", "发送失败，可能是没有网络");
                    }
                });
    }

    //    发送验证码倒计时
    private Runnable myRunable = new Runnable() {
        @Override
        public void run() {
            if(wait >=0) {
                sendSMSButton.setText(String.valueOf(wait));
                handler.postDelayed(this, 1000);
                //从当前时间开始延迟delayMillis时间后执行Runnable
                wait--;
            }
            else{
                wait = 60;
                sendSMSButton.setText("重新发送");
                sendSMSButton.setEnabled(true);
                handler.removeCallbacks(this);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerHandle.removeCallbacks(null);
    }
}