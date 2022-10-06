package com.payment.krishipay.views.member.model;

import com.google.gson.annotations.SerializedName;

public class AppMember {

    @SerializedName("city")
    private String city;

    @SerializedName("name")
    private String name;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("parents")
    private String parents;


    @SerializedName("aepsbalance")
    private String aepsbalance;


    @SerializedName("mainwallet")
    private String mainwallet;


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParents() {
        return parents;
    }

    public void setParents(String parents) {
        this.parents = parents;
    }

    public String getAepsbalance() {
        return aepsbalance;
    }

    public void setAepsbalance(String aepsbalance) {
        this.aepsbalance = aepsbalance;
    }

    public String getMainwallet() {
        return mainwallet;
    }

    public void setMainwallet(String mainwallet) {
        this.mainwallet = mainwallet;
    }
}