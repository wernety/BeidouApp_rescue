package com.beidouapp.model.messages;

public class tracsPos {
    private String deviceId;
    private long startTime;
    private long endTime;

    public tracsPos(){
        super();
    }

    public tracsPos(String deviceId, long startTime, long endTime){
        this.deviceId = deviceId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getDeviceId() {
        return deviceId;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public long getStartTime() {
        return startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public long getEndTime() {
        return endTime;
    }
}
