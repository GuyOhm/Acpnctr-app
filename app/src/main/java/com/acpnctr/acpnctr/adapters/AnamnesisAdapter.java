package com.acpnctr.acpnctr.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Anamnesis;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Adapter to feed data from firestore to the RecyclerView via ViewHolder
 * for the list of histories in {@link com.acpnctr.acpnctr.fragments.AnamnesisFragment}
 */

public class AnamnesisAdapter extends FirestoreRecyclerAdapter<Anamnesis,
        AnamnesisAdapter.AnamnesisHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AnamnesisAdapter(FirestoreRecyclerOptions<Anamnesis> options) {
        super(options);
    }

    @Override
    public AnamnesisHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new instance of the ViewHolder using our custom layout for each item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.anamnesis_list_item, viewGroup, false);

        return new AnamnesisHolder(view);
    }

    @Override
    protected void onBindViewHolder(AnamnesisHolder holder, int position, Anamnesis model) {
        // bind the Anamnesis object to the AnamnesisHolder
        holder.bind(model);
    }


    static class AnamnesisHolder extends RecyclerView.ViewHolder{

        private TextView anamnesisDateTextView;
        private TextView anamnesisHistoryTextView;

        public AnamnesisHolder(View itemView) {
            super(itemView);

            // hook my views in the item view
            anamnesisDateTextView = itemView.findViewById(R.id.tv_anamnesis_list_date);
            anamnesisHistoryTextView = itemView.findViewById(R.id.tv_anamnesis_list_history);
        }

        public void bind(Anamnesis anamnesis) {
            if (TextUtils.isEmpty(anamnesis.getDate())) {
                anamnesisDateTextView.setText(R.string.anam_empty_date);
            } else {
                anamnesisDateTextView.setText(anamnesis.getDate());
            }
            anamnesisHistoryTextView.setText(anamnesis.getHistory());
        }
    }
}
