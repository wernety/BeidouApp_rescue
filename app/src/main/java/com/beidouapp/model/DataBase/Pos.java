package com.beidouapp.model.DataBase;

import org.litepal.crud.LitePalSupport;

/**
 * 此表用于储存个人自建点信息
 * 经纬度，标签，用户，状态
 */
public class Pos extends LitePalSupport {
    private String latitude;
    private String lontitude;
    private String text;
    private String tag;
    private String uid;
    private String status;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLontitude(String lontitude) {
        this.lontitude = lontitude;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLontitude() {
        return lontitude;
    }

    public String getTag() {
        return tag;
    }

    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    public String getText() {
        return text;
    }
}
