package com.beidouapp.model.utils;

/**
 * 自建点的Json模板创建错了位置，不想变了，免得出故障
 */
public class selfPosJson {
    private String release_user_ID;  //发布者ID
    private String coord_lng;//纬度
    private String coord_lat;//经度
    private String lng_dir;//北纬那个北字
    private String lat_dir;//东经那个东字
    private int coord_legend;//图例
    private String coord_name;//描述（长江大桥）
    private String coord_desc;//详情（和小梓薇约会的地方）
    private int coord_status;//状态（是否发布）

    selfPosJson(String release_user_ID, String coord_lng, String coord_lat,
                String lng_dir, String lat_dir, int coord_legend, String coord_name, String coord_desc, int coord_status){
        this.release_user_ID = release_user_ID;
        this.coord_lng = coord_lng;
        this.coord_lat = coord_lat;
        this.lat_dir = "E";
        this.lng_dir = "W";
        this.coord_legend = coord_legend;
        this.coord_name = coord_name;
        this.coord_desc = coord_desc;
        this.coord_status = 1;
    }

    public selfPosJson(String release_user_ID, String coord_lng, String coord_lat,
                       int coord_legend, String coord_name, String coord_desc){
        this.release_user_ID = release_user_ID;
        this.coord_lng = coord_lng;
        this.coord_lat = coord_lat;
        this.lat_dir = "E";
        this.lng_dir = "W";
        this.coord_legend = coord_legend;
        this.coord_name = coord_name;
        this.coord_desc = coord_desc;
        this.coord_status = 1;
    }

    public void setRelease_user_ID(String release_user_ID) {
        this.release_user_ID = release_user_ID;
    }
    public String getRelease_user_ID() {
        return release_user_ID;
    }

    public void setCoord_lng(String coord_lng) {
        this.coord_lng = coord_lng;
    }
    public String getCoord_lng() {
        return coord_lng;
    }

    public void setCoord_lat(String coord_lat) {
        this.coord_lat = coord_lat;
    }
    public String getCoord_lat() {
        return coord_lat;
    }

    public void setLng_dir(String lng_dir) {
        this.lng_dir = lng_dir;
    }
    public String getLng_dir() {
        return lng_dir;
    }

    public void setLat_dir(String lat_dir) {
        this.lat_dir = lat_dir;
    }
    public String getLat_dir() {
        return lat_dir;
    }

    public void setCoord_legend(int coord_legend) {
        this.coord_legend = coord_legend;
    }
    public int getCoord_legend() {
        return coord_legend;
    }

    public void setCoord_name(String coord_name) {
        this.coord_name = coord_name;
    }
    public String getCoord_name() {
        return coord_name;
    }

    public void setCoord_desc(String coord_desc) {
        this.coord_desc = coord_desc;
    }
    public String getCoord_desc() {
        return coord_desc;
    }

    public void setCoord_status(int coord_status) {
        this.coord_status = coord_status;
    }
    public int getCoord_status() {
        return coord_status;
    }

    @Override
    public String toString() {
        return "selfPosJson{" +
                "release_user_ID='" + release_user_ID + '\'' +
                ", coord_lng='" + coord_lng + '\'' +
                ", coord_lat='" + coord_lat + '\'' +
                ", lng_dir='" + lng_dir + '\'' +
                ", lat_dir='" + lat_dir + '\'' +
                ", coord_legend=" + coord_legend +
                ", coord_name='" + coord_name + '\'' +
                ", coord_desc='" + coord_desc + '\'' +
                ", coord_status=" + coord_status +
                '}';
    }
}
