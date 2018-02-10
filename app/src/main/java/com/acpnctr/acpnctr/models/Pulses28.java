package com.acpnctr.acpnctr.models;

/**
 * Model POJO for 28 types of pulse.
 * This model is used in FourStepsActivity and PulsesActivity
 */

public class Pulses28 {

    private String type;
    private String translation;
    private String definition;
    private boolean isSelected;

    public Pulses28(String type, String translation, String definition) {
        this.type = type;
        this.translation = translation;
        this.definition = definition;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
