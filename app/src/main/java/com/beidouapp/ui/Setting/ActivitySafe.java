package com.beidouapp.ui.Setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beidouapp.R;

import com.beidouapp.model.User;
import com.beidouapp.model.utils.SharePerferenceUtils;
import com.google.gson.Gson;

public class ActivitySafe extends AppCompatActivity {
    EditText edit_phone;
    EditText edit_email;
    private String userId;
    private User user;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);

        Toolbar toolbar = findViewById(R.id.safe1);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edit_phone = findViewById(R.id.edit_phone);
        edit_email = findViewById(R.id.youxiang);
        AutoSeparateTextWatcher textWatcher = new AutoSeparateTextWatcher(edit_phone);
        textWatcher.setRULES(new int[]{3,4,4});
        textWatcher.setSeparator('-');
        edit_phone.addTextChangedListener(textWatcher);

        Button btn_mima = findViewById(R.id.btn_mima);
        btn_mima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mEdit = (EditText) findViewById(R.id.text1);
                mEdit.setEnabled(true);
            }
        });

        Button btn_num = findViewById(R.id.btn_shouji);
        btn_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edit_phone.getText().toString())){
                    showToast("请输入手机号");
                    return;
                }
                user.setPhone(edit_phone.getText().toString());
                SharePerferenceUtils.putString(ActivitySafe.this, userId+"_info", gson.toJson(user));
                showToast("保存成功");
            }
        });
        Button btn_yx = findViewById(R.id.btn_youxiang);
        btn_yx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edit_email.getText().toString())){
                    showToast("请输入邮箱");
                    return;
                }
                user.setEmail(edit_email.getText().toString());
                SharePerferenceUtils.putString(ActivitySafe.this, userId+"_info", gson.toJson(user));
                showToast("保存成功");
            }
        });

        userId = SharePerferenceUtils.getString(this, "userId","");
        String temp = SharePerferenceUtils.getString(this, userId+"_info","");
        if(!TextUtils.isEmpty(temp)){
            user = gson.fromJson(temp, User.class);
            edit_phone.setText(user.getPhone());
            edit_email.setText(user.getEmail());
        }else {
            user = new User();
            user.setUserID(userId);
        }
    }

    private void showToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
