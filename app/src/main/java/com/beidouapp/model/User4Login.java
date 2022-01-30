package com.beidouapp.model;

public class User4Login {

    private String username;
    private String password;


    public User4Login(String name, String psw) {
        username = name;
        password = psw;
    }



    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

}
