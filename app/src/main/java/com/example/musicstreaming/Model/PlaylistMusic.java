package com.example.musicstreaming.Model;

public class PlaylistMusic {
    public PlaylistMusic(Integer MUSIC_ID, Integer PLAYLIST_ID) {
        this.MUSIC_ID = MUSIC_ID;
        this.PLAYLIST_ID = PLAYLIST_ID;
    }
    public PlaylistMusic() {}
    Integer MUSIC_ID;

    public Integer getMUSIC_ID() {
        return MUSIC_ID;
    }

    public void setMUSIC_ID(Integer MUSIC_ID) {
        this.MUSIC_ID = MUSIC_ID;
    }

    public Integer getPLAYLIST_ID() {
        return PLAYLIST_ID;
    }

    public void setPLAYLIST_ID(Integer PLAYLIST_ID) {
        this.PLAYLIST_ID = PLAYLIST_ID;
    }

    Integer PLAYLIST_ID;

}
