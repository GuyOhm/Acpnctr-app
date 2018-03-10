package com.acpnctr.acpnctr.models;

import android.support.annotation.Keep;

/**
 * Model POJO for 28 types of pulse.
 * This model is used in FourStepsActivity and PulsesActivity
 */
@Keep
public class PulseType {

    private String key;
    private int name;
    private int translation;
    private int definition;
    private boolean isSelected = false;

    public PulseType(String key, int name, int translation) {
        this.key = key;
        this.name = name;
        this.translation = translation;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getTranslation() {
        return translation;
    }

    public void setTranslation(int translation) {
        this.translation = translation;
    }

    public int getDefinition() {
        return definition;
    }

    public void setDefinition(int definition) {
        this.definition = definition;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void toggleSelected(){
        isSelected = !isSelected;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
