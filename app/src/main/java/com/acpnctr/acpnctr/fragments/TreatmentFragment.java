package com.acpnctr.acpnctr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Treatment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.acpnctr.acpnctr.SessionActivity.isNewSession;
import static com.acpnctr.acpnctr.SessionActivity.sClientid;
import static com.acpnctr.acpnctr.SessionActivity.sSessionid;
import static com.acpnctr.acpnctr.SessionActivity.sUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_SESSIONS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_TREATMENT;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

/**
 * {@link Fragment} to display treatment given to a client during a specific session.
 */
public class TreatmentFragment extends Fragment {

    private static final String LOG_TAG = TreatmentFragment.class.getSimpleName();

    private AutoCompleteTextView acuPointActv;
    private Spinner pointStimulationSpinner;
    private Button addPointButton;

    private int mStimulation;
    private String[] acuPoints;

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TreatmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_treatment, container, false);

        // [START - SET UP AUTO COMPLETION FOR ACUPUNCTURE POINTS]
        // Get a reference to the AutoCompleteTextView in the layout
        acuPointActv = rootView.findViewById(R.id.actv_treatment_point);

        // Get the string array
        acuPoints = getResources().getStringArray(R.array.array_acupuncture_points);

        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_list_item_1, acuPoints);
        // Set the adapter to the ACTV
        acuPointActv.setAdapter(arrayAdapter);
        // [END - SET UP AUTO COMPLETION FOR ACUPUNCTURE POINTS]

        pointStimulationSpinner = rootView.findViewById(R.id.spinner_treatment_stimulation);
        setupStimulationSpinner();

        addPointButton = rootView.findViewById(R.id.btn_add_acu_point);

        addPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPointDocument();
            }
        });

        if (!isNewSession){
            displayTreatment();
        }

        return rootView;
    }

    /**
     * Setup the dropdown spinner that allows the user to select how the point
     * is stimulated.
     */
    private void setupStimulationSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter stimulationSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_stimulation_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        stimulationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        pointStimulationSpinner.setAdapter(stimulationSpinnerAdapter);

        pointStimulationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selection = (String) adapterView.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals(getString(R.string.stimulation_tonification))){
                        mStimulation = Treatment.TREATMENT_STIMULATION_TONIFICATION;
                    } else if (selection.equals(getString(R.string.stimulation_sedation))){
                        mStimulation = Treatment.TREATMENT_STIMULATION_SEDATION;
                    } else if (selection.equals(getString(R.string.stimulation_neutral))){
                        mStimulation = Treatment.TREATMENT_STIMULATION_NEUTRAL;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mStimulation = Treatment.TREATMENT_STIMULATION_NEUTRAL;

            }
        });
    }

    private void displayTreatment() {
        // Build path to treatment collection
        CollectionReference treatmentColl = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid)
                .collection(FIRESTORE_COLLECTION_TREATMENT);

        // WILL BE USED FOR OTHER DATA
        /*treatmentDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null){
                    Log.w(LOG_TAG, "Listen failed" , e);
                }

                if (documentSnapshot != null && documentSnapshot.exists()){
                    Session session = documentSnapshot.toObject(Session.class);
                    Map<String, Boolean> treatment = session.getTreatment();
                    if (treatment != null) {
                        for (String point : treatment.keySet()) {
                            Log.d(LOG_TAG, "=========== key : " + point + " ===========");
                        }
                    }
                } else {
                    Log.d(LOG_TAG, "Current data: null");
                }
            }
        });*/
        // WILL BE USED FOR OTHER DATA


    }

    private void addPointDocument() {
        String point = acuPointActv.getText().toString().trim();
        int position = getPositionFromArray(point);
        if (position < 0) {
            Toast.makeText(getActivity(), getString(R.string.treatment_invalid_point), Toast.LENGTH_SHORT).show();
            return;
        }

        Treatment treatment = new Treatment(position, mStimulation);

        db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid)
                .collection(FIRESTORE_COLLECTION_TREATMENT)
                .add(treatment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        acuPointActv.setText("");
                        pointStimulationSpinner.clearFocus();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error adding document", e);
                    }
                });
    }

    /**
     * This method returns the position of a given point in the array.
     *
     * @param point
     * @return point's position and -1 if the point doesn't exist in the array
     */
    private int getPositionFromArray(String point) {
        for (int i = 0; i < acuPoints.length; i++){
            if (acuPoints[i].equals(point)){
                return i;
            }
        }
        return -1;
    }
}
