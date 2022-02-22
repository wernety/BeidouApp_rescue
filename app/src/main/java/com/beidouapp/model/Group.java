package com.beidouapp.model;

public class Group {
    private String selfGroupId;
    private String selfGroupName;
    private String adminId;
    private String selfGroupOwner;
    private String selfCreateTime;
    private String selfGroupIcon;

    public void setSelfGroupId(String selfGroupId) {
        this.selfGroupId = selfGroupId;
    }
    public String getSelfGroupId() {
        return selfGroupId;
    }

    public void setSelfGroupName(String selfGroupName) {
        this.selfGroupName = selfGroupName;
    }
    public String getSelfGroupName() {
        return selfGroupName;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
    public String getAdminId() {
        return adminId;
    }

    public void setSelfGroupOwner(String selfGroupOwner) {
        this.selfGroupOwner = selfGroupOwner;
    }
    public String getSelfGroupOwner() {
        return selfGroupOwner;
    }

    public void setSelfCreateTime(String selfCreateTime) {
        this.selfCreateTime = selfCreateTime;
    }
    public String getSelfCreateTime() {
        return selfCreateTime;
    }

    public void setSelfGroupIcon(String selfGroupIcon) {
        this.selfGroupIcon = selfGroupIcon;
    }
    public String getSelfGroupIcon() {
        return selfGroupIcon;
    }

    public static class GroupUsers {
        private String groupId;
        private String userId;
        private String userName;
        private String addTime;

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
        public String getGroupId() {
            return groupId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
        public String getUserId() {
            return userId;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
        public String getUserName() {
            return userName;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }
        public String getAddTime() {
            return addTime;
        }
    }
}

