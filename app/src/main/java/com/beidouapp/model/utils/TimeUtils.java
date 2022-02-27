package com.beidouapp.model.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    /**
     * 差分时间
     * @param createTime
     * @return
     */
    public static String diffTime(String createTime) {
        //createTime单位毫秒
        String temp = "";
        try {
            long now = System.currentTimeMillis() ;
            long publish = Long.parseLong(createTime);
            long diff = Math.abs((now - publish)/1000);
            long months = diff / (60 * 60 * 24*30);
            long days = diff / (60 * 60 * 24);
            long hours = (diff - days * (60 * 60 * 24)) / (60 * 60);
            long minutes = (diff - days * (60 * 60 * 24) - hours * (60 * 60)) / 60;
            if (months > 0) {
                temp = months + "月前";
            } else if (days > 0) {
                temp = days + "天前";
            } else if (hours > 0) {
                temp = hours + "小时前";
            } else if (minutes > 0){
                temp = minutes + "分钟前";
            } else {
                temp = "刚刚";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return formatTime(createTime);
        }
        return temp;
    }


    /**
     * 将时间戳转换成分秒时
     * @param timeMillis
     * @return
     */
    private static String formatTime(String timeMillis) {
        long timeMillisl =Long.parseLong(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillisl);
        return simpleDateFormat.format(date);
    }
}
