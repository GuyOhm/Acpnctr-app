package com.acpnctr.acpnctr.models;

import android.support.annotation.Keep;

/**
 * POJO that represents a client's anamnesis
 */

@Keep
public class Anamnesis {

    private long timestamp;
    private String history;

    // Empty constructor required by firebase firestore
    public Anamnesis() {
    }

    public Anamnesis(long timestamp, String history) {
        this.timestamp = timestamp;
        this.history = history;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}
