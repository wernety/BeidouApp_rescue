package com.beidouapp.model.messages;

import java.util.List;

/**
 * 用于返回其他人位置信息的json解析包的方法，json包的形式为
 *
 */

public class recOtherPositions {

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

    public class Data {

        private List<Position> position;

        public void setPosition(List<Position> position) {
            this.position = position;
        }

        public List<Position> getPosition() {
            return position;
        }


        public class Position {

            private String lat;
            private int latDir;
            private String Lng;
            private int LngDir;
            private long posTime;
            private String speed;
            private String dviceld;

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLat() {
                return lat;
            }

            public void setLatDir(int latDir) {
                this.latDir = latDir;
            }

            public int getLatDir() {
                return latDir;
            }

            public void setLng(String lng) {
                Lng = lng;
            }

            public String getLng() {
                return Lng;
            }

            public void setLngDir(int lngDir) {
                LngDir = lngDir;
            }

            public int getLngDir() {
                return LngDir;
            }

            public void setPosTime(long posTime) {
                this.posTime = posTime;
            }

            public long getPosTime() {
                return posTime;
            }

            public void setSpeed(String speed) {
                this.speed = speed;
            }

            public String getSpeed() {
                return speed;
            }

            public void setDviceld(String dviceld) {
                this.dviceld = dviceld;
            }

            public String getDviceld() {
                return dviceld;
            }


        }
    }
}


