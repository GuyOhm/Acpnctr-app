package com.acpnctr.acpnctr.models;

import android.support.annotation.Keep;

/**
 * POJO that represents a client's treatment during a session.
 *
 * A treatment is a collection of points.
 */

@Keep
public class Treatment {

    // Must match the order in the corresponding R.array for stimulation
    public static final int TREATMENT_STIMULATION_TONIFICATION = 0;
    public static final int TREATMENT_STIMULATION_SEDATION = 1;
    public static final int TREATMENT_STIMULATION_NEUTRAL = 2;

    // Must match the order in the corresponding R.array for laterality
    public static final int TREATMENT_LATERALITY_BILATERAL = 0;
    public static final int TREATMENT_LATERALITY_LEFT = 1;
    public static final int TREATMENT_LATERALITY_RIGHT = 2;

    // Position in the R.array corresponding to the point
    private int point;
    // Position in the R.array corresponding to the stimulation
    private int stimulation;
    // Position in the R.array corresponding to the laterality option
    private int laterality;

    // Empty constructor required by firebase firestore
    public Treatment() {
    }

    public Treatment(int point, int stimulation, int laterality) {
        this.point = point;
        this.stimulation = stimulation;
        this.laterality = laterality;
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

    public int getLaterality() {
        return laterality;
    }

    public void setLaterality(int laterality) {
        this.laterality = laterality;
    }
}
