package com.acpnctr.acpnctr.models;

import android.support.annotation.Keep;

/**
 * Model POJO for a user i.e. a practitioner / acupuncturist
 */

@Keep
public class User {

    private String uid;
    private String fullname;
    private String email;
    private String phone;
    private String address;
    private String zipCode;
    private String city;
    private String country;
    private String authProvider;
    private long timestampCreated;

    // Empty constructor required by Firebase firestore
    public User() {
    }

    public User(String uid, String fullname, String email, String phone, String authProvider, long timestampCreated) {
        this.uid = uid;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.authProvider = authProvider;
        this.timestampCreated = timestampCreated;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public long getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(long timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
