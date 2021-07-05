package com.moomen.graduationproject.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable {
    @Keep
    @SerializedName("senderID")
    private String senderID;
    @SerializedName("receiverID")
    private String receiverID;
    @SerializedName("senderName")
    private String senderName;
    @SerializedName("senderEmail")
    private String senderEmail;
    @SerializedName("senderImage")
    private String senderImage;
    @SerializedName("date")
    private String date;
    @SerializedName("serviceId")
    private String serviceId;
    @SerializedName("isSupport")
    private boolean isSupport;
    @SerializedName("messageArrayList")
    private ArrayList<Message> messageArrayList;

    public Chat() {
    }

    public Chat(String senderID, String receiverID, String senderName, String senderEmail, String senderImage
            , String date, String serviceId, boolean isSupport, ArrayList<Message> messageArrayList) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.senderImage = senderImage;
        this.date = date;
        this.serviceId = serviceId;
        this.isSupport = isSupport;
        this.messageArrayList = messageArrayList;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Message> getMessageArrayList() {
        return messageArrayList;
    }

    public void setMessageArrayList(ArrayList<Message> messageArrayList) {
        this.messageArrayList = messageArrayList;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public boolean isSupport() {
        return isSupport;
    }

    public void setSupport(boolean support) {
        isSupport = support;
    }
}
