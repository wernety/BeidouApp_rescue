package com.beidouapp.ui;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.beidouapp.model.DataBase.DBHelper;

import org.litepal.LitePal;

public class DemoApplication extends Application {
    SQLiteDatabase dbForRecord;
    public DBHelper dbHelper;
    //使用这个dbHelper再某个表中添加数据的方法是
    /*
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    db.insert()
    */


    //global variable userID
    private String userID;
    private String curToken;
    private String Token;

    public String getToken() {
        return Token;
    }
    public String getCurToken() {
        return curToken;
    }
    public String getUserID() {
        return userID;
    }

    public void setToken(String token) {
        Token = token;
    }
    public void setCurToken(String curToken) {
        this.curToken = curToken;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
