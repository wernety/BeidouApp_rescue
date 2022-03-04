package com.beidouapp.ui;

import static com.beidouapp.model.utils.checkUtils.isMobileNO;
import static com.beidouapp.model.utils.checkUtils.isNetworkConnected;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
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
    private static final String TAG = "登录";
    private TextView userNameText ,passwordText;
    private EditText userNameEditText, userPasswordEditText;
    private CheckBox rememberCheck;
    private Button loginButton; //登录
    private Button passwordHideButton; //是否显示密码
    private Button registerButton; //注册
    private Button switchLoginButton; // 切换登录方式
    private Button sendCodeButton; // 发送验证码
    private boolean ifHide = true; // 是否显示密码
    private boolean ifPassword = true; // 是否密码登录
    private boolean ifRemember = true; //是否记住密码
    private boolean ifNetwork; //当前是否联网
    private final String networkTip = "当前没有网络"; //网络状态提示
    Handler countHandler = new Handler(); //验证码发送等待倒计时
    int wait = 60; //等待60秒
    private String token;
    private String username;
    private String password;
    private String smsCode; //验证码
    private String uuid; //验证码登录的唯一标识
    private DemoApplication application;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        application = (DemoApplication) this.getApplicationContext();
        application.setFlag(false);
        checkPermissions();
        initUI();                                                                                   //初始化UI
        initListener();//初始化监听器
        ifNetwork = isNetworkConnected(LoginActivity.this);
        if(!ifNetwork){
            Toast.makeText(LoginActivity.this, networkTip, Toast.LENGTH_LONG).show();
        }
    }

    private void initUI() {
        userNameText = findViewById(R.id.text_username);
        passwordText = findViewById(R.id.text_password);
        userNameEditText = findViewById(R.id.edt_username);
        userPasswordEditText = findViewById(R.id.edt_password);
        sendCodeButton = findViewById(R.id.btn_send_code);
        loginButton = findViewById(R.id.btn_login);
        passwordHideButton = findViewById(R.id.btn_switch_psw);
        switchLoginButton = findViewById(R.id.btn_login_switch);
        registerButton =  findViewById(R.id.btn_register);
        rememberCheck = findViewById(R.id.remember);
        // 实例化SharedPreferences
        sp = getSharedPreferences("user", MODE_PRIVATE);
        //获取文件中的值
        username = sp.getString("name", ""); //第二个参数为不能正常获取时的值
        password = sp.getString("pwd", "");
        userNameEditText.setText(username);
        userPasswordEditText.setText(password);
    }

    private void initListener(){
        loginButton.setOnClickListener(v -> {
            username = userNameEditText.getText().toString();
            if(ifPassword){
                password = userPasswordEditText.getText().toString();
                if (TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "大哥，用户或者密码少填了一个", Toast.LENGTH_LONG).show();
                }else if(!ifNetwork) {
                    Toast.makeText(LoginActivity.this, networkTip, Toast.LENGTH_LONG).show();
                }else
                {
                    loginByPsw(username, password);
                }
            }
            else {
                smsCode = userPasswordEditText.getText().toString();
                if (TextUtils.isEmpty(smsCode)){
                    Toast.makeText(LoginActivity.this, "验证码不能为空", Toast.LENGTH_LONG).show();
                }else if(!ifNetwork){
                    Toast.makeText(LoginActivity.this, networkTip, Toast.LENGTH_LONG).show();
                }
                else {
                    loginBySMS(username, smsCode);
                }
            }
        });
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
        rememberCheck.setOnClickListener(v -> {
            if (rememberCheck.isChecked()) {
                ifRemember = true;
                Toast.makeText(LoginActivity.this, "记住密码", Toast.LENGTH_LONG).show();}
            else {
                ifRemember = false;
                Toast.makeText(LoginActivity.this, "不记住", Toast.LENGTH_LONG).show();}
        });
        switchLoginButton.setOnClickListener(v -> {
            if (v.getId() == R.id.btn_login_switch) {//从密码登录到验证码登录
                ifPassword = !ifPassword; //切换
                if (ifPassword) {
                    userNameText.setText("手机号");
                    passwordText.setText("密码");
                    switchLoginButton.setText("验证码登录");
                    userNameEditText.setHint("请输入用户名");
                    userPasswordEditText.setHint("请输入密码");
                    userPasswordEditText.setText("");
//                        检查当前的小眼睛状态决定密码是否显示
                    if (ifHide) {
                        userPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    else {
                        userPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    passwordHideButton.setVisibility(View.VISIBLE); //设置隐藏密码的小眼睛按钮可见
                    sendCodeButton.setVisibility(View.GONE); //设置发送验证码按钮不可见
                }
                else {
                    userNameText.setText("手机号");
                    passwordText.setText("验证码");
                    switchLoginButton.setText("账号密码登录");
                    userNameEditText.setHint("请输入手机号");
                    userNameEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                    userPasswordEditText.setHint("请输入验证码");
                    userPasswordEditText.setText("");
                    userPasswordEditText.setInputType(InputType.TYPE_CLASS_NUMBER); //设置输入模式为数字
                    passwordHideButton.setVisibility(View.GONE); //设置隐藏密码的小眼睛按钮不可见
                    sendCodeButton.setVisibility(View.VISIBLE);
                }
            }
        });

        sendCodeButton.setOnClickListener(v -> {
            if (v.getId() ==R.id.btn_send_code){
                username = userNameEditText.getText().toString();
                boolean isPhoneNum = isMobileNO(userNameEditText.getText().toString());
                if (TextUtils.isEmpty(username)){
                    Toast.makeText(LoginActivity.this, "手机号不能为空", Toast.LENGTH_LONG).show();
                }else if (!isPhoneNum){
                    userNameEditText.setText("");
                    Toast.makeText(LoginActivity.this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
                }else if(!ifNetwork){
                    Toast.makeText(LoginActivity.this, networkTip, Toast.LENGTH_LONG).show();
                }
                else {
                    //如果手机号输入正确且当前有网络状态
                    //先设置按钮不可用，然后执行倒计时，给后端提示发送验证码
                    v.setEnabled(false);
                    countHandler.postDelayed(myRunable,1000);
                    JSONObject needSMSCode = new JSONObject();
                    needSMSCode.put("mobile",username);
                    OkHttpUtils.getInstance(LoginActivity.this).post("http://139.196.122.222:8080/sms/code",needSMSCode.toString(),
                            new OkHttpUtils.MyCallback() {
                                @Override
                                public void success(Response response) throws IOException {
                                    JSONObject object = JSON.parseObject(response.body().string());
                                    Log.d("登录", "success: "+ object);
                                    int code = object.getInteger("code");
                                    if (code == 200) {
                                        uuid = object.getString("uuid");
                                        Log.d(TAG, "success: 验证码发送成功");
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

        registerButton.setOnClickListener(v -> {
            Intent  toRegister = new Intent(LoginActivity.this, registerActivity.class);
            startActivity(toRegister);
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

    private Handler getInfoHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                try {
                    OkHttpUtils.getInstance(LoginActivity.this).get("http://139.196.122.222:8080/getInfo",
                            token, new OkHttpUtils.MyCallback() {
                                @Override
                                public void success(Response response) throws IOException {
                                    JSONObject object = JSON.parseObject(response.body().string());
                                    int code = object.getInteger("code");
                                    String msg = object.getString("msg");
                                    if (code == 200) {
                                        User user = JSON.parseObject(object.getString("user"), User.class);
                                        application.setIndexID(user.getUserId());
                                        application.setNickName(user.getNickName());
                                        application.setUserID(user.getUserName());
                                        application.setUserPass(password);
                                        application.setToken(token);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (code == 500) {
                                        LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_LONG).show());
                                    }
                                }

                                @Override
                                public void failed(IOException e) {
                                    e.printStackTrace();
                                }
                            });
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }
    });


    /**
     * 账号密码方式登录
     * @param username
     * @param password
     */
    public void loginByPsw(String username, String password){
        try {
            User4Login user4Login = new User4Login(username, password);
            String json = JSONUtils.sendJSON(user4Login);
            Log.d("json", json);

            OkHttpUtils.getInstance(LoginActivity.this).post("http://139.196.122.222:8080/login", json,
                    new OkHttpUtils.MyCallback() {
                        @Override
                        public void success(Response response) throws IOException {
                            JSONObject object = JSON.parseObject(response.body().string());
                            int code = object.getInteger("code");
                            String msg = object.getString("msg");
                            if (code == 200) {
                                token = object.getString("token");
                                // 获取sp的编辑器
                                SharedPreferences.Editor edit = sp.edit();
                                if (ifPassword) { //如果采用账号密码登录，且选择了记住密码，将账号密码存到sp
                                    if(ifRemember){
                                        edit.putString("name", username);
                                        edit.putString("pwd", password);
                                        // 把edit进行提交
                                        edit.apply();
                                    }
                                    else {
                                        // 清除保存的信息
                                        edit.clear();
                                        edit.commit();
                                    }
                                }
                                Message message = new Message();
                                message.what = 1;
                                getInfoHandler.sendMessage(message);
                            } else if (code == 500) {
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                        @Override
                        public void failed(IOException e) {
                            Toast.makeText(LoginActivity.this, "网络未连接", Toast.LENGTH_LONG).show();
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
            Log.d("zw", "loginByPsw: 遇到问题直接退出了");
            e.printStackTrace();
        }
    }

    /**
     * 短信验证码登录
     * @param username
     * @param smsCode
     */
    private void loginBySMS(String username, String smsCode) {
        JSONObject SMSLogin = new JSONObject();
        SMSLogin.put("mobile",username);
        SMSLogin.put("smsCode",smsCode);
        SMSLogin.put("uuid",uuid);
        OkHttpUtils.getInstance(LoginActivity.this).post("http://139.196.122.222:8080/sms/login", SMSLogin.toString(),
                new OkHttpUtils.MyCallback() {
                    @Override
                    public void success(Response response) throws IOException {
                        JSONObject object = JSON.parseObject(response.body().string());
                        int code = object.getInteger("code");
                        String msg = object.getString("msg");
                        if (code == 200) {
                            Log.d("验证码登录", "success:成功");
                            token = object.getString("token");
                            // 获取sp的编辑器
                            SharedPreferences.Editor edit = sp.edit();
                            if (ifPassword) { //如果采用账号密码登录，且选择了记住密码，将账号密码存到sp
                                if(ifRemember){
                                    edit.putString("name", username);
                                    edit.putString("pwd", password);
                                    // 把edit进行提交
                                }
                                else {
                                    // 清除保存的信息
                                    edit.clear();
                                }
                                edit.apply();
                            }
                            Message message = new Message();
                            message.what = 1;
                            getInfoHandler.sendMessage(message);
                        } else if(code==500) {
                            LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show());
                        }
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
    }

    //    发送验证码倒计时
    private Runnable myRunable = new Runnable() {
        @Override
        public void run() {
            if(wait >=0) {
                sendCodeButton.setText(String.valueOf(wait));
                countHandler.postDelayed(this, 1000);
                //从当前时间开始延迟delayMillis时间后执行Runnable
                wait--;
            }
            else{
                wait = 60;
                sendCodeButton.setText("重新发送");
                sendCodeButton.setEnabled(true);
                countHandler.removeCallbacks(this);
            }
        }
    };



    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {                         //若无读写权限，结束程序
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "谢谢！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "未授权！", Toast.LENGTH_SHORT).show();
                    Process.killProcess(Process.myPid());
                    System.exit(0);
                }
                break;
            default:
                break;
        }
    }                                                                                                                                                               

    /**
     * @param
     * @return null
     * @Title
     * @parameter
     * @Description 杀死程序后能再次进入页面，无需登录
     * @author chx
     * @data 2022/2/27/027  15:31
     */
    @Override
    protected void onResume() {
        if(application.getFlag()){
            application.setFlag(true);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("uid", application.getUserID());
            intent.putExtra("upw", application.getUserPass());
            intent.putExtra("token", application.getToken());
            startActivity(intent);
            finish();
        }
        super.onResume();
    }
}