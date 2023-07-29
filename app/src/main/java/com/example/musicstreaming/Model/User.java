package com.example.musicstreaming.Model;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
public class User {
    @PropertyName("Name")
    String Name;

    public User(String name, String USERNAME, String PASSWORD) {
        Name = name;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
    }
    public User(){}
    @PropertyName("USERNAME")
    String USERNAME;
    @PropertyName("PASSWORD")
    String PASSWORD;
    @PropertyName("Name")
    public String getName() {
        return Name;
    }
    @PropertyName("Name")
    public void setName(String name) {
        Name = name;
    }
    @PropertyName("USERNAME")
    public String getUSERNAME() {
        return USERNAME;
    }
    @PropertyName("USERNAME")
    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }
    @PropertyName("PASSWORD")
    public String getPASSWORD() {
        return PASSWORD;
    }
    @PropertyName("PASSWORD")
    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }
    @PropertyName("USER_ID")
    public Integer getUSER_ID() {
        return USER_ID;
    }
    @PropertyName("USER_ID")
    public void setUSER_ID(Integer USER_ID) {
        this.USER_ID = USER_ID;
    }

    @PropertyName("USER_ID")
    Integer USER_ID;
}
