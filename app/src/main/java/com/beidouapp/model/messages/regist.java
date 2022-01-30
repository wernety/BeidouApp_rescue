package com.beidouapp.model.messages;

public class regist {
    private String appId;
    private String timestamp;
    private String sign;

    public regist(String appId, String timestamp, String sign){
        this.sign = sign;
        this.timestamp = timestamp;
        this.appId = appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getAppId() {
        return appId;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
    public String getSign() {
        return sign;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getTimestamp() {
        return timestamp;
    }
}
