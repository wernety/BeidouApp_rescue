package com.beidouapp.model.messages;

import java.util.List;


/**
 * 聊天消息发送
 * 用于JSON
 */
public class Message4Send {

    private String groupId;
    private String receiveUserId;
    private String sendType;
    private String msgType;
    private String sendText;


    public Message4Send(String Id, String sendType, String msgType, String sendText){
        if (sendType.equals("single")) {
            this.receiveUserId = Id;
            this.sendType = sendType;
            this.msgType = msgType;
            this.sendText = sendText;
        } else {
            this.groupId = Id;
            this.sendType = sendType;
            this.msgType = msgType;
            this.sendText = sendText;
        }

    }


    public void setReceiveUserId(String receiveUserId) {
        this.receiveUserId = receiveUserId;
    }
    public String getReceiveUserId() {
        return receiveUserId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getGroupId() {
        return groupId;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }
    public String getSendType() {
        return sendType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
    public String getMsgType() {
        return msgType;
    }

    public void setSendText(String sendText) {
        this.sendText = sendText;
    }
    public String getSendText() {
        return sendText;
    }

    @Override
    public String toString() {
        return "Message4Send{" +
                "groupId='" + groupId + '\'' +
                ", receiveUserId='" + receiveUserId + '\'' +
                ", sendType='" + sendType + '\'' +
                ", msgType='" + msgType + '\'' +
                ", sendText='" + sendText + '\'' +
                '}';
    }
}


