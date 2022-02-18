package com.beidouapp.model.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.model.messages.HeartbeatMsg;
import com.beidouapp.model.messages.Message4Receive;
import com.beidouapp.model.messages.Message4Send;
import com.beidouapp.model.messages.Other_loc;
import com.beidouapp.model.messages.recOtherPositions;
import com.beidouapp.model.messages.regist;
import com.beidouapp.model.messages.registToken;
import com.beidouapp.model.messages.regist_fail;
import com.beidouapp.model.messages.*;

import java.util.List;

public class JSONUtils {
    public static String sendJSON (Message4Send message4send) {
        return JSON.toJSONString(message4send, true);
    }
    public static String sendJSON (HeartbeatMsg heartbeatMsg) {
        return JSON.toJSONString(heartbeatMsg, true);
    }

    public static String sendJson(regist regist){
        return JSON.toJSONString(regist,true);
    }

    public static String sendJson(posBD posBD){ return JSON.toJSONString(posBD, true);}

    public static String sendJSON (Object o) {
        return JSON.toJSONString(o, true);
    }

    public static  String sendJson(selfPosJson selfPosJson){
        return JSON.toJSONString(selfPosJson, true);
    }

   public static String sendJson(tracsPos tracsPos){
        return JSON.toJSONString(tracsPos, true);
   }

    public static recOtherPositions receiveLocJSON(String json){
        recOtherPositions recOtherPositions = JSON.parseObject(json, com.beidouapp.model.messages.recOtherPositions.class);
        return recOtherPositions;
    }

    public static String sendJson(Other_loc other_loc){
        return JSON.toJSONString(other_loc, true);
    }//根据选择用户的用户名


    public static Message4Receive receiveJSON (String json) {
        Message4Receive message4Receive = JSON.parseObject(json, Message4Receive.class);
        return message4Receive;
    }

    public static registToken receiveTokenJson(String json){
        registToken registToken = JSON.parseObject(json, registToken.class);
        return registToken;
    }

    public static regist_fail receiveJson(String json){
        regist_fail regist_fail = JSON.parseObject(json, regist_fail.class);
        return regist_fail;
    }

    public static onlinestetusON receiveOnlinestetusONJson(String json){
        onlinestetusON onlinestetusON = JSON.parseObject(json, com.beidouapp.model.messages.onlinestetusON.class);
        return  onlinestetusON;
    }

    public static posFromBD receivePosFromBDJson(String json){
        posFromBD posFromBD = JSON.parseObject(json, posFromBD.class);
        return  posFromBD;
    }

    public static tracePosFromBD reciveTracePosFromBDJson(String json){
        tracePosFromBD tracePosFromBD = JSON.parseObject(json, tracePosFromBD.class);
        return tracePosFromBD;
    }

    public static JSONObject Receive(String json) {
        JSONObject object = JSON.parseObject(json);
        return object;
    }


}
