package com.beidouapp.model.messages;

import java.util.List;

public class otherStarLocFromLiang {


        private List<Data> data;
        public void setData(List<Data> data) {
            this.data = data;
        }
        public List<Data> getData() {
            return data;
        }


    public static class Data {

        private String coordDesc;
        private String lngDir;
        private String releaseUserID;
        private String latDir;
        private String coordLng;
        private int coordLegend;
        private int coordStatus;
        private String coordName;
        private int id;
        private String coordLat;
        private String CoordTag;

        public String getCoordTag() {
            return CoordTag;
        }

        public void setCoordTag(String coordTag) {
            CoordTag = coordTag;
        }

        public void setCoordDesc(String coordDesc) {
            this.coordDesc = coordDesc;
        }
        public String getCoordDesc() {
            return coordDesc;
        }

        public void setLngDir(String lngDir) {
            this.lngDir = lngDir;
        }
        public String getLngDir() {
            return lngDir;
        }

        public void setReleaseUserID(String releaseUserID) {
            this.releaseUserID = releaseUserID;
        }
        public String getReleaseUserID() {
            return releaseUserID;
        }

        public void setLatDir(String latDir) {
            this.latDir = latDir;
        }
        public String getLatDir() {
            return latDir;
        }

        public void setCoordLng(String coordLng) {
            this.coordLng = coordLng;
        }
        public String getCoordLng() {
            return coordLng;
        }

        public void setCoordLegend(int coordLegend) {
            this.coordLegend = coordLegend;
        }
        public int getCoordLegend() {
            return coordLegend;
        }

        public void setCoordStatus(int coordStatus) {
            this.coordStatus = coordStatus;
        }
        public int getCoordStatus() {
            return coordStatus;
        }

        public void setCoordName(String coordName) {
            this.coordName = coordName;
        }
        public String getCoordName() {
            return coordName;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setCoordLat(String coordLat) {
            this.coordLat = coordLat;
        }
        public String getCoordLat() {
            return coordLat;
        }

    }

}
