package com.beidouapp.model.messages;

import java.util.Date;


/**
 * 聊天消息接受
 * 用于JSON
 */

public class Message4Receive {
    private String type;
    private String msg;
    private data data;
    private String receiveType;

//    public Message4Receive() {
//        type = "";
//        msg = "";
//        data = new data();
//    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setData(data data) {
        this.data = data;
    }
    public data getData() {
        return data;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }
    public String getReceiveType() {
        return receiveType;
    }

    public static class data {
        private String msgType;
        private String sendText;
        private String sendUserId;
        private String receiveUserId;
        private String groupId;


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

        public void setSendUserId(String sendUserId) {
            this.sendUserId = sendUserId;
        }
        public String getSendUserId() {
            return sendUserId;
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
    }
}
