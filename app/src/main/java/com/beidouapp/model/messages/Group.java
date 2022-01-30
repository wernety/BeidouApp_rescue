package com.beidouapp.model.messages;

public class Group {
    private String id;
    private String name;
    private String adminId;
    private String createUser;
    private String createTime;

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
    public String getAdminId() {
        return adminId;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public String getCreateTime() {
        return createTime;
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

