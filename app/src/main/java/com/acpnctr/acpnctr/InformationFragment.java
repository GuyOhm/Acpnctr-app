package com.acpnctr.acpnctr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.acpnctr.acpnctr.data.ClientContract.ClientEntry;

/**
 * {@link Fragment} to display client information.
 */
public class InformationFragment extends Fragment {

    /**
     * EditText field to enter the client's gender and acquisition
     */
    private Spinner clientGenderSpinner;
    private Spinner clientAcquisitionSpinner;

    /**
     * Gender and acquisition of the client.
     */
    private int clientGender = ClientEntry.GENDER_UNKNOWN;
    private int clientAcquisition = ClientEntry.ACQUI_UNKNOW;

    public InformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);

        // Find views that we need to read input from
        clientGenderSpinner = (Spinner) rootView.findViewById(R.id.spinner_client_gender);
        clientAcquisitionSpinner = (Spinner) rootView.findViewById(R.id.spinner_client_acquisition);

        setupGenderSpinner();

        setupAcquisitionSpinner();

        return rootView;
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the client.
     */
    private void setupGenderSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        clientGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        clientGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        clientGender = ClientEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        clientGender = ClientEntry.GENDER_FEMALE;
                    } else {
                        clientGender = ClientEntry.GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clientGender = ClientEntry.GENDER_UNKNOWN;
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the acquisition channel of the client.
     */
    private void setupAcquisitionSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter acquiSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_acquisition_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        acquiSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        clientAcquisitionSpinner.setAdapter(acquiSpinnerAdapter);

        // Set the integer mSelected to the constant values
        clientAcquisitionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.acquisition_offline))) {
                        clientAcquisition = ClientEntry.ACQUI_OFFLINE;
                    } else if (selection.equals(getString(R.string.acquisition_wom))) {
                        clientAcquisition = ClientEntry.ACQUI_WOM;
                    } else if (selection.equals(getString(R.string.acquisition_website))) {
                        clientAcquisition = ClientEntry.ACQUI_WEBSITE;
                    } else if (selection.equals(getString(R.string.acquisition_facebook))) {
                        clientAcquisition = ClientEntry.ACQUI_FACEBOOK;
                    } else if (selection.equals(getString(R.string.acquisition_confrere))) {
                        clientAcquisition = ClientEntry.ACQUI_CONFRERE;
                    } else {
                        clientAcquisition = ClientEntry.ACQUI_UNKNOW;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clientAcquisition = ClientEntry.ACQUI_UNKNOW;
            }
        });
    }

}
