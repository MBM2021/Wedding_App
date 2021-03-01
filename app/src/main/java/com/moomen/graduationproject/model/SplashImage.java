package com.moomen.graduationproject.model;

public class SplashImage {
    private String title;
    private String disc;
    private int image;

    public SplashImage(String title, String disc, int image) {
        this.title = title;
        this.disc = disc;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
