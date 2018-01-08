package com.acpnctr.acpnctr.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Model POJO for a session that the user creates.
 * This firestore Document belongs to the Collection sessions in the clientid Document
 */

public class Session implements Parcelable {

    /**********************************************
     * MODEL RELATED CONSTANTS TO BE USED AS KEYS *
     **********************************************/
    // Session data from FourStepsFragment
    public static final String SESSION_RATING_KEY = "sessionRating";
    public static final String SESSION_TIMESTAMP = "timestampCreated";
    public static final String SESSION_GOAL = "goal";

    // Bagang from DiagnosisFragment
    public static final String BAGANG_KEY = "bagang";
    public static final String BAGANG_YIN_KEY = "yin";
    public static final String BAGANG_YANG_KEY = "yang";
    public static final String BAGANG_DEFICIENCY_KEY = "deficiency";
    public static final String BAGANG_EXCESS_KEY = "excess";
    public static final String BAGANG_COLD_KEY = "cold";
    public static final String BAGANG_HEAT_KEY = "heat";
    public static final String BAGANG_INTERIOR_KEY = "interior";
    public static final String BAGANG_EXTERIOR_KEY = "exterior";


    private long timestampCreated;
    private String goal;
    private float sessionRating;
    private Map<String, Boolean> bagang;

    // Empty constructor required by Firebase firestore
    public Session() {
    }

    public Session(long timestampCreated, String goal, int sessionRating) {
        this.timestampCreated = timestampCreated;
        this.goal = goal;
        this.sessionRating = sessionRating;
    }

    public Session(Map<String, Boolean> bagang) {
        this.bagang = bagang;
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

    public Map<String, Boolean> getBagang() {
        return bagang;
    }

    public void setBagang(Map<String, Boolean> bagang) {
        this.bagang = bagang;
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

        // [START - PARCEL OUT BAGANG MAP]
        if (bagang != null) {
            Bundle bagangBundle = new Bundle();
            for (Map.Entry<String, Boolean> entry : bagang.entrySet()) {
                bagangBundle.putBoolean(entry.getKey(), entry.getValue());
            }
            out.writeBundle(bagangBundle);
        }
        // [END - PARCEL OUT BAGANG MAP]
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

        // [START - PARCEL IN BAGANG MAP]
        Bundle bundle = new Bundle();
        bundle = in.readBundle(getClass().getClassLoader());
        Map<String, Boolean> tempBagang = new HashMap<>();
        for (String key: bundle.keySet()){
            tempBagang.put(key, bundle.getBoolean(key));
        }
        bagang = tempBagang;
        // [START - PARCEL IN BAGANG MAP]
    }
}
