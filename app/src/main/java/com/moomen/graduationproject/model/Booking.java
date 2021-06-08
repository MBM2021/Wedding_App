package com.moomen.graduationproject.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Booking implements Serializable {
    @SerializedName("bookingDate")
    String bookingDate;
    @SerializedName("date")
    String date;
    @SerializedName("serviceId")
    String serviceId;
    @SerializedName("userId")
    String userId;
    @SerializedName("delay")
    String delay;
    //The booking status is the service booking is accepted or not
    @SerializedName("status")
    boolean status;

    public Booking() {
    }

    public Booking(String bookingDate, String date, String serviceId, String userId, String delay, boolean status) {
        this.bookingDate = bookingDate;
        this.date = date;
        this.serviceId = serviceId;
        this.userId = userId;
        this.delay = delay;
        this.status = status;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
