package com.beidouapp.model.messages;

public class ChatMessage {
    private String name;
    private String content;
    private String time;
    private String type;
    private int isMeSend;//0是对方发送 1是自己发送
    //private int isRead;//是否已读（0未读 1已读）

    public ChatMessage(){
        super();
    }

    public ChatMessage(String name, String content, String time, String type,int isMeSend){
        this.content = content;
        this.isMeSend = isMeSend;
        this.name = name;
        this.time = time;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIsMeSend() {
        return isMeSend;
    }

    public void setIsMeSend(int isMeSend) {
        this.isMeSend = isMeSend;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    //    public int getIsRead() {
//        return isRead;
//    }

//    public void setIsRead(int isRead) {
//        this.isRead = isRead;
//    }
}
