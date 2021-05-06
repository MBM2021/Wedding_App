package com.moomen.graduationproject.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Favourite implements Serializable {
    @Keep
    @SerializedName("serviceId")
    private String serviceId;

    public Favourite() {

    }

    public Favourite(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
