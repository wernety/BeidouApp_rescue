package com.beidouapp.model.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.model.Relation;
import com.beidouapp.model.User;
import com.beidouapp.model.messages.HeartbeatMsg;
import com.beidouapp.model.messages.Message4Receive;
import com.beidouapp.model.messages.Message4Send;
import com.beidouapp.model.messages.Other_loc;
import com.beidouapp.model.messages.recOtherPositions;
import com.beidouapp.model.messages.regist;
import com.beidouapp.model.messages.registToken;
import com.beidouapp.model.messages.regist_fail;
import com.beidouapp.model.messages.*;

import java.util.ArrayList;
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

    public static otherStarLocFromLiang reciveOtherStarLocFromLiang(String json){
        otherStarLocFromLiang otherStarLocFromLiang = JSON.parseObject(json, otherStarLocFromLiang.class);
        return otherStarLocFromLiang;
    }

    public static JSONObject Receive(String json) {
        JSONObject object = JSON.parseObject(json);
        return object;
    }

    /**
     * 组织关系json格式转换
     * @param rel
     */
    public static void transform(Relation rel) {
        //判断是否做过转换
        if (!rel.isTransformed()) {
            //判断有没有成员
            if (rel.getMember() != null && rel.getMember().size() > 0) {
                //如果没有叶子列表，就创建叶子列表
                if (rel.getChildren() == null) {
                    List <Relation> children = new ArrayList<>();
                    rel.setChildren(children);
                }
                //将成员作为叶子加入叶子列表中
                List<User> memberList = rel.getMember();
                int size_m = memberList.size();
                User temp;
                Relation relation;
                String Id;
                String parentId;
                String label;
                String userId;
                List<Relation> list = rel.getChildren();
                for (int i = 0; i < size_m; i++) {
                    temp = memberList.get(i);
                    Id = temp.getUserName();
                    parentId = temp.getDeptId();
                    label = temp.getNickName();
                    userId = temp.getUserId();
                    relation = new Relation(Id, parentId, userId, label);
                    relation.setTransformed(true);
                    list.add(relation);
                    Log.d("relation", label + parentId + Id);
                }
                rel.setChildren(list);
            }
            rel.setTransformed(true);
        } else if (Long.parseLong(rel.getId()) < 10000000000L) {
            if (rel.getChildren() == null) {
                List <Relation> children = new ArrayList<>();
                rel.setChildren(children);
            }
        }
        if (rel.getChildren() != null && rel.getChildren().size() > 0) {
            int size_c = rel.getChildren().size();
            for (int i = 0; i < size_c; i++) {
                transform(rel.getChildren().get(i));
            }
        }
    }


}
