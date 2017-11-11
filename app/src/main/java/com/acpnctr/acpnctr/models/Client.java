package com.acpnctr.acpnctr.models;

/**
 * Model POJO for a Client.
 */

public class Client {

    // key constants (not sure if I'll be using them
    public static final String CLIENT_NAME_KEY = "clientName";
    public static final String CLIENT_DOB_KEY = "clientDOB";
    public static final String CLIENT_PHONE_KEY = "clientPhone";
    public static final String CLIENT_MAIL_KEY = "clientEmail";
    public static final String CLIENT_GENDER_KEY = "clientGender";
    public static final String CLIENT_ACQUI_KEY = "clientAcqui";
    public static final String CLIENT_TIMESTAMP_KEY = "timestampCreated";

    private String clientName;
    private String clientDOB;
    private String clientPhone;
    private String clientEmail;
    private String clientGender;
    private String clientAcquisition;
    // This var is not passed into the constructor as a param => to be handled during implementation
    private double timestampCreated;

    // Empty constructor required by firebase firestore
    public Client() {}

    public Client(String clientName, String clientDOB, String clientPhone, String clientEmail,
                  String clientGender, String clientAcquisition, double timestampCreated) {
        this.clientName = clientName;
        this.clientDOB = clientDOB;
        this.clientPhone = clientPhone;
        this.clientEmail = clientEmail;
        this.clientGender = clientGender;
        this.clientAcquisition = clientAcquisition;
        this.timestampCreated = timestampCreated;
    }

    // constructor without timestamp for updates
    public Client(String clientName, String clientDOB, String clientPhone, String clientEmail,
                  String clientGender, String clientAcquisition) {
        this.clientName = clientName;
        this.clientDOB = clientDOB;
        this.clientPhone = clientPhone;
        this.clientEmail = clientEmail;
        this.clientGender = clientGender;
        this.clientAcquisition = clientAcquisition;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientDOB() {
        return clientDOB;
    }

    public void setClientDOB(String clientDOB) {
        this.clientDOB = clientDOB;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientGender() {
        return clientGender;
    }

    public void setClientGender(String clientGender) {
        this.clientGender = clientGender;
    }

    public String getClientAcquisition() {
        return clientAcquisition;
    }

    public void setClientAcquisition(String clientAcquisition) {
        this.clientAcquisition = clientAcquisition;
    }

    public double getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(double timestampCreated) {
        this.timestampCreated = timestampCreated;
    }
}
