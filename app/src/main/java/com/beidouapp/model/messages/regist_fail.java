package com.beidouapp.model.messages;

public class regist_fail {
    private String data;
    private long time;
    private String rtnMsg;
    private String rtnCode;

    public regist_fail(){
        super();
    }

    public regist_fail(String data, long time, String rtnMsg, String rtnCode){
        this.data = data;
        this.rtnCode = rtnCode;
        this.rtnMsg = rtnMsg;
        this.time =time;

    }

    public void setData(String data) {
        this.data = data;
    }
    public String getData() {
        return data;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public long getTime() {
        return time;
    }

    public void setRtnMsg(String rtnMsg) {
        this.rtnMsg = rtnMsg;
    }
    public String getRtnMsg() {
        return rtnMsg;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }
    public String getRtnCode() {
        return rtnCode;
    }
}
