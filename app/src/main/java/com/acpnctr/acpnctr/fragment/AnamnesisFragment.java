package com.acpnctr.acpnctr.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acpnctr.acpnctr.R;

/**
 * {@link Fragment} to display client anamnesis.
 */
public class AnamnesisFragment extends Fragment implements View.OnClickListener {

    private final static String LOG_TAG = AnamnesisFragment.class.getSimpleName();

    private Button buttonAdd;
    private EditText editTextDate;
    private EditText editTextHistory;
    private LinearLayout linearLayout;

    public AnamnesisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_anamnesis, container, false);

        buttonAdd = rootView.findViewById(R.id.btn_add_history);
        editTextDate = rootView.findViewById(R.id.et_history_date);
        editTextHistory = rootView.findViewById(R.id.et_history_text);
        linearLayout = rootView.findViewById(R.id.ll_history_list);

        buttonAdd.setOnClickListener(this);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            Log.v(LOG_TAG, "this is anamnesis fragment on air! ");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        TextView textView = new TextView(getActivity());
        String textToDisplay = editTextDate.getText().toString() + " - " + editTextHistory.getText().toString();
        textView.setText(textToDisplay);
        linearLayout.addView(textView);
        editTextDate.setText("");
        editTextHistory.setText("");
    }
}
