package com.beidouapp.ui.Setting;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beidouapp.R;
import com.beidouapp.model.User;
import com.beidouapp.model.utils.SharePerferenceUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityUser extends AppCompatActivity {
    private TextView textView,mTvBirthday;
    private EditText mEtName;
    private CircleImageView mImgHead;
    private RadioGroup id_radiogroup;
    private RadioButton radio_1;
    private RadioButton radio_2;
    private String userId;
    private User user;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.user1);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mEtName = findViewById(R.id.mEtName);
        id_radiogroup = findViewById(R.id.id_radiogroup);
        radio_1 = findViewById(R.id.radio_1);
        radio_2 = findViewById(R.id.radio_2);
        mTvBirthday = findViewById(R.id.mTvBirthday);
        mImgHead = findViewById(R.id.mImgHead);

        findViewById(R.id.mBtnSave).setOnClickListener(view -> save());
        findViewById(R.id.mTvBirthday).setOnClickListener(view -> showDayDialog());
        findViewById(R.id.mImgHead).setOnClickListener(view -> selectImg());

        userId = SharePerferenceUtils.getString(this, "userId","");
        String temp = SharePerferenceUtils.getString(this, userId+"_info","");
        if(!TextUtils.isEmpty(temp)){
            user = gson.fromJson(temp, User.class);
            mEtName.setText(user.getUserName());
            if(!TextUtils.isEmpty(user.getSex())){
                if("女".equals(user.getSex())){
                    radio_2.setChecked(true);
                }else {
                    radio_1.setChecked(true);
                }
            }
            if(!TextUtils.isEmpty(user.getBirthday())){
                String birthday = user.getBirthday();
                mTvBirthday.setText("生日："+birthday);
            }
            if(!TextUtils.isEmpty(user.getHead())){
                Glide.with(this).load(user.getHead()).into(mImgHead);
            }
        }else {
            user = new User();
            user.setUserId(userId);
        }
    }

    private void save(){
        if(!TextUtils.isEmpty(mEtName.getText().toString())){
            user.setUserName(mEtName.getText().toString());
        }
        if(id_radiogroup.getCheckedRadioButtonId()== R.id.radio_2){
            user.setSex("女");
        }else {
            user.setSex("男");
        }

        String birthday = mTvBirthday.getText().toString().replace("生日：", "");
        user.setBirthday(birthday);

        SharePerferenceUtils.putString(this, userId+"_info", gson.toJson(user));
        showToast("保存成功");
        finish();
    }

    private void selectImg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("请选择操作类型")
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            startActivityForResult(intent, 12);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("图库", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addPic();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }

    private void addPic(){
        //选择照片
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //这里要传一个整形的常量RESULT_LOAD_IMAGE到startActivityForResult()方法。
        startActivityForResult(intent, 11);
    }

    private void showDayDialog(){
        Calendar calendar= Calendar.getInstance();
        new DatePickerDialog( this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mTvBirthday.setText("生日："+year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }
        ,calendar.get(Calendar.YEAR)
        ,calendar.get(Calendar.MONTH)
        ,calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == RESULT_OK && null != data) {
            //读取选择的照片
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            //查询我们需要的数据
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            //更新头像
            Glide.with(this).load(picturePath).into(mImgHead);

            user.setHead(picturePath);
        }
        if(requestCode == 12)//拍照
        {
            try
            {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap)bundle.get("data");
                String photoPath = saveBitmap(userId + "_img.png", bitmap);
                user.setHead(photoPath);
                Glide.with(this).load(photoPath).into(mImgHead);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    /**
     * 保存图片
     * 返回path
     * @param name
     * @param bitmap
     * @return
     */
    public String saveBitmap(String name, Bitmap bitmap) {
        File file = new File(getFilesDir(), name);
        if(file.exists())
        {
            file.delete();
        }
        try
        {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            String path = file.getPath();
            return path;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
