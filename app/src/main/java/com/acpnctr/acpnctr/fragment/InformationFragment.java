package com.acpnctr.acpnctr.fragment;


import android.content.Intent;
import android.net.Uri;
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
import com.acpnctr.acpnctr.data.ClientContract.ClientEntry;
import com.acpnctr.acpnctr.models.Client;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.acpnctr.acpnctr.data.ClientContract.ClientEntry.GENDER_UNKNOWN;
import static com.acpnctr.acpnctr.models.Client.CLIENT_ACQUI_KEY;
import static com.acpnctr.acpnctr.models.Client.CLIENT_DOB_KEY;
import static com.acpnctr.acpnctr.models.Client.CLIENT_GENDER_KEY;
import static com.acpnctr.acpnctr.models.Client.CLIENT_MAIL_KEY;
import static com.acpnctr.acpnctr.models.Client.CLIENT_NAME_KEY;
import static com.acpnctr.acpnctr.models.Client.CLIENT_PHONE_KEY;

/**
 * {@link Fragment} to display client information.
 */
public class InformationFragment extends Fragment {

    // Log for debugging purposes
    private static final String LOG_TAG = InformationFragment.class.getSimpleName();
    public static final String CLIENTS_COLLECTION = "clients";
    public static final String NEW_CLIENT = "none";

    // Document reference variable for the current client
    private String clientDocRef;

    // Firebase instance variables
    // note: can be written FirebaseFirestore.getInstance().document("clients/client");
    /*
    private DocumentReference clientInfoDocRef = FirebaseFirestore.getInstance()
            .collection("clients")
            .document(clientDocRef);
    */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String documentID = NEW_CLIENT;

    /**
     * EditText field to enter the client's information
     */
    private TextView clientNameEditText;
    private TextView clientDobEditText;
    private TextView clientPhoneEditText;
    private TextView clientEmailEditText;
    private Spinner clientGenderSpinner;
    private Spinner clientAcquisitionSpinner;

    /**
     * Gender and acquisition of the client.
     */
    private int clientGender = GENDER_UNKNOWN;
    private int clientAcquisition = ClientEntry.ACQUI_UNKNOWN;

    /**
     * Content URI for the existing client (null if it's a new client)
     */
    private Uri currentClientUri;

    /**
     * Identifier for the client data loader
     */
    private static final int EXISTING_CLIENT_LOADER = 0;

    /**
     * Boolean flag that keeps track of whether the client has been edited (true) or not (false)
     */
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

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new client or editing an existing one.
        Intent intent = getActivity().getIntent();
        currentClientUri = intent.getData();

/*        // If the intent DOES NOT contain a client content URI, then we know that we are
        // creating a new client.
        if (currentClientUri == null) {
            // This is a new client, so change the app bar to say "Add a client"
            getActivity().setTitle(getString(R.string.add_client_activity_title));

        } else {
            // Initialize a loader to read the client data from the database
            // and display the current values in the editor
            getActivity().getLoaderManager().initLoader(EXISTING_CLIENT_LOADER, null, this);
        }*/

        // Find views that we need to read input from
        clientNameEditText = (TextView) rootView.findViewById(R.id.edit_client_name);
        clientDobEditText = (TextView) rootView.findViewById(R.id.edit_dob_name);
        clientPhoneEditText = (TextView) rootView.findViewById(R.id.edit_client_phone);
        clientEmailEditText = (TextView) rootView.findViewById(R.id.edit_client_email);
        clientGenderSpinner = (Spinner) rootView.findViewById(R.id.spinner_client_gender);
        clientAcquisitionSpinner = (Spinner) rootView.findViewById(R.id.spinner_client_acquisition);

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

        return rootView;
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
                        clientGender = ClientEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        clientGender = ClientEntry.GENDER_FEMALE;
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
                        clientAcquisition = ClientEntry.ACQUI_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clientAcquisition = ClientEntry.ACQUI_UNKNOWN;
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


        // If it's a new client check if name and gender has been edited
        if (currentClientUri == null && TextUtils.isEmpty(name)) {
            Toast.makeText(getActivity(), getString(R.string.client_info_name_needed),
                    Toast.LENGTH_SHORT).show();
            // return early
            return;
        } else if (currentClientUri == null && clientGender == ClientEntry.GENDER_UNKNOWN) {
            Toast.makeText(getActivity(), getString(R.string.client_info_gender_needed),
                    Toast.LENGTH_SHORT).show();
            // return early
            return;
        }

        // if it is a client file creation i.e. documentID = "none"
        if (documentID.equals(NEW_CLIENT)) {
            // get current timestamp
            double timestamp = (double) System.currentTimeMillis()/1000;

            // create a Client object with info fetched from the UI
            Client clientInfo = new Client(name, dob, phone, email, String.valueOf(clientGender),
                    String.valueOf(clientAcquisition), timestamp);

            // create a doc from the Client object to clients collection with auto-generated unique ID
            db.collection(CLIENTS_COLLECTION)
                    .add(clientInfo)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(getActivity(), getString(R.string.client_info_insert_successful),
                                    Toast.LENGTH_SHORT).show();
                            // update documentID
                            documentID = documentReference.getId();
                            // reinit clienHasChanged to false
                            clientHasChanged = false;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(LOG_TAG, "Error adding document", e);
                            Toast.makeText(getActivity(), getString(R.string.client_info_insert_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if(clientHasChanged) {
            // the client has already been created
            // create the path to the document
            DocumentReference clientDocReference = db.collection(CLIENTS_COLLECTION).document(documentID);

            Log.d(LOG_TAG, "documentID: " + documentID);

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
                            Toast.makeText(getActivity(), getString(R.string.client_info_update_successful),
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), getString(R.string.client_info_update_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}
