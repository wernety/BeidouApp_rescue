package com.beidouapp.model.DataBase;

import org.litepal.crud.LitePalSupport;

/**
 * 此表是在收到消息或者发到消息时添加，而且必须在chatActivity中，否则不会自动添加
 * （但是建议不这样做，应该在收到消息时就开始记录）
 * 此表用来储存最近的和我发消息的联系人
 * 还可以添加其他字段
 * selfId 查询最近的联系人的时候，需要根据selfID来查询，这样保证同一台设备换用户登录，隐私不会泄露
 */
public class recentMan extends LitePalSupport {
    private String selfId;//自己的ID
    private String uid;//其他人的ID


//    recentMan(String uid){
//        this.uid = uid;
//    }


    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getSelfId() {
        return selfId;
    }
}
