package com.beidouapp.model;

import com.beidouapp.model.base.BaseModel;

import java.util.List;

public class Relation extends BaseModel<Relation> {
    private List<User> member;
    private boolean isTransformed;
    private boolean isOnline;

    public Relation(String Id, String parentId, String label) {
        this.setId(Id);
        this.setParentId(parentId);
        this.setLabel(label);
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
}
