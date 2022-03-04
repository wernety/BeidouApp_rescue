package com.beidouapp.ui;

import static com.beidouapp.model.utils.checkUtils.isMobileNO;
import static com.beidouapp.model.utils.checkUtils.isNetworkConnected;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.R;
import com.beidouapp.model.utils.OkHttpUtils;

import java.io.IOException;

import okhttp3.Response;

public class registerActivity extends AppCompatActivity {
    private EditText userNameEditText, userPasswordEditText, smsCodeEditText;
    private Button passwordHideButton, sendSMSButton, registerButton, toLoginButton;
    private boolean ifHide = true;
    Handler handler = new Handler(); //验证码发送等待倒计时
    int wait = 60; //等待60秒
    private String username;
    private String password;
    private String smsCode; //验证码
    private String uuid; //验证码登录的唯一标识
    private boolean ifNetwork; //当前是否联网
    private final String networkTip = "当前没有网络"; //网络状态提示
    private static final String TAG = "注册";
    
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
        ifNetwork = isNetworkConnected(registerActivity.this);

        passwordHideButton.setOnClickListener(v -> {
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
        });

        sendSMSButton.setOnClickListener(v -> {
            if (v.getId() ==R.id.btn_send_code){
                username = userNameEditText.getText().toString();
                boolean isPhoneNum = isMobileNO(userNameEditText.getText().toString());
                if (TextUtils.isEmpty(username)){
                    Toast.makeText(registerActivity.this, "手机号不能为空", Toast.LENGTH_LONG).show();
                }else if (!isPhoneNum){
                    userNameEditText.setText("");
                    Toast.makeText(registerActivity.this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
                }else if(!ifNetwork){
                    Toast.makeText(registerActivity.this, networkTip, Toast.LENGTH_LONG).show();
                }
                else {
                    //如果手机号输入正确且有网络
                    //先设置按钮不可用，然后执行倒计时，给后端提示发送验证码
                    v.setEnabled(false);
                    handler.postDelayed(myRunnable,1000);
                    JSONObject needSMSCode = new JSONObject();
                    Toast.makeText(registerActivity.this, "成功发送至"+username, Toast.LENGTH_LONG).show();
                    needSMSCode.put("mobile",username);
                    OkHttpUtils.getInstance(registerActivity.this).post("http://139.196.122.222:8080/sms/code",needSMSCode.toString(),
                            new OkHttpUtils.MyCallback() {
                                @Override
                                public void success(Response response) throws IOException {
                                    JSONObject object = JSON.parseObject(response.body().string());
                                    Log.d(TAG, "success: "+ object);
                                    int code = object.getInteger("code");
                                    String msg= object.getString("msg");
                                    if (code == 200) {
                                        uuid = object.getString("uuid");
                                        registerActivity.this.runOnUiThread(() -> Toast.makeText(registerActivity.this, msg, Toast.LENGTH_LONG).show());
                                        Log.d(TAG, "success: 验证码成功发送至"+username);
                                    } else
                                        Log.d(TAG, "success: 请求错误");
                                }
                                @Override
                                public void failed(IOException e) {
                                    Log.d(TAG, "fail: 请求失败");
                                }
                            });
                }
            }
        });

        toLoginButton.setOnClickListener(v -> {
            Intent toLogin = new Intent(registerActivity.this, LoginActivity.class);
            startActivity(toLogin);
        });

        registerButton.setOnClickListener(v -> {
            username = userNameEditText.getText().toString();
            smsCode = smsCodeEditText.getText().toString();
            password = userPasswordEditText.getText().toString();
            boolean isPhoneNum = isMobileNO(username);
            if (TextUtils.isEmpty(username)){
                Toast.makeText(registerActivity.this, "手机号不能为空", Toast.LENGTH_LONG).show();
            }else if (!isPhoneNum){
                userNameEditText.setText("");
                Toast.makeText(registerActivity.this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
            }else if(TextUtils.isEmpty(smsCode)){
                Toast.makeText(registerActivity.this, "验证码不能为空", Toast.LENGTH_LONG).show();
            }
            else if(TextUtils.isEmpty(password)){
                Toast.makeText(registerActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
            }
            else if(!ifNetwork){
                Toast.makeText(registerActivity.this, networkTip, Toast.LENGTH_LONG).show();
            }
            else
            registerBySMS(username,smsCode,password,uuid);
        });
    }


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
//                        Log.d(TAG, "注册信息为 "+SMSRegister.toString());
                        JSONObject object = JSON.parseObject(response.body().string());
                        int code = object.getInteger("code");
                        String msg = object.getString("msg");
                        if (code == 200) {
                            registerActivity.this.runOnUiThread(() -> Toast.makeText(registerActivity.this, msg, Toast.LENGTH_LONG).show());
                            Log.d(TAG, "success: 注册成功");
                        } else if(code==500)
                            registerActivity.this.runOnUiThread(() -> Toast.makeText(registerActivity.this, msg, Toast.LENGTH_LONG).show());
                            Log.d(TAG, "code=500"+object);
                    }
                    @Override
                    public void failed(IOException e) {
                        Log.d(TAG, "发送失败，可能是没有网络");
                    }
                });
    }

    //    发送验证码倒计时
    private Runnable myRunnable = new Runnable() {
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
    }
}