package com.acpnctr.acpnctr;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acpnctr.acpnctr.data.ClientContract.ClientEntry;

import java.text.DateFormat;

/**
 * {@link ClientCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of client data as its data source. This adapter knows
 * how to create list items for each row of client data in the {@link Cursor}.
 */

public class ClientCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ClientCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ClientCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.client_list_item, parent, false);
    }

    /**
     * This method binds the client data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current client can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find the views we want to modify in the client list layout
        // TODO: explore butterknife library or ViewHolder pattern to make this more efficient
        TextView nameTextView = (TextView) view.findViewById(R.id.client_list_name);
        TextView lastSessionTextView = (TextView) view.findViewById(R.id.client_list_last_session);

        // Read the client attribute of the current client
        String clientName = cursor.getString(cursor.getColumnIndex(ClientEntry.COLUMN_CLIENT_NAME));
        // Format Unix time to localized date
        long sessionDate = cursor.getLong(cursor.getColumnIndex(ClientEntry.COLUMN_CLIENT_DATETIME));
        String clientLastSession = DateFormat.getDateInstance().format(sessionDate);

        // Update the TextViews with the attributes for the current client
        nameTextView.setText(clientName);
        lastSessionTextView.setText(clientLastSession);
    }
}
