package com.acpnctr.acpnctr.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Client;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.acpnctr.acpnctr.ClientActivity.isNewClient;
import static com.acpnctr.acpnctr.ClientActivity.sClientid;
import static com.acpnctr.acpnctr.ClientActivity.sUid;
import static com.acpnctr.acpnctr.models.Client.CLIENT_ACQUI_KEY;
import static com.acpnctr.acpnctr.models.Client.CLIENT_DOB_KEY;
import static com.acpnctr.acpnctr.models.Client.CLIENT_GENDER_KEY;
import static com.acpnctr.acpnctr.models.Client.CLIENT_MAIL_KEY;
import static com.acpnctr.acpnctr.models.Client.CLIENT_NAME_KEY;
import static com.acpnctr.acpnctr.models.Client.CLIENT_PHONE_KEY;
import static com.acpnctr.acpnctr.utils.Constants.ACQUI_CONFRERE;
import static com.acpnctr.acpnctr.utils.Constants.ACQUI_FACEBOOK;
import static com.acpnctr.acpnctr.utils.Constants.ACQUI_OFFLINE;
import static com.acpnctr.acpnctr.utils.Constants.ACQUI_UNKNOWN;
import static com.acpnctr.acpnctr.utils.Constants.ACQUI_WEBSITE;
import static com.acpnctr.acpnctr.utils.Constants.ACQUI_WOM;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;
import static com.acpnctr.acpnctr.utils.Constants.GENDER_FEMALE;
import static com.acpnctr.acpnctr.utils.Constants.GENDER_MALE;
import static com.acpnctr.acpnctr.utils.Constants.GENDER_UNKNOWN;
import static com.acpnctr.acpnctr.utils.Constants.INTENT_SELECTED_CLIENT;

/**
 * {@link Fragment} to display client information.
 */
public class InformationFragment extends Fragment {

    // Log for debugging purposes
    private static final String LOG_TAG = InformationFragment.class.getSimpleName();

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // EditText field to enter the client's information
    private TextView clientNameEditText;
    private TextView clientDobEditText;
    private TextView clientPhoneEditText;
    private TextView clientEmailEditText;
    private Spinner clientGenderSpinner;
    private Spinner clientAcquisitionSpinner;

    // Gender and acquisition of the client.
    private String clientGender = GENDER_UNKNOWN;
    private String clientAcquisition = ACQUI_UNKNOWN;

    // Boolean flag that keeps track of whether the client has been edited (true) or not (false)
    public static boolean clientHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the clientHasChanged boolean to true.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            clientHasChanged = true;
            return false;
        }
    };

    public InformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        clientHasChanged = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);

        // Find views that we need to read input from
        clientNameEditText = rootView.findViewById(R.id.edit_client_name);
        clientDobEditText = rootView.findViewById(R.id.edit_dob_name);
        clientPhoneEditText = rootView.findViewById(R.id.edit_client_phone);
        clientEmailEditText = rootView.findViewById(R.id.edit_client_email);
        clientGenderSpinner = rootView.findViewById(R.id.spinner_client_gender);
        clientAcquisitionSpinner = rootView.findViewById(R.id.spinner_client_acquisition);

        // Set up spinners
        setupGenderSpinner();
        setupAcquisitionSpinner();

        // Setup OnTouchListeners on all the input fields.
        clientNameEditText.setOnTouchListener(touchListener);
        clientDobEditText.setOnTouchListener(touchListener);
        clientPhoneEditText.setOnTouchListener(touchListener);
        clientEmailEditText.setOnTouchListener(touchListener);
        clientGenderSpinner.setOnTouchListener(touchListener);
        clientAcquisitionSpinner.setOnTouchListener(touchListener);

        // it is an existing client who has been selected from the list recover the Client object
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(INTENT_SELECTED_CLIENT)){
            Client selectedClient = intent.getParcelableExtra(INTENT_SELECTED_CLIENT);
            onClientSelectedInitialize(selectedClient);
        }

        return rootView;
    }

    private void onClientSelectedInitialize(Client selectedClient) {
        clientNameEditText.setText(selectedClient.getClientName());
        clientDobEditText.setText(selectedClient.getClientDOB());
        clientPhoneEditText.setText(selectedClient.getClientPhone());
        clientEmailEditText.setText(selectedClient.getClientEmail());
        clientGender = selectedClient.getClientGender();
        clientAcquisition = selectedClient.getClientAcquisition();
        // see the position in the arrays.xml
        clientGenderSpinner.setSelection(getPositionInArray(R.array.array_gender_options, clientGender));
        clientAcquisitionSpinner.setSelection(getPositionInArray(R.array.array_acquisition_options, clientAcquisition));
    }

    /**
     * This method find the value in an array and returns its position. Used to set the right
     * selection for the spinners.
     *
     * @param resourceID array containing the possible values
     * @param value we are looking for in the array
     * @return position of the value in the resource array
     */
    private int getPositionInArray(int resourceID, String value) {
        String[] options = getResources().getStringArray(resourceID);
        // Capitalize value to match with constants
        String capValue = value.substring(0, 1).toUpperCase() + value.substring(1);
        for (int i = 0; i < options.length; i++){
            if(capValue.equals(options[i])){
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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
                        clientGender = GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        clientGender = GENDER_FEMALE;
                    } else {
                        clientGender = GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clientGender = GENDER_UNKNOWN;
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
                        clientAcquisition = ACQUI_OFFLINE;
                    } else if (selection.equals(getString(R.string.acquisition_wom))) {
                        clientAcquisition = ACQUI_WOM;
                    } else if (selection.equals(getString(R.string.acquisition_website))) {
                        clientAcquisition = ACQUI_WEBSITE;
                    } else if (selection.equals(getString(R.string.acquisition_facebook))) {
                        clientAcquisition = ACQUI_FACEBOOK;
                    } else if (selection.equals(getString(R.string.acquisition_confrere))) {
                        clientAcquisition = ACQUI_CONFRERE;
                    } else {
                        clientAcquisition = ACQUI_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clientAcquisition = ACQUI_UNKNOWN;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // Save client to database
                saveClient();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveClient() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String name = clientNameEditText.getText().toString().trim();
        String dob = clientDobEditText.getText().toString().trim();
        String phone = clientPhoneEditText.getText().toString().trim();
        String email = clientEmailEditText.getText().toString().trim();


        // Check if name and gender has been edited
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getActivity(), getString(R.string.client_info_name_needed), Toast.LENGTH_SHORT).show();
            // return early
            return;
        } else if (clientGender.equals(GENDER_UNKNOWN)) {
            Toast.makeText(getActivity(), getString(R.string.client_info_gender_needed), Toast.LENGTH_SHORT).show();
            // return early
            return;
        }

        // if it is a client file creation
        if (isNewClient) {
            // get current timestamp
            long timestamp = System.currentTimeMillis();

            // create a Client object with info fetched from the UI
            Client client = new Client(name, dob, phone, email, clientGender, clientAcquisition, timestamp);
            createNewClientDocument(client);

        // isNewClient == false
        } else if(clientHasChanged) {
            // the client has already been created
            updateClientDocument(name, dob, phone, email);
        }
    }

    private void updateClientDocument(String name, String dob, String phone, String email) {
        // create the path to the document
        DocumentReference clientDocReference = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid);

        Log.d(LOG_TAG, "sClientid: " + sClientid);

        // create a hashmap with values fetched from the UI that may have been updated
        Map<String, Object> updates = new HashMap<>();
        updates.put(CLIENT_GENDER_KEY, clientGender);
        updates.put(CLIENT_MAIL_KEY, email);
        updates.put(CLIENT_DOB_KEY, dob);
        updates.put(CLIENT_NAME_KEY, name);
        updates.put(CLIENT_PHONE_KEY, phone);
        updates.put(CLIENT_ACQUI_KEY, clientAcquisition);

        // udpate the corresponding document in the firestore DB
        clientDocReference
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), getString(R.string.client_info_update_successful), Toast.LENGTH_SHORT).show();
                        // reinit clienHasChanged to false
                        clientHasChanged = false;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "error updating client: " + e);
                        Toast.makeText(getActivity(), getString(R.string.client_info_update_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createNewClientDocument(Client client) {
        // create a doc from the Client object to clients collection with auto-generated unique ID
        db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .add(client)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getActivity(), getString(R.string.client_info_insert_successful), Toast.LENGTH_SHORT).show();
                        // update sClientid
                        sClientid = documentReference.getId();
                        isNewClient = false;
                        // reinit clienHasChanged to false
                        clientHasChanged = false;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error adding document", e);
                        Toast.makeText(getActivity(), getString(R.string.client_info_insert_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
