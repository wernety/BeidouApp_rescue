package com.beidouapp.model.messages;

public class Friend {
    private String userId;
    private String friendId;
    private String friendName;
    private String addTime;
    private boolean isChecked;

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
    public String getFriendId() {
        return friendId;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
    public String getFriendName() {
        return friendName;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
    public String getAddTime() {
        return addTime;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    public boolean isChecked() {
        return isChecked;
    }
}
