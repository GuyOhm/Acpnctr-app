package com.acpnctr.acpnctr.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Model POJO for a session that the user creates.
 * This firestore Document belongs to the Collection sessions in the clientid Document
 */

public class Session implements Parcelable {

    /************************************************/

    private long timestampCreated;
    private String goal;
    private float sessionRating;
    private ArrayList<String> treatmentList;

    // Empty constructor required by Firebase firestore
    public Session() {
    }

    public Session(long timestampCreated, String goal, int sessionRating) {
        this.timestampCreated = timestampCreated;
        this.goal = goal;
        this.sessionRating = sessionRating;
    }

    public long getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(long timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public float getSessionRating() {
        return sessionRating;
    }

    public void setSessionRating(float sessionRating) {
        this.sessionRating = sessionRating;
    }

    public ArrayList<String> getTreatmentList() {
        return treatmentList;
    }

    public void setTreatmentList(ArrayList<String> treatmentList) {
        this.treatmentList = treatmentList;
    }

    // PARCEL PART
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timestampCreated);
        dest.writeString(this.goal);
        dest.writeFloat(this.sessionRating);
        dest.writeStringList(this.treatmentList);
    }

    protected Session(Parcel in) {
        this.timestampCreated = in.readLong();
        this.goal = in.readString();
        this.sessionRating = in.readFloat();
        this.treatmentList = in.createStringArrayList();
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel source) {
            return new Session(source);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
}
