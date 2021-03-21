package com.moomen.graduationproject.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Notification implements Serializable {
    @SerializedName("userImage")
    String userImage;
    @SerializedName("title")
    String title;
    @SerializedName("description")
    String description;
    @SerializedName("shortDescription")
    String shortDescription;
    @SerializedName("date")
    String date;
    @SerializedName("notificationUid")
    String notificationUid;
    @SerializedName("notificationType")
    String notificationType;
    @SerializedName("status")
    boolean status;
    @SerializedName("seen")
    boolean seen;

    public Notification() {
    }

    public Notification(String userImage, String title, String description, String shortDescription
            , String date, String notificationUid, boolean status, boolean seen) {
        this.userImage = userImage;
        this.title = title;
        this.description = description;
        this.shortDescription = shortDescription;
        this.date = date;
        this.notificationUid = notificationUid;
        this.status = status;
        this.seen = seen;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotificationUid() {
        return notificationUid;
    }

    public void setNotificationUid(String notificationUid) {
        this.notificationUid = notificationUid;
    }
}
