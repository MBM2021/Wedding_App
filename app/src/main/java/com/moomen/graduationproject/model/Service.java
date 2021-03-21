package com.moomen.graduationproject.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Service implements Serializable {
    @SerializedName("hallImage")
    String hallImage;
    @SerializedName("city")
    String city;
    @SerializedName("hallName")
    String hallName;
    @SerializedName("ownerName")
    String ownerName;
    @SerializedName("phone")
    String phone;
    @SerializedName("location")
    String location;
    @SerializedName("detail")
    String detail;
    @SerializedName("status")
    boolean status;
    @SerializedName("tagsArrayList")
    ArrayList<Tags> tagsArrayList;
    @SerializedName("notificationType")
    String notificationType;


    public Service() {
    }

    public Service(String hallImage, String city, String hallName, String ownerName, String phone
            , String location, String detail, boolean status, ArrayList<Tags> tagsArrayList, String notificationType) {
        this.hallImage = hallImage;
        this.city = city;
        this.hallName = hallName;
        this.ownerName = ownerName;
        this.phone = phone;
        this.location = location;
        this.detail = detail;
        this.status = status;
        this.tagsArrayList = tagsArrayList;
        this.notificationType = notificationType;
    }

    public String getNotification() {
        return notificationType;
    }

    public void setNotification(String notificationType) {
        this.notificationType = notificationType;
    }

    public ArrayList<Tags> getTagsArrayList() {
        return tagsArrayList;
    }

    public void setTagsArrayList(ArrayList<Tags> tagsArrayList) {
        this.tagsArrayList = tagsArrayList;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getHallImage() {
        return hallImage;
    }

    public void setHallImage(String hallImage) {
        this.hallImage = hallImage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
