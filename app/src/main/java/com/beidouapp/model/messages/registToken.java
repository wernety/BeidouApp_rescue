package com.beidouapp.model.messages;

public class registToken {
    private String rtnCode;
    private String rtnMsg;
    private Data data;
    private long time;

    registToken(){
        super();
    }

    public registToken(String rtnCode, String rtnMsg, Data data, long time){
        this.rtnCode = rtnCode;
        this.rtnMsg =rtnMsg;
        this.data = data;
        this.time = time;
    }



    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }
    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnMsg(String rtnMsg) {
        this.rtnMsg = rtnMsg;
    }
    public String getRtnMsg() {
        return rtnMsg;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public long getTime() {
        return time;
    }

    public class Data {

        private String token;
        public void setToken(String token) {
            this.token = token;
        }
        public String getToken() {
            return token;
        }
    }
}
