package com.beidouapp.model.utils;

/**
 * 自建点的Json模板创建错了位置，不想变了，免得出故障
 */
public class selfPosJson {
    private String releaseUserID;  //发布者ID
    private String coordLng;//纬度
    private String coordLat;//经度
    private String lngDir;//北纬那个北字
    private String latDir;//东经那个东字
    private int coordLegend;//图例
    private String coordName;//描述（长江大桥）
    private String coordDesc;//详情（和小梓薇约会的地方）
    private int coordStatus;//状态（是否发布）
    private String coordTag;

    public selfPosJson(String releaseUserID, String coordLng, String coordLat,
                String lngDir, String latDir, int coordLegend, String coordName, String coordDesc, int coordStatus){
        this.releaseUserID = releaseUserID;
        this.coordLng = coordLng;
        this.coordLat = coordLat;
        this.latDir = "E";
        this.lngDir = "W";
        this.coordLegend = coordLegend;
        this.coordName = coordName;
        this.coordDesc = coordDesc;
        this.coordStatus = 1;
    }

    public selfPosJson(String releaseUserID, String coordLng, String coordLat,
                       int coordLegend, String coordName, String coordDesc){
        this.releaseUserID = releaseUserID;
        this.coordLng = coordLng;
        this.coordLat = coordLat;
        this.latDir = "E";
        this.lngDir = "W";
        this.coordLegend = coordLegend;
        this.coordName = coordName;
        this.coordDesc = coordDesc;
        this.coordStatus = 1;
    }

    public selfPosJson(String releaseUserID, String coordLng, String coordLat,
                       int coordLegend, String coordName, String coordDesc, int coordStatus){
        this.releaseUserID = releaseUserID;
        this.coordLng = coordLng;
        this.coordLat = coordLat;
        this.latDir = "E";
        this.lngDir = "W";
        this.coordLegend = coordLegend;
        this.coordName = coordName;
        this.coordDesc = coordDesc;
        this.coordStatus = coordStatus;
    }
    public selfPosJson(String releaseUserID, String coordLng, String coordLat,
                       int coordLegend, String coordName, String coordDesc, int coordStatus, String coordTag){
        this.releaseUserID = releaseUserID;
        this.coordLng = coordLng;
        this.coordLat = coordLat;
        this.latDir = "E";
        this.lngDir = "W";
        this.coordLegend = coordLegend;
        this.coordName = coordName;
        this.coordDesc = coordDesc;
        this.coordStatus = coordStatus;
        this.coordTag = coordTag;
    }


    public String getReleaseUserID() {
        return releaseUserID;
    }

    public void setReleaseUserID(String releaseUserID) {
        this.releaseUserID = releaseUserID;
    }

    public void setcoordLng(String coordLng) {
        this.coordLng = coordLng;
    }
    public String getcoordLng() {
        return coordLng;
    }

    public void setcoordLat(String coordLat) {
        this.coordLat = coordLat;
    }
    public String getcoordLat() {
        return coordLat;
    }

    public void setlngDir(String lngDir) {
        this.lngDir = lngDir;
    }
    public String getlngDir() {
        return lngDir;
    }

    public void setlatDir(String latDir) {
        this.latDir = latDir;
    }
    public String getlatDir() {
        return latDir;
    }

    public void setcoordLegend(int coordLegend) {
        this.coordLegend = coordLegend;
    }
    public int getcoordLegend() {
        return coordLegend;
    }

    public void setcoordName(String coordName) {
        this.coordName = coordName;
    }
    public String getcoordName() {
        return coordName;
    }

    public void setcoordDesc(String coordDesc) {
        this.coordDesc = coordDesc;
    }
    public String getcoordDesc() {
        return coordDesc;
    }

    public void setcoordStatus(int coordStatus) {
        this.coordStatus = coordStatus;
    }
    public int getcoordStatus() {
        return coordStatus;
    }

    public String getcoordTag() {
        return coordTag;
    }

    public void setcoordTag(String coordTag) {
        this.coordTag = coordTag;
    }

    @Override
    public String toString() {
        return "selfPosJson{" +
                "releaseUserID='" + releaseUserID + '\'' +
                ", coordLng='" + coordLng + '\'' +
                ", coordLat='" + coordLat + '\'' +
                ", lngDir='" + lngDir + '\'' +
                ", latDir='" + latDir + '\'' +
                ", coordLegend=" + coordLegend +
                ", coordName='" + coordName + '\'' +
                ", coordDesc='" + coordDesc + '\'' +
                ", coordStatus=" + coordStatus +
                '}';
    }
}
