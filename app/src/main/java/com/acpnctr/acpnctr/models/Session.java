package com.acpnctr.acpnctr.models;

/**
 * Model POJO for a session that the user creates.
 * This firestore Document belongs to the Collection sessions in the clientid Document
 */

public class Session {

    private long timestampCreated;
    private  String goal;
    private  int sessionRating;

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

    public int getSessionRating() {
        return sessionRating;
    }

    public void setSessionRating(int sessionRating) {
        this.sessionRating = sessionRating;
    }
}
