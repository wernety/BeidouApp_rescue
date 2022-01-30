package com.beidouapp.model.base;

import java.util.List;

public class BaseModel<E extends BaseModel> {
    /**
     * 是否展开
     */
    private boolean isOpen;
    /**
     * 是否选择
     */
    private boolean isCheck;
    /**
     * 上一级父id
     */
    private String parentId;
    /**
     * 自己的id  13886415060
     */
    private String id;
    /**
     * item名   柴浩翔
     */
    private String label;

    /**
     * 标记第几级
     */
    private int leave;

    private List<E>children;

    public int getLeave() {
        return leave;
    }

    public void setLeave(int leave) {
        this.leave = leave;
    }
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<E> getChildren() {
        return children;
    }

    public void setChildren(List<E> children) {
        this.children = children;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "isOpen=" + isOpen +
                ", isCheck=" + isCheck +
                ", paarentId='" + parentId + '\'' +
                ", id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", leave=" + leave +
                ", children=" + children +
                '}';
    }
}
