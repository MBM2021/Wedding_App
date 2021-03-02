package com.moomen.graduationproject.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    @Keep
    @SerializedName("name")
    String name;
    @SerializedName("email")
    String email;
    @SerializedName("dateOfBirth")
    String dateOfBirth;
    @SerializedName("address")
    String address;
    @SerializedName("phone")
    String phone;
    @SerializedName("gender")
    String gender;
    @SerializedName("userType")
    String userType;
    @SerializedName("dateCreate")
    String dateCreate;
    @SerializedName("userImage")
    String userImage;
    @SerializedName("status")
    boolean status;
    @SerializedName("aboutCompany")
    String aboutCompany;

    public User() {
    }

    public User(String name, String email, String dateOfBirth, String address, String phone, String gender
            , String userType, String dateCreate, String userImage, boolean status, String aboutCompany) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.gender = gender;
        this.userType = userType;
        this.dateCreate = dateCreate;
        this.userImage = userImage;
        this.status = status;
        this.aboutCompany = aboutCompany;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAboutCompany() {
        return aboutCompany;
    }

    public void setAboutCompany(String aboutCompany) {
        this.aboutCompany = aboutCompany;
    }
}
