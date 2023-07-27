package com.example.musicstreaming.Model;

public class User {
    String Name;

    public User(String name, String USERNAME, String PASSWORD, Integer USER_ID) {
        Name = name;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        this.USER_ID = USER_ID;
    }
    public User(){}
    String USERNAME;
    String PASSWORD;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public Integer getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(Integer USER_ID) {
        this.USER_ID = USER_ID;
    }

    Integer USER_ID;
}
