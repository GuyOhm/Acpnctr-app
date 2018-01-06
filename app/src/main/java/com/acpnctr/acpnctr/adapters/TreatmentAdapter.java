package com.acpnctr.acpnctr.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.fragments.TreatmentFragment;
import com.acpnctr.acpnctr.models.Treatment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Adapter to feed data from firestore to the RecyclerView via ViewHolder
 * for the list of points displayed in {@link com.acpnctr.acpnctr.fragments.TreatmentFragment}
 */

public class TreatmentAdapter extends FirestoreRecyclerAdapter<Treatment,
        TreatmentAdapter.PointHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TreatmentAdapter(FirestoreRecyclerOptions<Treatment> options) {
        super(options);
    }

    @Override
    public PointHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new instance of the ViewHolder using our custom layout for each item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.treatment_list_item, viewGroup, false);

        return new PointHolder(view);
    }

    @Override
    protected void onBindViewHolder(PointHolder holder, int position, Treatment treatment) {
        // bind the Anamnesis object to the AnamnesisHolder
        holder.bind(treatment);
    }


    static class PointHolder extends RecyclerView.ViewHolder{

        private TextView acuPointTextView;
        private TextView stimulationTextView;

        public PointHolder(View itemView) {
            super(itemView);
            // hook my views in the item view
            acuPointTextView = itemView.findViewById(R.id.tv_treatment_point);
            stimulationTextView = itemView.findViewById(R.id.tv_treatment_stimulation);
        }

        public void bind(Treatment treatment) {
            int pointPosition = treatment.getPoint();
            int stimulationPosition = treatment.getStimulation();
            acuPointTextView.setText(TreatmentFragment.acuPoints[pointPosition]);
            stimulationTextView.setText(TreatmentFragment.stimulationOptions[stimulationPosition]);
        }
    }
}
