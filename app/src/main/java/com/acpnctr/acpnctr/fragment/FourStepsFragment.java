package com.acpnctr.acpnctr.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link Fragment} to display the 4 steps of a session.
 */
public class FourStepsFragment extends Fragment {


    public FourStepsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText("Four steps of the traditional diagnosis");
        return textView;
    }

}
