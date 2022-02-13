package com.beidouapp.ui.fragment;


import java.io.Serializable;

/**
 * 用于收藏点和自建点的Adapter初始化列表显示数据的类
 */
public class starPos implements Serializable{
    private String Text;
    private String uid;
    private String status;
    private String tag;
    private String latitude;
    private String lontitude;
    private String locInfo;//信息
    private int legend;

    public starPos(){
    }

    public starPos(String Text, String uid, String status, String tag, String latitude, String lontitude,
                   String locInfo, int legend){
        this.latitude = latitude;
        this.lontitude = lontitude;
        this.status = status;
        this.tag = tag;
        this.uid = uid;
        this.Text = Text;
        this.locInfo = locInfo;
        this.legend = legend;
    }

    public String getStatus() {
        return status;
    }

    public String getUid() {
        return uid;
    }

    public String getLontitude() {
        return lontitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getText() {
        return Text;
    }

    public String getTag() {
        return tag;
    }

    public String getLocInfo() {
        return locInfo;
    }

    public int getLegend() {
        return legend;
    }

    @Override
    public String toString() {
        return "starPos{" +
                "Text='" + Text + '\'' +
                ", uid='" + uid + '\'' +
                ", status='" + status + '\'' +
                ", tag='" + tag + '\'' +
                ", latitude='" + latitude + '\'' +
                ", lontitude='" + lontitude + '\'' +
                ", locInfo='" + locInfo + '\'' +
                ", legend=" + legend +
                '}';
    }
}
