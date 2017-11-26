package com.acpnctr.acpnctr;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.acpnctr.acpnctr.models.Client;

/**
 * ViewHolder to cache the children view for a clients list item
 */

class ClientHolder extends RecyclerView.ViewHolder{

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

        listItemClientName = itemView.findViewById(R.id.tv_client_list_name);
        listItemTimestampCreated = itemView.findViewById(R.id.tv_client_list_last_session);
    }

    public void bind(Client client){
        listItemClientName.setText(client.getClientName());
        listItemTimestampCreated.setText(String.valueOf(client.getTimestampCreated()));
    }
}
