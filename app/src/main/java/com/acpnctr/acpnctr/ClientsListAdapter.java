package com.acpnctr.acpnctr;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acpnctr.acpnctr.models.ClientListItem;

import java.util.List;

/**
 * Custom ArrayAdapter to display a list of clients in the Dashboard screen
 */

public class ClientsListAdapter extends ArrayAdapter<ClientListItem> {

    public ClientsListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ClientListItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.client_list_item, parent, false);
        }

        // Find the views we want to modify in the client list layout
        TextView nameTextView = (TextView) convertView.findViewById(R.id.client_list_name);
        TextView lastSessionTextView = (TextView) convertView.findViewById(R.id.client_list_last_session);

        ClientListItem client = getItem(position);

        // Update the TextViews with the attributes for the current client
        nameTextView.setText(client.getClientName());
        lastSessionTextView.setText(client.getLastSessionDate());

        return convertView;
    }
}
