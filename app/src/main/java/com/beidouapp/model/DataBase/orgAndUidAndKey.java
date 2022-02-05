package com.beidouapp.model.DataBase;

import org.litepal.crud.LitePalSupport;

public class orgAndUidAndKey extends LitePalSupport {
    private String org;
    private String uid;
    private String pass;
    private String curToken;
//    private String

    public String getUid() {
        return uid;
    }

    public String getOrg() {
        return org;
    }

    public String getPass() {
        return pass;
    }

    public String getCurToken() {
        return curToken;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setCurToken(String curToken) {
        this.curToken = curToken;
    }
}
