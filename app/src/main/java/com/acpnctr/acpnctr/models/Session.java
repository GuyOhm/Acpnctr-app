package com.acpnctr.acpnctr.models;

import android.os.Parcel;
import android.os.Parcelable;

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
    public static final String QUEST_FIVE_PHASES_KEY = "five_phases";
    public static final String QUEST_DIET_KEY = "diet";
    public static final String QUEST_DIGESTION_KEY = "digestion";
    public static final String QUEST_WAY_OF_LIFE_KEY = "way_of_life";
    public static final String QUEST_SLEEP_KEY = "sleep";
    public static final String QUEST_SYMPTOMS_KEY = "symptoms";
    public static final String QUEST_MEDICATION_KEY = "medication";
    public static final String QUEST_EVENTS_KEY = "events";
    public static final String QUEST_EMOTIONAL_KEY = "emotional";
    public static final String QUEST_PSYCHOLOGICAL_KEY = "psychological";
    public static final String QUEST_GYNECOLOGICAL_KEY = "gynecological";

    // Observation from 4 steps
    public static final String OBS_KEY = "observation";
    public static final String OBS_BEHAVIOUR_KEY = "behaviour";
    public static final String OBS_TONGUE_KEY = "tongue";
    public static final String OBS_LIPS_KEY = "lips";
    public static final String OBS_LIMB_KEY = "limb";
    public static final String OBS_MERIDIAN_KEY = "meridian";
    public static final String OBS_MORPHOLOGY_KEY = "morphology";
    public static final String OBS_NAILS_KEY = "nails";
    public static final String OBS_SKIN_KEY = "skin";
    public static final String OBS_HAIRINESS_KEY = "hairiness";
    public static final String OBS_COMPLEXION_KEY = "complexion";
    public static final String OBS_EYES_KEY = "eyes";

    // Auscultation from 4 steps
    public static final String AUSC_KEY = "auscultation";
    public static final String AUSC_BLOOD_PRESSURE_KEY = "blood_pressure";
    public static final String AUSC_ABDOMEN_KEY = "abdomen";
    public static final String AUSC_SMELL_KEY = "smell";
    public static final String AUSC_BREATHING_KEY = "breathing";
    public static final String AUSC_COUGH_KEY = "cough";
    public static final String AUSC_VOICE_KEY = "voice";

    // Palpation from 4 steps
    public static final String PALP_KEY = "palpation";
    public static final String PALP_ABDOMEN_KEY = "abdomen";
    public static final String PALP_MERIDIAN_KEY = "meridian";
    public static final String PALP_POINT_KEY = "point";

    /************************************************/

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
