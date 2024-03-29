package com.acpnctr.acpnctr.fragments;


import android.content.DialogInterface;
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
import com.acpnctr.acpnctr.utils.AcpnctrUtil;
import com.acpnctr.acpnctr.utils.Constants;
import com.acpnctr.acpnctr.utils.FirebaseUtil;
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
public class TreatmentFragment extends Fragment implements TreatmentAdapter.OnTreatmentLongClickedListener {

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
    // Laterality buttons
    private ImageButton mLeftBtn;
    private ImageButton mRightBtn;
    private TextView mLateralityHint;

    private int mStimulation;
    private int mLaterality;
    public static String[] acuPoints;
    private static String[] stimulationOptions;
    private static String[] lateralityOptions;

    private ArrayList<String> mAbbreviationsList = new ArrayList<>();

    // boolean Flag for stimulation buttons
    private boolean isTonificationActivated = true;
    private boolean isNeutralActivated = false;
    private boolean isSedationActivated = false;

    // boolean Flag for laterality buttons
    private boolean isBilateralityActivated = true;
    private boolean isLeftActivated = false;
    private boolean isRightActivated = false;

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Constants to store state information
    private static final String IS_TONIFICATION_ACTIVATED = "is_tonification";
    private static final String IS_SEDATION_ACTIVATED = "is_sedation";
    private static final String IS_NEUTRAL_ACTIVATED = "is_neutral";
    private static final String ABBREVIATIONS_LIST = "abbreviation_list";
    private static final String IS_BILATERALITY_ACTIVATED = "is_bilaterality";
    private static final String IS_LEFT_ACTIVATED = "is_left";
    private static final String IS_RIGHT_ACTIVATED = "is_right";

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
        mLeftBtn = rootView.findViewById(R.id.ib_left);
        mRightBtn = rootView.findViewById(R.id.ib_right);
        mLateralityHint = rootView.findViewById(R.id.tv_laterality);

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

        // fill laterality array with its options
        lateralityOptions = getResources().getStringArray(R.array.array_laterality_options);

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

        // Set laterality button for left
        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLeftActivated) {
                    activateLeftButton();
                    deactivateRightButton();
                    deactivateBilaterality();
                } else {
                    deactivateLeftButton();
                    activateBilaterality();
                }
            }
        });

        // Set laterality button for right
        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRightActivated) {
                    activateRightButton();
                    deactivateLeftButton();
                    deactivateBilaterality();
                } else {
                    deactivateRightButton();
                    activateBilaterality();
                }
            }
        });

        if (savedInstanceState != null){
            isTonificationActivated = savedInstanceState.getBoolean(IS_TONIFICATION_ACTIVATED);
            isNeutralActivated = savedInstanceState.getBoolean(IS_NEUTRAL_ACTIVATED);
            isSedationActivated = savedInstanceState.getBoolean(IS_SEDATION_ACTIVATED);
            mAbbreviationsList = savedInstanceState.getStringArrayList(ABBREVIATIONS_LIST);
            isBilateralityActivated = savedInstanceState.getBoolean(IS_BILATERALITY_ACTIVATED);
            isLeftActivated = savedInstanceState.getBoolean(IS_LEFT_ACTIVATED);
            isRightActivated = savedInstanceState.getBoolean(IS_RIGHT_ACTIVATED);
        } else {
            // Check if this session was selected from the list at {@link SessionsListFragment}
            Intent intent = getActivity().getIntent();
            if (intent.hasExtra(Constants.INTENT_SELECTED_SESSION_ID)) {
                Session selectedSession = intent.getParcelableExtra(Constants.INTENT_SELECTED_SESSION);
                if (selectedSession.getTreatmentList() != null) {
                    mAbbreviationsList = selectedSession.getTreatmentList();
                }
            }
        }

        initializeStimulationButtons();
        initializeLateralityButtons();

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

    private void initializeLateralityButtons() {
        if (isBilateralityActivated) {
            activateBilaterality();
        } else if (isLeftActivated) {
            activateLeftButton();
        } else if (isRightActivated) {
            activateRightButton();
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

    private void activateLeftButton() {
        ImageViewCompat.setImageTintList(mLeftBtn, ColorStateList.valueOf(ContextCompat.getColor(
                getActivity(), R.color.colorAccent)));
        isLeftActivated = true;
        mLateralityHint.setText(lateralityOptions[1]);
        mLaterality = Treatment.TREATMENT_LATERALITY_LEFT;
    }

    private void deactivateLeftButton() {
        ImageViewCompat.setImageTintList(mLeftBtn, ColorStateList.valueOf(ContextCompat.getColor(
                getActivity(), R.color.white)));
        isLeftActivated = false;
    }

    private void activateRightButton() {
        ImageViewCompat.setImageTintList(mRightBtn, ColorStateList.valueOf(ContextCompat.getColor(
                getActivity(), R.color.colorAccent)));
        isRightActivated = true;
        mLateralityHint.setText(lateralityOptions[2]);
        mLaterality = Treatment.TREATMENT_LATERALITY_RIGHT;
    }

    private void deactivateRightButton() {
        ImageViewCompat.setImageTintList(mRightBtn, ColorStateList.valueOf(ContextCompat.getColor(
                getActivity(), R.color.white)));
        isRightActivated = false;
    }

    private void activateBilaterality() {
        isBilateralityActivated = true;
        mLateralityHint.setText(lateralityOptions[0]);
        mLaterality = Treatment.TREATMENT_LATERALITY_BILATERAL;
    }

    private void deactivateBilaterality() {
        isBilateralityActivated = false;
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

        mAdapter = new TreatmentAdapter(options, this) {

            @Override
            public void onDataChanged() {
                // hide the loading indicator
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
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

        Treatment treatment = new Treatment(position, mStimulation, mLaterality);

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
                        mAbbreviationsList.add(abbreviatePoint(point));
                        updateAbbreviationsList();
                        acuPointActv.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), R.string.generic_data_insert_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * This method update the treatmentList member variable of the Session Document
     * so that we can display a list of point abbreviations in session item list
     */
    private void updateAbbreviationsList() {

        db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid)
                .update(Constants.SESSION_TREATMENT_LIST_KEY, mAbbreviationsList);
    }

    /**
     * This method returns an abbreviation of the selected point
     *
     * @param point acupuncture point
     * @return String with the acupuncture point abbreviation
     */
    @NonNull
    private String abbreviatePoint(String point) {
        return point.substring(0, point.indexOf("-")).trim();
    }

    /**
     * This method returns the position of a given point in the array.
     *
     * @param point acupuncture point
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
        outState.putBoolean(IS_BILATERALITY_ACTIVATED, isBilateralityActivated);
        outState.putBoolean(IS_LEFT_ACTIVATED, isLeftActivated);
        outState.putBoolean(IS_RIGHT_ACTIVATED, isRightActivated);
    }

    @Override
    public void onTreatmentLongClicked(final int position) {
        final String treatmentId = FirebaseUtil.getIdFromSnapshot(mAdapter, position);

        // Get the Treatment object at that position hence the point that might be deleted
        Treatment treatment = (Treatment) mAdapter.getItem(position);
        final String pointToDelete = acuPoints[treatment.getPoint()];

        // Create a dialog button click listener
        DialogInterface.OnClickListener deleteButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection(FIRESTORE_COLLECTION_USERS)
                                .document(sUid)
                                .collection(FIRESTORE_COLLECTION_CLIENTS)
                                .document(sClientid)
                                .collection(FIRESTORE_COLLECTION_SESSIONS)
                                .document(sSessionid)
                                .collection(FIRESTORE_COLLECTION_TREATMENT)
                                .document(treatmentId)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (mAdapter != null) {
                                            mAdapter.notifyDataSetChanged();
                                            // Get the abbreviation of the deleted point
                                            String abbreviation = abbreviatePoint(pointToDelete);
                                            // Find index of the point abbreviation
                                            int index = mAbbreviationsList.indexOf(abbreviation);
                                            // and delete it from the list
                                            mAbbreviationsList.remove(index);
                                            // Update modified abbreviation list
                                            updateAbbreviationsList();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if (isAdded() && getActivity() != null) {
                                            Toast.makeText(getActivity(), R.string.delete_data_failed, Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }
                                });
                    }
                };

        // Show a dialog to confirm the user wants to delete selected data
        AcpnctrUtil.showDeleteDataDialog(getActivity(), deleteButtonClickListener);
    }
}
