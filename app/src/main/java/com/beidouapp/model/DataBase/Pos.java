package com.beidouapp.model.DataBase;

import org.litepal.crud.LitePalSupport;

/**
 * 此表用于储存个人自建点信息
 * 经纬度，标签，用户，状态
 */
public class Pos extends LitePalSupport {
    private String latitude;
    private String lontitude;
    private String text;  //显示长江大桥等名称
    private String tag;   //显示吃饭点，集合点等
    private String uid;
    private String status;  //设置状态
    private String locInfo; // 自建点详细信息
    private int legend;// 创建图例

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

    public void setLocInfo(String locInfo) {
        this.locInfo = locInfo;
    }

    public void setLegend(int legend) {
        this.legend = legend;
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

    public String getLocInfo() {
        return locInfo;
    }

    public int getLegend() {
        return legend;
    }
}
