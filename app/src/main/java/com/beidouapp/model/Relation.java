package com.beidouapp.model;

import com.beidouapp.model.base.BaseModel;

import java.util.List;

public class Relation extends BaseModel<Relation> {
    private List<User> member;
    private boolean isTransformed;
    private boolean isOnline;
    private String userId;

    public Relation(String Id, String parentId, String userId, String label) {
        this.setId(Id);
        this.setParentId(parentId);
        this.setLabel(label);
        this.setUserId(userId);
    }

    public Relation() {
        isTransformed = false;
    }

    public void setMember(List<User> member) {
        this.member = member;
    }
    public List<User> getMember() {
        return member;
    }

    public void setTransformed(boolean transformed) {
        isTransformed = transformed;
    }
    public boolean isTransformed() {
        return isTransformed;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
