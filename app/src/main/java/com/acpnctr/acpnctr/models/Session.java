package com.acpnctr.acpnctr.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model POJO for a session that the user creates.
 * This firestore Document belongs to the Collection sessions in the clientid Document
 */

public class Session implements Parcelable {

    // Model related constants to be used as key
    public static final String SESSION_RATING_KEY = "sessionRating";
    public static final String SESSION_TIMESTAMP = "timestampCreated";
    public static final String SESSION_GOAL = "goal";

    private long timestampCreated;
    private String goal;
    private float sessionRating;

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

    // Parcel part
    // as per: https://developer.android.com/reference/android/os/Parcelable.html

    @Override
    public int describeContents() {
        return 0;
    }

    // Write the object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(timestampCreated);
        out.writeString(goal);
        out.writeFloat(sessionRating);
    }

    // This is used to regenerate your object.
    // All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Session> CREATOR = new Parcelable.Creator<Session>() {
        public Session createFromParcel(Parcel in){
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    // Constructor that takes a Parcel and gives you an object populated with it's values
    private Session(Parcel in) {
        timestampCreated = in.readLong();
        goal = in.readString();
        sessionRating = in.readFloat();
    }

}
