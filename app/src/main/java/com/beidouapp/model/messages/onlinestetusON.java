package com.beidouapp.model.messages;

import java.util.List;

//这是实际使用的获取亮哥的状态的格式
public class onlinestetusON {
    private int code;
    private String msg;
    private Data data;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }


    public static class Data {

        private boolean success;
        private int totalCount;
        private List<String> deviceId;
        private List<Integer> status;
        public void setSuccess(boolean success) {
            this.success = success;
        }
        public boolean getSuccess() {
            return success;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
        public int getTotalCount() {
            return totalCount;
        }

        public void setDeviceId(List<String> deviceId) {
            this.deviceId = deviceId;
        }
        public List<String> getDeviceId() {
            return deviceId;
        }

        public void setStatus(List<Integer> status) {
            this.status = status;
        }
        public List<Integer> getStatus() {
            return status;
        }

    }

}
