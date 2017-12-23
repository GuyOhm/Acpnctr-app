package com.acpnctr.acpnctr.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acpnctr.acpnctr.DashboardActivity;
import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Client;
import com.acpnctr.acpnctr.utils.DateFormatUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Adapter to feed data from firestore to the RecyclerView via ViewHolder
 * for the clients list in {@link DashboardActivity}
 */

public class ClientAdapter extends FirestoreRecyclerAdapter<Client, ClientAdapter.ClientHolder> {

    // interface to manage how client are selected
    public interface OnClientSelectedListener {
        void onClientSelected(Client client, int position);
    }

    private OnClientSelectedListener mListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     * @param listener
     */
    public ClientAdapter(FirestoreRecyclerOptions<Client> options, OnClientSelectedListener listener) {
        super(options);
        mListener = listener;
    }

    @Override
    public ClientHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new instance of the ViewHolder using our custom layout for each item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.client_list_item, viewGroup, false);

        return new ClientHolder(view);
    }

    @Override
    protected void onBindViewHolder(ClientHolder holder, int position, Client model) {
        // bind the Client object to the ClientHolder
        holder.bind(model, mListener);
    }

    static class ClientHolder extends RecyclerView.ViewHolder {

        // display the client's name
        private TextView listItemClientName;

        // display the timestampCreated for the client file
        // TODO: replace this by the last session data
        private TextView listItemTimestampCreated;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews.
         * @param itemView The View that we inflate in onCreateViewHolder(ViewGroup, int) when creating
         *                 the FirestoreRecyclerAdapter in {@link DashboardActivity}
         */
        public ClientHolder(View itemView) {
            super(itemView);

            // hook my views in the item view
            listItemClientName = itemView.findViewById(R.id.tv_client_list_name);
            listItemTimestampCreated = itemView.findViewById(R.id.tv_client_list_last_session);
        }

        public void bind(final Client client, final OnClientSelectedListener listener){
            listItemClientName.setText(client.getClientName());
            listItemTimestampCreated.setText(DateFormatUtil.convertTimestampToString(client.getTimestampCreated()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onClientSelected(client, position);
                    }
                }
            });
        }
    }
}
