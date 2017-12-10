package com.acpnctr.acpnctr.models;

/**
 * POJO that represents a client's anamnesis
 */

public class Anamnesis {

    private String date;
    private String history;

    // Empty constructor required by firebase firestore
    public Anamnesis() {
    }

    public Anamnesis(String date, String history) {
        this.date = date;
        this.history = history;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}
