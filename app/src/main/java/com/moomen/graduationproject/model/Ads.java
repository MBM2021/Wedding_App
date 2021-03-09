package com.moomen.graduationproject.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Ads implements Serializable {
    @SerializedName("image")
    String image;

    public Ads() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Ads(String image) {
        this.image = image;
    }
}
