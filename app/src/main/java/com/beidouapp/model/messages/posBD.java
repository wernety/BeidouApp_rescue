package com.beidouapp.model.messages;

import java.util.List;

public class posBD {
    private List<String> deviceIds;

    public posBD(){
        super();
    }

    public posBD(List<String > deviceIds){
        this.deviceIds = deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }
}
