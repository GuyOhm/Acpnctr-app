package com.acpnctr.acpnctr.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link Fragment} to display treatment given to a client during a specific session.
 */
public class TreatmentFragment extends Fragment {


    public TreatmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText("Treatment of this session");
        return textView;
    }

}
