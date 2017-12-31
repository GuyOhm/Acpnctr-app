package com.acpnctr.acpnctr.models;

/**
 * POJO that represents a client's treatment during a session.
 *
 * A treatment is a collection of points.
 */

public class Treatment {

    // Must match the order in the corresponding R.array
    public static final int TREATMENT_STIMULATION_TONIFICATION = 0;
    public static final int TREATMENT_STIMULATION_SEDATION = 1;
    public static final int TREATMENT_STIMULATION_NEUTRAL = 2;

    // Position in the R.array corresponding to the point
    private int point;
    // Position in the R.array corresponding to the stimulation
    private int stimulation;

    // Empty constructor required by firebase firestore
    public Treatment() {
    }

    public Treatment(int point, int stimulation) {
        this.point = point;
        this.stimulation = stimulation;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getStimulation() {
        return stimulation;
    }

    public void setStimulation(int stimulation) {
        this.stimulation = stimulation;
    }
}
