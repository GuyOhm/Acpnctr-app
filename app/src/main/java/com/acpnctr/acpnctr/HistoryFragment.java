package com.acpnctr.acpnctr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A {@link Fragment} to display the sessions history of a client.
 */
public class HistoryFragment extends Fragment {

    // TODO: implement loader manager to load sessions list in background thread
    // TODO: create a cursor adapter to feed data from the cursor to the listview
    // TODO: create a content provider to request data from DB to the cursor

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TextView textView = new TextView(getActivity());
        textView.setText("list of sessions");

        return textView;
    }

}
