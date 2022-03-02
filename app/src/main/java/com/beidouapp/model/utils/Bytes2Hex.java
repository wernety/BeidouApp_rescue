package com.beidouapp.model.utils;

public class Bytes2Hex {
    public static String bytes2hex(byte[] bts) //加密字节数组转十六进制字符串
    {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF)); //转十六进制字符

            if (tmp.length() == 1) {
                des += "0";

            }
            des += tmp;
        }
        return des;
    }
}
