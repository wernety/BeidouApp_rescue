package com.beidouapp.ui;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.beidouapp.model.DataBase.DBHelper;
import com.beidouapp.model.User;
import com.beidouapp.model.Group;

import org.litepal.LitePal;

import java.util.List;

public class DemoApplication extends Application {
    SQLiteDatabase dbForRecord;
    public DBHelper dbHelper;
    //使用这个dbHelper再某个表中添加数据的方法是
    /*
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    db.insert()
    */

    private String userPass;
    private String org;
    private String userID;
    private String indexID;
    private String curToken;
    private String Token;
    private List<String> otherLocIDRecord;
    private List<User> friendList;
    private List<Group> groupList;
    private Boolean flag;

    public String getToken() {
        return Token;
    }
    public String getCurToken() {
        return curToken;
    }
    public String getUserID() {
        return userID;
    }
    public List<String> getOtherLocIDRecord() {
        return otherLocIDRecord;
    }
    public String getOrg() {
        return org;
    }
    public List<User> getFriendList() {
        return friendList;
    }
    public List<Group> getGroupList() {
        return groupList;
    }
    public String getUserPass() {
        return userPass;
    }
    public String getIndexID() {
        return indexID;
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
    public void setOtherLocIDRecord(List<String> otherLocIDRecord) {
        this.otherLocIDRecord = otherLocIDRecord;
    }
    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Boolean getFlag() {
        return flag;
    }
    public void setOrg(String org) {
        this.org = org;
    }
    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }
    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }
    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
    public void setIndexID(String indexID) {
        this.indexID = indexID;
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
