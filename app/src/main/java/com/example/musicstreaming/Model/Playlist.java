package com.example.musicstreaming.Model;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
public class Playlist {
    public Playlist(String USERNAME, String PLAYLIST_NAME, Integer PLAYLIST_ID) {
        this.USERNAME = USERNAME;
        this.PLAYLIST_NAME = PLAYLIST_NAME;
        this.PLAYLIST_ID = PLAYLIST_ID;
    }

    public Playlist(){}
    @PropertyName("USERNAME")
    public String getUSERNAME() {
        return USERNAME;
    }
    @PropertyName("USERNAME")
    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }
    @PropertyName("PLAYLIST_NAME")
    public String getPLAYLIST_NAME() {
        return PLAYLIST_NAME;
    }
    @PropertyName("PLAYLIST_NAME")
    public void setPLAYLIST_NAME(String PLAYLIST_NAME) {
        this.PLAYLIST_NAME = PLAYLIST_NAME;
    }
    @PropertyName("PLAYLIST_ID")
    public Integer getPLAYLIST_ID() {
        return PLAYLIST_ID;
    }
    @PropertyName("PLAYLIST_ID")
    public void setPLAYLIST_ID(Integer PLAYLIST_ID) {
        this.PLAYLIST_ID = PLAYLIST_ID;
    }
    @PropertyName("USERNAME")
    String USERNAME;
    @PropertyName("PLAYLIST_NAME")
    String PLAYLIST_NAME;
    @PropertyName("PLAYLIST_ID")
    Integer PLAYLIST_ID;

}
