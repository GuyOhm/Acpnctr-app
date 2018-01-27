package com.acpnctr.acpnctr.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Adapter to display the four steps of the traditional exams:
 * - Questionnaire
 * - Auscultation
 * - observation
 * - palpation
 */

public class FourStepsAdapter extends ArrayAdapter<String> {


    public FourStepsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
    }
}
