package com.acpnctr.acpnctr.fragments;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.adapters.TreatmentAdapter;
import com.acpnctr.acpnctr.models.Session;
import com.acpnctr.acpnctr.models.Treatment;
import com.acpnctr.acpnctr.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

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

    // Adapter responsible for feeding data to the RecyclerView
    private FirestoreRecyclerAdapter mAdapter;
    // Recycler view variable to display points list
    private RecyclerView mTreatmentList;
    // loading indicator
    private ProgressBar mLoadingIndicator;
    // Stimulation buttons
    private ImageButton mTonificationBtn;
    private ImageButton mNeutralBtn;
    private ImageButton mSedationBtn;
    private TextView mStimulationHint;

    private int mStimulation;
    public static String[] acuPoints;
    public static String[] stimulationOptions;

    private ArrayList<String> mAbbreviationsList = new ArrayList<>();

    // boolean Flag for stimulation buttons
    private boolean isTonificationActivated = true;
    private boolean isNeutralActivated = false;
    private boolean isSedationActivated = false;

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Constants to store state information
    private static final String IS_TONIFICATION_ACTIVATED = "is_tonification";
    private static final String IS_SEDATION_ACTIVATED = "is_sedation";
    private static final String IS_NEUTRAL_ACTIVATED = "is_neutral";
    private static final String ABBREVIATIONS_LIST = "abbreviation_list";

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
        mTonificationBtn = rootView.findViewById(R.id.ib_tonification);
        mNeutralBtn = rootView.findViewById(R.id.ib_neutral);
        mSedationBtn = rootView.findViewById(R.id.ib_sedation);
        mStimulationHint = rootView.findViewById(R.id.tv_stimulation);
        Button addPointButton = rootView.findViewById(R.id.btn_add_acu_point);

        // [START - SET UP AUTO COMPLETION FOR ACUPUNCTURE POINTS]
        // Get a reference to the AutoCompleteTextView in the layout
        acuPointActv = rootView.findViewById(R.id.actv_treatment_point);

        // Get the string arrays
        acuPoints = getResources().getStringArray(R.array.array_acupuncture_points);

        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_list_item_1, acuPoints);
        // Set the adapter to the ACTV
        acuPointActv.setAdapter(arrayAdapter);
        // [END - SET UP AUTO COMPLETION FOR ACUPUNCTURE POINTS]

        // fill stimulation array with its options
        stimulationOptions = getResources().getStringArray(R.array.array_stimulation_options);

        // Set onclick handler for add point to the treatment button
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

        // Set stimulation button for tonification
        mTonificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTonificationActivated) {
                    activateTonificationButton();
                    deactivateSedationButton();
                    deactivateNeutralButton();
                }
            }
        });

        // Set stimulation button for neutral
        mNeutralBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNeutralActivated) {
                    activateNeutralButton();
                    deactivateTonificationButton();
                    deactivateSedationButton();
                }
            }
        });

        // Set stimulation button for sedation
        mSedationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSedationActivated) {
                    activateSedationButton();
                    deactivateTonificationButton();
                    deactivateNeutralButton();
                }
            }
        });

        if (savedInstanceState != null){
            isTonificationActivated = savedInstanceState.getBoolean(IS_TONIFICATION_ACTIVATED);
            isNeutralActivated = savedInstanceState.getBoolean(IS_NEUTRAL_ACTIVATED);
            isSedationActivated= savedInstanceState.getBoolean(IS_SEDATION_ACTIVATED);
            mAbbreviationsList = savedInstanceState.getStringArrayList(ABBREVIATIONS_LIST);
        } else {
            /** Check if this session was selected from the list at {@link SessionsListFragment} */
            Intent intent = getActivity().getIntent();
            if (intent.hasExtra(Constants.INTENT_SELECTED_SESSION_ID)) {
                Session selectedSession = intent.getParcelableExtra(Constants.INTENT_SELECTED_SESSION);
                if (selectedSession.getTreatmentList() != null) {
                    mAbbreviationsList = selectedSession.getTreatmentList();
                }
            }
        }

        initializeStimulationButtons();

        if (!isNewSession){
            mLoadingIndicator.setVisibility(View.VISIBLE);
            displayTreatment();
        }

        return rootView;
    }

    private void initializeStimulationButtons() {
        if (isTonificationActivated){
            activateTonificationButton();
        } else if (isNeutralActivated){
            activateNeutralButton();
        } else if (isSedationActivated){
            activateSedationButton();
        }
    }

    private void activateTonificationButton(){
        ImageViewCompat.setImageTintList(mTonificationBtn, ColorStateList.valueOf(ContextCompat.getColor(
                getActivity(), R.color.colorAccent)));
        isTonificationActivated = true;
        mStimulationHint.setText(stimulationOptions[0]);
        mStimulation = Treatment.TREATMENT_STIMULATION_TONIFICATION;
    }

    private void deactivateTonificationButton(){
        ImageViewCompat.setImageTintList(mTonificationBtn, ColorStateList.valueOf(ContextCompat.getColor(
                getActivity(), R.color.white)));
        isTonificationActivated = false;
    }

    private void activateNeutralButton(){
        ImageViewCompat.setImageTintList(mNeutralBtn, ColorStateList.valueOf(ContextCompat.getColor(
                getActivity(), R.color.colorAccent)));
        isNeutralActivated = true;
        mStimulationHint.setText(stimulationOptions[2]);
        mStimulation = Treatment.TREATMENT_STIMULATION_NEUTRAL;
    }

    private void deactivateNeutralButton(){
        ImageViewCompat.setImageTintList(mNeutralBtn, ColorStateList.valueOf(ContextCompat.getColor(
                getActivity(), R.color.white)));
        isNeutralActivated = false;
    }

    private void activateSedationButton(){
        ImageViewCompat.setImageTintList(mSedationBtn, ColorStateList.valueOf(ContextCompat.getColor(
                getActivity(), R.color.colorAccent)));
        isSedationActivated = true;
        mStimulationHint.setText(stimulationOptions[1]);
        mStimulation = Treatment.TREATMENT_STIMULATION_SEDATION;
    }

    private void deactivateSedationButton(){
        ImageViewCompat.setImageTintList(mSedationBtn, ColorStateList.valueOf(ContextCompat.getColor(
                getActivity(), R.color.white)));
        isSedationActivated = false;
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
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.treatment_list_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mTreatmentList.setLayoutManager(layoutManager);
        mTreatmentList.setHasFixedSize(true);
        mTreatmentList.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    private void addPointDocument() {
        final String point = acuPointActv.getText().toString().trim();
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
                        updateAbbreviationsList(point);
                        acuPointActv.setText("");
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
     * This method update the treatmentList member variable of the Session Document
     * so that we can display a list of point abbreviations in session item list
     *
     * @param point
     */
    // TODO: OPTIMIZATION - move this operation to Cloud Functions
    private void updateAbbreviationsList(String point) {
        mAbbreviationsList.add(abbreviatePoint(point));

        db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid)
                .update(Constants.SESSION_TREATMENT_LIST_KEY, mAbbreviationsList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error updating document", e);
                    }
                });
    }

    /**
     * This method returns an abbreviation of the selected point
     *
     * @param point
     * @return
     */
    @NonNull
    private String abbreviatePoint(String point) {
        return point.substring(0, point.indexOf("-")).trim();
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_TONIFICATION_ACTIVATED, isTonificationActivated);
        outState.putBoolean(IS_NEUTRAL_ACTIVATED, isNeutralActivated);
        outState.putBoolean(IS_SEDATION_ACTIVATED, isSedationActivated);
        outState.putStringArrayList(ABBREVIATIONS_LIST, mAbbreviationsList);
    }
}
