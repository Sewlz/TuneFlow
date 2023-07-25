package com.example.musicstreaming.Model;
public class Music {
    public Music(int CATEGORY_ID, int DURATION, String LYRICS, int MUSIC_ID, String MUSIC_URL, String RELEASE_DATE, String THUMBNAIL_URL, String TITLE) {
        this.CATEGORY_ID = CATEGORY_ID;
        this.DURATION = DURATION;
        this.LYRICS = LYRICS;
        this.MUSIC_ID = MUSIC_ID;
        this.MUSIC_URL = MUSIC_URL;
        this.RELEASE_DATE = RELEASE_DATE;
        this.THUMBNAIL_URL = THUMBNAIL_URL;
        this.TITLE = TITLE;
    }

    public int getCATEGORY_ID() {
        return CATEGORY_ID;
    }

    public void setCATEGORY_ID(int CATEGORY_ID) {
        this.CATEGORY_ID = CATEGORY_ID;
    }

    public int getDURATION() {
        return DURATION;
    }

    public void setDURATION(int DURATION) {
        this.DURATION = DURATION;
    }

    public String getLYRICS() {
        return LYRICS;
    }

    public void setLYRICS(String LYRICS) {
        this.LYRICS = LYRICS;
    }

    public int getMUSIC_ID() {
        return MUSIC_ID;
    }

    public void setMUSIC_ID(int MUSIC_ID) {
        this.MUSIC_ID = MUSIC_ID;
    }

    public String getMUSIC_URL() {
        return MUSIC_URL;
    }

    public void setMUSIC_URL(String MUSIC_URL) {
        this.MUSIC_URL = MUSIC_URL;
    }

    public String getRELEASE_DATE() {
        return RELEASE_DATE;
    }

    public void setRELEASE_DATE(String RELEASE_DATE) {
        this.RELEASE_DATE = RELEASE_DATE;
    }

    public String getTHUMBNAIL_URL() {
        return THUMBNAIL_URL;
    }

    public void setTHUMBNAIL_URL(String THUMBNAIL_URL) {
        this.THUMBNAIL_URL = THUMBNAIL_URL;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    private int CATEGORY_ID;
    private int DURATION;
    private String LYRICS;
    private int MUSIC_ID;
    private String MUSIC_URL;
    private String RELEASE_DATE;
    private String THUMBNAIL_URL;
    private String TITLE;

    public Music() {
        // Required empty constructor for Firebase
    }


    @Override
    public String toString() {
        return "Music{" +
                "categoryId=" + CATEGORY_ID +
                ", duration=" + DURATION +
                ", lyrics='" + LYRICS + '\'' +
                ", musicId=" + MUSIC_ID +
                ", musicUrl='" + MUSIC_URL + '\'' +
                ", releaseDate='" + RELEASE_DATE + '\'' +
                ", thumbnailUrl='" + THUMBNAIL_URL + '\'' +
                ", title='" + TITLE + '\'' +
                '}';
    }
}
