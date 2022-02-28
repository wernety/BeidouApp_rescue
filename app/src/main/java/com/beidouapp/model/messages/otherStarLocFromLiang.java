package com.beidouapp.model.messages;

import java.util.List;

public class otherStarLocFromLiang {


    private int code;
    private String msg;
    private List<Data> data;
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

    public void setData(List<Data> data) {
        this.data = data;
    }
    public List<Data> getData() {
        return data;
    }


    public static class Data {

        private int id;
        private String releaseUserID;
        private String coordLng;
        private String coordLat;
        private String lngDir;
        private String latDir;
        private int coordLegend;
        private String coordName;
        private String coordDesc;
        private int coordStatus;
        private String coordTag;
        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setReleaseUserID(String releaseUserID) {
            this.releaseUserID = releaseUserID;
        }
        public String getReleaseUserID() {
            return releaseUserID;
        }

        public void setCoordLng(String coordLng) {
            this.coordLng = coordLng;
        }
        public String getCoordLng() {
            return coordLng;
        }

        public void setCoordLat(String coordLat) {
            this.coordLat = coordLat;
        }
        public String getCoordLat() {
            return coordLat;
        }

        public void setLngDir(String lngDir) {
            this.lngDir = lngDir;
        }
        public String getLngDir() {
            return lngDir;
        }

        public void setLatDir(String latDir) {
            this.latDir = latDir;
        }
        public String getLatDir() {
            return latDir;
        }

        public void setCoordLegend(int coordLegend) {
            this.coordLegend = coordLegend;
        }
        public int getCoordLegend() {
            return coordLegend;
        }

        public void setCoordName(String coordName) {
            this.coordName = coordName;
        }
        public String getCoordName() {
            return coordName;
        }

        public void setCoordDesc(String coordDesc) {
            this.coordDesc = coordDesc;
        }
        public String getCoordDesc() {
            return coordDesc;
        }

        public void setCoordStatus(int coordStatus) {
            this.coordStatus = coordStatus;
        }
        public int getCoordStatus() {
            return coordStatus;
        }

        public void setCoordTag(String coordTag) {
            this.coordTag = coordTag;
        }
        public String getCoordTag() {
            return coordTag;
        }

    }

}
