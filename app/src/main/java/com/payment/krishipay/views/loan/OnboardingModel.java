package com.payment.krishipay.views.loan;

import android.os.Parcel;
import android.os.Parcelable;

public class OnboardingModel implements Parcelable {
    private String Name;
    private String Address;
    private String City;
    private String State;
    private String Phone;
    private String Email;
    private String ShopName;
    private String Pan;
    private String PinCode;
    private String Aadhaar;
    private String picAadhaar;
    private String pinPan;
    private String loanPurpose;
    private String loanAmount;
    private String loanDuration;

    public OnboardingModel() {
    }

    protected OnboardingModel(Parcel in) {
        Name = in.readString();
        Address = in.readString();
        City = in.readString();
        State = in.readString();
        Phone = in.readString();
        Email = in.readString();
        ShopName = in.readString();
        Pan = in.readString();
        PinCode = in.readString();
        Aadhaar = in.readString();
        picAadhaar = in.readString();
        pinPan = in.readString();
        loanPurpose = in.readString();
        loanAmount = in.readString();
        loanDuration = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Address);
        dest.writeString(City);
        dest.writeString(State);
        dest.writeString(Phone);
        dest.writeString(Email);
        dest.writeString(ShopName);
        dest.writeString(Pan);
        dest.writeString(PinCode);
        dest.writeString(Aadhaar);
        dest.writeString(picAadhaar);
        dest.writeString(pinPan);
        dest.writeString(loanPurpose);
        dest.writeString(loanAmount);
        dest.writeString(loanDuration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OnboardingModel> CREATOR = new Creator<OnboardingModel>() {
        @Override
        public OnboardingModel createFromParcel(Parcel in) {
            return new OnboardingModel(in);
        }

        @Override
        public OnboardingModel[] newArray(int size) {
            return new OnboardingModel[size];
        }
    };

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getPan() {
        return Pan;
    }

    public void setPan(String pan) {
        Pan = pan;
    }

    public String getPinCode() {
        return PinCode;
    }

    public void setPinCode(String pinCode) {
        PinCode = pinCode;
    }

    public String getAadhaar() {
        return Aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        Aadhaar = aadhaar;
    }

    public String getPicAadhaar() {
        return picAadhaar;
    }

    public void setPicAadhaar(String picAadhaar) {
        this.picAadhaar = picAadhaar;
    }

    public String getPinPan() {
        return pinPan;
    }

    public void setPinPan(String pinPan) {
        this.pinPan = pinPan;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanDuration() {
        return loanDuration;
    }

    public void setLoanDuration(String loanDuration) {
        this.loanDuration = loanDuration;
    }
}
