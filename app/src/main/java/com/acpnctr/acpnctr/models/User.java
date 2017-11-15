package com.acpnctr.acpnctr.models;

/**
 * Model POJO for a user i.e. a practitioner / acupuncturist
 */

public class User {

    private String uid;
    private String fullname;
    private String email;
    private String authProvider;
    private long timestampCreated;

    // Empty constructor required by Firebase firestore
    public User() {
    }

    public User(String uid, String fullname, String email, String authProvider, long timestampCreated) {
        this.uid = uid;
        this.fullname = fullname;
        this.email = email;
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
}
