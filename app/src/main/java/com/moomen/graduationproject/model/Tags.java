package com.moomen.graduationproject.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Tags implements Serializable {
    @SerializedName("name")
    String name;

    public Tags() {
    }

    public Tags(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
