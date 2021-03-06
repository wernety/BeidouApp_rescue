package com.beidouapp.model.messages;

import java.util.List;

public class tracePosFromBD {
    private String rtnCode;
    private String rtnMsg;
    private Data data;
    private long time;
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

    public static class Data {

        private List<Position> position;
        public void setPosition(List<Position> position) {
            this.position = position;
        }
        public List<Position> getPosition() {
            return position;
        }

    }

    public static class Position {

        private String deviceId;
        private long posTime;
        private int lngDir;
        private String lng;
        private int latDir;
        private String lat;
        private String speed;
        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }
        public String getDeviceId() {
            return deviceId;
        }

        public void setPosTime(long posTime) {
            this.posTime = posTime;
        }
        public long getPosTime() {
            return posTime;
        }

        public void setLngDir(int lngDir) {
            this.lngDir = lngDir;
        }
        public int getLngDir() {
            return lngDir;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }
        public String getLng() {
            return lng;
        }

        public void setLatDir(int latDir) {
            this.latDir = latDir;
        }
        public int getLatDir() {
            return latDir;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
        public String getLat() {
            return lat;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }
        public String getSpeed() {
            return speed;
        }

    }
}
