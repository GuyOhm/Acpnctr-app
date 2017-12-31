package com.acpnctr.acpnctr.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Session;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Adapter to feed data from firestore to the RecyclerView via ViewHolder
 * for the list of points displayed in {@link com.acpnctr.acpnctr.fragments.TreatmentFragment}
 */

public class TreatmentAdapter extends FirestoreRecyclerAdapter<Session,
        TreatmentAdapter.PointHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TreatmentAdapter(FirestoreRecyclerOptions<Session> options) {
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
    protected void onBindViewHolder(PointHolder holder, int position, Session session) {
        // bind the Anamnesis object to the AnamnesisHolder
        holder.bind(session);
    }


    static class PointHolder extends RecyclerView.ViewHolder{

        private TextView acuPointTextView;

        public PointHolder(View itemView) {
            super(itemView);

            // hook my views in the item view
            acuPointTextView = itemView.findViewById(R.id.tv_treatment_point);
        }

        public void bind(Session session) {

        }
    }
}
