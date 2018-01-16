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

    // Wu xing (5 phases) from DiagnosisFragment
    public static final String WUXING_KEY = "wuxing";
    public static final String WUXING_WOOD_TO_EARTH_KEY = "wood_attacks_earth";
    public static final String WUXING_WOOD_TO_METAL_KEY = "wood_attacks_metal";
    public static final String WUXING_FIRE_TO_METAL_KEY = "fire_attacks_metal";
    public static final String WUXING_FIRE_TO_WATER_KEY = "fire_attacks_water";
    public static final String WUXING_EARTH_TO_WATER_KEY = "earth_attacks_water";
    public static final String WUXING_EARTH_TO_WOOD_KEY = "earth_attacks_wood";
    public static final String WUXING_METAL_TO_WOOD_KEY = "metal_attacks_wood";
    public static final String WUXING_METAL_TO_FIRE_KEY = "metal_attacks_fire";
    public static final String WUXING_WATER_TO_FIRE_KEY = "water_attacks_fire";
    public static final String WUXING_WATER_TO_EARTH_KEY = "water_attacks_earth";

    // Questionnaire from 4 steps
    public static final String QUEST_KEY = "questionnaire";
    public static final String QUEST_YIN_YANG_KEY = "yin_yang";

    /************************************************/

    private long timestampCreated;
    private String goal;
    private float sessionRating;
    private Map<String, Boolean> bagang;
    private Map<String, Boolean> wuxing;
    private Map<String, String> questionnaire;

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

    public Map<String, Boolean> getWuxing() {
        return wuxing;
    }

    public void setWuxing(Map<String, Boolean> wuxing) {
        this.wuxing = wuxing;
    }

    public Map<String, String> getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Map<String, String> questionnaire) {
        this.questionnaire = questionnaire;
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

        // [START - PARCEL OUT WUXING MAP]
        if (wuxing != null) {
            Bundle wuxingBundle = new Bundle();
            for (Map.Entry<String, Boolean> entry : wuxing.entrySet()) {
                wuxingBundle.putBoolean(entry.getKey(), entry.getValue());
            }
            out.writeBundle(wuxingBundle);
        }
        // [END - PARCEL OUT WUXING MAP]

        // [START - PARCEL OUT QUESTIONNAIRE MAP]
        if (questionnaire != null) {
            Bundle questionBundle = new Bundle();
            for (Map.Entry<String, String> entry : questionnaire.entrySet()){
                questionBundle.putString(entry.getKey(), entry.getValue());
            }
            out.writeBundle(questionBundle);
        }
        // [END - PARCEL OUT QUESTIONNAIRE MAP]

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
        Bundle bagangBundle = new Bundle();
        bagangBundle = in.readBundle(getClass().getClassLoader());
        Map<String, Boolean> tempBagang = new HashMap<>();
        for (String key: bagangBundle.keySet()){
            tempBagang.put(key, bagangBundle.getBoolean(key));
        }
        bagang = tempBagang;
        // [END - PARCEL IN BAGANG MAP]

        // [START - PARCEL IN WUXING MAP]
        Bundle wuxingBundle = new Bundle();
        wuxingBundle = in.readBundle(getClass().getClassLoader());
        Map<String, Boolean> tempWuxing = new HashMap<>();
        for (String key: wuxingBundle.keySet()){
            tempWuxing.put(key, wuxingBundle.getBoolean(key));
        }
        wuxing = tempWuxing;
        // [END - PARCEL IN WUXING MAP]

        // [START - PARCEL IN QUESTIONNAIRE MAP]
        Bundle questionBundle = new Bundle();
        questionBundle = in.readBundle(getClass().getClassLoader());
        Map<String, String> tempQuestion = new HashMap<>();
        for (String key: questionBundle.keySet()){
            tempQuestion.put(key, questionBundle.getString(key));
        }
        questionnaire = tempQuestion;
        // [END - PARCEL IN QUESTIONNAIRE MAP]
    }
}
