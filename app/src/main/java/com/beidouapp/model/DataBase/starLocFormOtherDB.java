package com.beidouapp.model.DataBase;

import org.litepal.crud.LitePalSupport;

/**
 * 此数据库将用来存储从亮哥那儿post来的收藏点信息
 * 其中的selfID可以不使用
 */
public class starLocFormOtherDB extends LitePalSupport {
    private String selfID;      //这里的uid不用设置
    private String latitude;
    private String lontitude;
    private String text;  //显示长江大桥等名称
    private String tag;   //显示吃饭点，集合点等
    private String uid;
    private String status;  //设置状态
    private String locInfo; // 自建点详细信息
    private int legend;// 创建图例

    public String getSelfID() {
        return selfID;
    }

    public void setSelfID(String selfID) {
        this.selfID = selfID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLontitude() {
        return lontitude;
    }

    public void setLontitude(String lontitude) {
        this.lontitude = lontitude;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocInfo() {
        return locInfo;
    }

    public void setLocInfo(String locInfo) {
        this.locInfo = locInfo;
    }

    public int getLegend() {
        return legend;
    }

    public void setLegend(int legend) {
        this.legend = legend;
    }
}
