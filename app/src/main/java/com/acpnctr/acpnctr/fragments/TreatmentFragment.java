package com.acpnctr.acpnctr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.adapters.TreatmentAdapter;
import com.acpnctr.acpnctr.models.Treatment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

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

    // Adapter responsible for feeding data to the RecyclerView
    private FirestoreRecyclerAdapter mAdapter;
    // Recycler view variable to display points list
    private RecyclerView mTreatmentList;
    // loading indicator
    private ProgressBar mLoadingIndicator;

    private int mStimulation;
    public static String[] acuPoints;
    public static String[] stimulationOptions;

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TreatmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_treatment, container, false);

        // Get references to our Layout views
        mTreatmentList = rootView.findViewById(R.id.rv_treatment_points_list);
        mLoadingIndicator = rootView.findViewById(R.id.pb_treatment_loading_indicator);

        // [START - SET UP AUTO COMPLETION FOR ACUPUNCTURE POINTS]
        // Get a reference to the AutoCompleteTextView in the layout
        acuPointActv = rootView.findViewById(R.id.actv_treatment_point);

        // Get the string arrays
        acuPoints = getResources().getStringArray(R.array.array_acupuncture_points);
        stimulationOptions = getResources().getStringArray(R.array.array_stimulation_options);

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
                if (!isNewSession) {
                    addPointDocument();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.session_not_saved), Toast.LENGTH_SHORT)
                            .show();
                }
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

        // Query firestore for the list of points in treatment collection
        Query query = treatmentColl.orderBy("point");

        // Configure recycler adapter options
        FirestoreRecyclerOptions<Treatment> options = new FirestoreRecyclerOptions.Builder<Treatment>()
                .setQuery(query, Treatment.class)
                .build();

        mAdapter = new TreatmentAdapter(options) {

            @Override
            public void onDataChanged() {
                // hide the loading indicator
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.d(LOG_TAG, "Error while loading data for treatment" + e);
                Toast.makeText(getActivity(), getString(R.string.treatment_list_error),
                        Toast.LENGTH_SHORT).show();
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mTreatmentList.setLayoutManager(layoutManager);
        mTreatmentList.setHasFixedSize(true);
        mTreatmentList.setAdapter(mAdapter);
        mAdapter.startListening();
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

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) mAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) mAdapter.stopListening();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_save);
        menuItem.setVisible(false);
    }
}
