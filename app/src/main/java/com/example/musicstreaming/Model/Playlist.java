package com.example.musicstreaming.Model;

public class Playlist {
    public Playlist(String USERNAME, String PLAYLIST_NAME, Integer PLAYLIST_ID) {
        this.USERNAME = USERNAME;
        this.PLAYLIST_NAME = PLAYLIST_NAME;
        this.PLAYLIST_ID = PLAYLIST_ID;
    }
    public Playlist(){}
    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getPLAYLIST_NAME() {
        return PLAYLIST_NAME;
    }

    public void setPLAYLIST_NAME(String PLAYLIST_NAME) {
        this.PLAYLIST_NAME = PLAYLIST_NAME;
    }

    public Integer getPLAYLIST_ID() {
        return PLAYLIST_ID;
    }

    public void setPLAYLIST_ID(Integer PLAYLIST_ID) {
        this.PLAYLIST_ID = PLAYLIST_ID;
    }

    String USERNAME, PLAYLIST_NAME;
    Integer PLAYLIST_ID;

}
