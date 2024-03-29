package com.acpnctr.acpnctr.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private OnTreatmentLongClickedListener mListener;

    public interface OnTreatmentLongClickedListener {
        void onTreatmentLongClicked(int position);
    }

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options recycler options with firestore query
     */
    public TreatmentAdapter(FirestoreRecyclerOptions<Treatment> options, OnTreatmentLongClickedListener listener) {
        super(options);
        this.mListener = listener;
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
        holder.bind(treatment, mListener);
    }


    static class PointHolder extends RecyclerView.ViewHolder{

        private TextView acuPointTextView;
        private ImageView stimulationImageView;
        private ImageView lateralityImageView;

        public PointHolder(View itemView) {
            super(itemView);
            // hook my views in the item view
            acuPointTextView = itemView.findViewById(R.id.tv_treatment_point);
            stimulationImageView = itemView.findViewById(R.id.iv_stimulation_ic);
            lateralityImageView = itemView.findViewById(R.id.iv_laterality_ic);
        }

        public void bind(Treatment treatment, final OnTreatmentLongClickedListener listener) {
            int pointPosition = treatment.getPoint();
            int stimulationPosition = treatment.getStimulation();
            int lateralityPosition = treatment.getLaterality();
            acuPointTextView.setText(TreatmentFragment.acuPoints[pointPosition]);

            switch(stimulationPosition){
                case Treatment.TREATMENT_STIMULATION_TONIFICATION:
                    stimulationImageView.setImageResource(R.drawable.ic_up_arrow_white_36dp);
                    break;
                case Treatment.TREATMENT_STIMULATION_NEUTRAL:
                    stimulationImageView.setImageResource(R.drawable.ic_neutral_white_36dp);
                    break;
                case Treatment.TREATMENT_STIMULATION_SEDATION:
                    stimulationImageView.setImageResource(R.drawable.ic_down_arrow_white_36dp);
                    break;
            }

            switch (lateralityPosition){
                case Treatment.TREATMENT_LATERALITY_LEFT:
                    lateralityImageView.setImageResource(R.drawable.ic_left_arrow_white_36dp);
                    break;
                case Treatment.TREATMENT_LATERALITY_RIGHT:
                    lateralityImageView.setImageResource(R.drawable.ic_right_arrow_white_36dp);
                    break;
            }

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null) {
                        listener.onTreatmentLongClicked(getAdapterPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
