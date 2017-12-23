package com.acpnctr.acpnctr.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model POJO for a Client.
 */

public class Client implements Parcelable{

    // key constants (not sure if I'll be using them
    public static final String CLIENT_NAME_KEY = "clientName";
    public static final String CLIENT_DOB_KEY = "clientDOB";
    public static final String CLIENT_PHONE_KEY = "clientPhone";
    public static final String CLIENT_MAIL_KEY = "clientEmail";
    public static final String CLIENT_GENDER_KEY = "clientGender";
    public static final String CLIENT_ACQUI_KEY = "clientAcquisition";
    public static final String CLIENT_TIMESTAMP_KEY = "timestampCreated";

    // member variables
    private String clientName;
    private String clientDOB;
    private String clientPhone;
    private String clientEmail;
    private String clientGender;
    private String clientAcquisition;
    // This var is not passed into the constructor as a param => to be handled during implementation
    private long timestampCreated;

    // Empty constructor required by firebase firestore
    public Client() {}

    public Client(String clientName, String clientDOB, String clientPhone, String clientEmail,
                  String clientGender, String clientAcquisition, long timestampCreated) {
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

    public long getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(long timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    // Parcel part
    // as per: https://developer.android.com/reference/android/os/Parcelable.html
    @Override
    public int describeContents() {
        return 0;
    }

    // Write the object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(clientName);
        out.writeString(clientDOB);
        out.writeString(clientPhone);
        out.writeString(clientEmail);
        out.writeString(clientGender);
        out.writeString(clientAcquisition);
        out.writeLong(timestampCreated);
    }

    // This is used to regenerate your object.
    // All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Client> CREATOR = new Parcelable.Creator<Client>() {
        public Client createFromParcel(Parcel in){
            return new Client(in);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

    // Example constructor that takes a Parcel and gives you an object populated with it's values
    private Client(Parcel in) {
        clientName = in.readString();
        clientDOB = in.readString();
        clientPhone = in.readString();
        clientEmail = in.readString();
        clientGender = in.readString();
        clientAcquisition = in.readString();
        timestampCreated = in.readLong();
    }
}
