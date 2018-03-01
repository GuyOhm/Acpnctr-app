package com.acpnctr.acpnctr.fragments;


import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acpnctr.acpnctr.AuscultationActivity;
import com.acpnctr.acpnctr.ObservationActivity;
import com.acpnctr.acpnctr.PalpationActivity;
import com.acpnctr.acpnctr.PulsesActivity;
import com.acpnctr.acpnctr.QuestionnaireActivity;
import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.adapters.FourStepsAdapter;
import com.acpnctr.acpnctr.models.Session;
import com.acpnctr.acpnctr.utils.Constants;
import com.acpnctr.acpnctr.utils.DateFormatUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.acpnctr.acpnctr.SessionActivity.isNewSession;
import static com.acpnctr.acpnctr.SessionActivity.sClientid;
import static com.acpnctr.acpnctr.SessionActivity.sSessionid;
import static com.acpnctr.acpnctr.SessionActivity.sUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_SESSIONS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

/**
 * {@link Fragment} to display the 4 steps of a session.
 */
public class FourStepsFragment extends Fragment {

    private static final String LOG_TAG = FourStepsFragment.class.getSimpleName();

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Class members
    private EditText goalEditText;
    private EditText dateEditText;
    // for primary session data
    private String mSessionDate;
    private String mSessionGoal;
    // for questionnaire data
    private ArrayList<String> mQuestionArray = new ArrayList<>();
    private ArrayAdapter<String> mQuestionAdapter;
    private Map<String, String> mQuestionMap;
    // for observation data
    private ArrayList<String> mObservationArray = new ArrayList<>();
    private ArrayAdapter<String> mObservationAdapter;
    private Map<String, String> mObservationMap;
    // for auscultation data
    private ArrayList<String> mAuscultationArray = new ArrayList<>();
    private ArrayAdapter<String> mAuscultationAdapter;
    private Map<String, String> mAuscultationMap;
    // for palpation data
    private ArrayList<String> mPalpationArray = new ArrayList<>();
    private ArrayAdapter<String> mPalpationAdapter;
    private Map<String, String> mPalpationMap;
    // for pulses data
    private TextView mEurythmyTextView;
    private Map<String, Object> mPulsesMap;
    private TextView m28PulseTypesTextView;

    private static boolean sessionDataHasChanged = false;

    private View.OnTouchListener sessionTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            sessionDataHasChanged = true;
            return false;
        }
    };

    // Final strings to store state information
    private static final String SESSION_DATE = "session_date";
    private static final String SESSION_GOAL = "session_goal";
    private static final String SESSION_HAS_CHANGED = "session_has_changed";
    private static final String QUESTION_ARRAY = "question_array";
    private static final String OBSERVATION_ARRAY = "observation_array";
    private static final String AUSCULTATION_ARRAY = "auscultation_array";
    private static final String PALPATION_ARRAY = "palpation_array";

    // Request code for result from activity
    public static final int QUESTION_REQUEST = 2;
    public static final int OBSERVATION_REQUEST = 3;
    public static final int AUSCULTATION_REQUEST = 4;
    public static final int PALPATION_REQUEST = 5;
    public static final int PULSES_REQUEST = 6;

    public FourStepsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_four_steps, container, false);

        // hook up the views
        goalEditText = rootView.findViewById(R.id.et_session_goal);
        dateEditText = rootView.findViewById(R.id.et_session_date);
        Button addQuestion = rootView.findViewById(R.id.btn_add_question);
        ListView questionContainer = rootView.findViewById(R.id.lv_questionnaire_container);
        Button addObservation = rootView.findViewById(R.id.btn_add_observation);
        ListView observationContainer = rootView.findViewById(R.id.lv_observation_container);
        Button addAuscultation = rootView.findViewById(R.id.btn_add_auscultation);
        ListView auscultationContainer = rootView.findViewById(R.id.lv_auscultation_container);
        Button addPalpation = rootView.findViewById(R.id.btn_add_palpation);
        ListView palpationContainer = rootView.findViewById(R.id.lv_palpation_container);
        Button addPulses = rootView.findViewById(R.id.btn_add_pulses);
        mEurythmyTextView = rootView.findViewById(R.id.tv_eurythmy_value);
        m28PulseTypesTextView = rootView.findViewById(R.id.tv_28_pulse_types_value);

        // set up OnTouchListeners on them
        goalEditText.setOnTouchListener(sessionTouchListener);
        dateEditText.setOnTouchListener(sessionTouchListener);

        // check if there's state to recover after config change
        if (savedInstanceState != null){
            // session data
            mSessionDate = savedInstanceState.getString(SESSION_DATE);
            mSessionGoal = savedInstanceState.getString(SESSION_GOAL);
            sessionDataHasChanged = savedInstanceState.getBoolean(SESSION_HAS_CHANGED);
            mQuestionArray = savedInstanceState.getStringArrayList(QUESTION_ARRAY);
            mObservationArray = savedInstanceState.getStringArrayList(OBSERVATION_ARRAY);
            mAuscultationArray = savedInstanceState.getStringArrayList(AUSCULTATION_ARRAY);
            mPalpationArray = savedInstanceState.getStringArrayList(PALPATION_ARRAY);
        } else {
            /** Check if this session was selected from the list at {@link SessionsListFragment} */
            Intent intent = getActivity().getIntent();
            if (intent.hasExtra(Constants.INTENT_SELECTED_SESSION_ID)) {
                Session selectedSession = intent.getParcelableExtra(Constants.INTENT_SELECTED_SESSION);
                onSessionSelectedInitialize(selectedSession);
            } else {
                // this is a new session, set session date to today date as a suggestion
                dateEditText.setText(DateFormatUtil.getCurrentDate());
            }
        }

        // set up button listener for questionnaire
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewSession) {
                    Intent questIntent = new Intent(getActivity(), QuestionnaireActivity.class);
                    startActivityForResult(questIntent, QUESTION_REQUEST);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.session_not_saved),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set up button listener for observation
        addObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewSession) {
                    Intent obsIntent = new Intent(getActivity(), ObservationActivity.class);
                    startActivityForResult(obsIntent, OBSERVATION_REQUEST);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.session_not_saved),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set up button listener for auscultation
        addAuscultation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewSession) {
                    Intent auscIntent = new Intent(getActivity(), AuscultationActivity.class);
                    startActivityForResult(auscIntent, AUSCULTATION_REQUEST);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.session_not_saved),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set up button listener for palpation
        addPalpation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewSession) {
                    Intent palpIntent = new Intent(getActivity(), PalpationActivity.class);
                    startActivityForResult(palpIntent, PALPATION_REQUEST);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.session_not_saved),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set up button listener for pulses
        addPulses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewSession) {
                    Intent pulsesIntent = new Intent(getActivity(), PulsesActivity.class);
                    startActivityForResult(pulsesIntent, PULSES_REQUEST);
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.session_not_saved),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        // set adapter to questionnaire listview
        mQuestionAdapter = new FourStepsAdapter(getActivity(), android.R.layout.simple_list_item_1, mQuestionArray);
        questionContainer.setAdapter(mQuestionAdapter);

        // set adapter to observation listview
        mObservationAdapter = new FourStepsAdapter(getActivity(), android.R.layout.simple_list_item_1, mObservationArray);
        observationContainer.setAdapter(mObservationAdapter);

        // set adapter to auscultation listview
        mAuscultationAdapter = new FourStepsAdapter(getActivity(), android.R.layout.simple_list_item_1, mAuscultationArray);
        auscultationContainer.setAdapter(mAuscultationAdapter);

        // set adapter to palpation listview
        mPalpationAdapter = new FourStepsAdapter(getActivity(), android.R.layout.simple_list_item_1, mPalpationArray);
        palpationContainer.setAdapter(mPalpationAdapter);

        return rootView;
    }

    private void loadFourSteps() {

        db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null){
                    Log.w(LOG_TAG, "Listen failed" , e);
                }

                if (documentSnapshot != null && documentSnapshot.exists()){
                    // Store all data to a Map
                    Map<String, Object> sessionMap = documentSnapshot.getData();

                    // [START - GET AND SET QUESTIONNAIRE DATA]
                    mQuestionMap = (Map<String, String>) sessionMap.get(Constants.QUEST_KEY);
                    if (mQuestionMap != null) {
                        mQuestionArray.clear();
                        for (Map.Entry<String, String> entry : mQuestionMap.entrySet()) {
                            if (!TextUtils.isEmpty(entry.getValue())) {
                                mQuestionArray.add(entry.getValue());
                            }
                            if (mQuestionAdapter != null) {
                                mQuestionAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.d(LOG_TAG, "No questionnaire data yet!");
                    }
                    // [END - GET AND SET QUESTIONNAIRE DATA]

                    // [START - GET AND SET OBSERVATION DATA]
                    mObservationMap = (Map<String, String>) sessionMap.get(Constants.OBS_KEY);
                    if (mObservationMap != null) {
                        mObservationArray.clear();
                        for (Map.Entry<String, String> entry : mObservationMap.entrySet()) {
                            if (!TextUtils.isEmpty(entry.getValue())) {
                                mObservationArray.add(entry.getValue());
                            }
                            if (mObservationAdapter != null) {
                                mObservationAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.d(LOG_TAG, "No observation data yet!");
                    }
                    // [END - GET AND SET OBSERVATION DATA]

                    // [START - GET AND SET AUSCULTATION DATA]
                    mAuscultationMap = (Map<String, String>) sessionMap.get(Constants.AUSC_KEY);
                    if (mAuscultationMap != null) {
                        mAuscultationArray.clear();
                        for (Map.Entry<String, String> entry : mAuscultationMap.entrySet()) {
                            if (!TextUtils.isEmpty(entry.getValue())) {
                                mAuscultationArray.add(entry.getValue());
                            }
                            if (mAuscultationAdapter != null) {
                                mAuscultationAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.d(LOG_TAG, "No auscultation data yet!");
                    }
                    // [END - GET AND SET AUSCULTATION DATA]

                    // [START - GET AND SET PALPATION DATA]
                    mPalpationMap = (Map<String, String>) sessionMap.get(Constants.PALP_KEY);
                    if (mPalpationMap != null) {
                        mPalpationArray.clear();
                        for (Map.Entry<String, String> entry : mPalpationMap.entrySet()) {
                            if (!TextUtils.isEmpty(entry.getValue())) {
                                mPalpationArray.add(entry.getValue());
                            }
                            if (mPalpationAdapter != null) {
                                mPalpationAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.d(LOG_TAG, "No palpation data yet!");
                    }
                    // [END - GET AND SET PALPATION DATA]

                    // [START - GET AND SET PULSES DATA]
                    mPulsesMap = (Map<String, Object>) sessionMap.get(Constants.PULSES_KEY);
                    if (mPulsesMap != null){
                        // get eurythmy data if any
                        Map<String, String> eurythmyMap = (Map<String, String>) mPulsesMap.get(Constants.PULSES_EURYTHMY_KEY);
                        if (eurythmyMap != null && eurythmyMap.containsKey(Constants.PULSES_EURYTHMY_BPB_KEY)){
                            mEurythmyTextView.setVisibility(View.VISIBLE);
                            StringBuilder eurythmyString = new StringBuilder();
                            if (getActivity() != null && isAdded()) {
                                eurythmyString
                                        .append(getResources().getString(R.string.pulses_eurythmy_value_label))
                                        .append(" ")
                                        .append(eurythmyMap.get(Constants.PULSES_EURYTHMY_BPB_KEY))
                                        .append(" ")
                                        .append(getResources().getString(R.string.pulses_beat_per_breath_label));
                                mEurythmyTextView.setText(eurythmyString);
                            }
                        } else {
                            mEurythmyTextView.setVisibility(View.GONE);
                        }

                        // get 28 Pulse Types data if any
                        Map<String, Boolean> pulseTypesMap = (Map<String, Boolean>) mPulsesMap.get(Constants.PULSES_28_TYPES_KEY);
                        if (pulseTypesMap != null){
                            m28PulseTypesTextView.setVisibility(View.VISIBLE);
                            StringBuilder typeString = new StringBuilder();
                            if (getActivity() != null && isAdded()) {
                                typeString
                                        .append(getResources().getString(R.string.pulses_28_types_value_label))
                                        .append(" ");
                                for (Map.Entry<String, Boolean> entry : pulseTypesMap.entrySet()) {
                                    typeString
                                            .append(entry.getKey().toUpperCase())
                                            .append(" / ");
                                }
                                m28PulseTypesTextView.setText(typeString);
                            }
                        } else {
                            m28PulseTypesTextView.setVisibility(View.GONE);
                        }

                    } else {
                        Log.d(LOG_TAG, "No pulses data yet!");
                    }
                    // [START - GET AND SET PULSES DATA]

                // There is no session document
                } else {
                    Log.d(LOG_TAG, "Current data: null");
                }
            }
        });
    }

    private void onSessionSelectedInitialize(Session selectedSession) {
        // Get date and goal
        String dateString = DateFormatUtil.convertTimestampToString(selectedSession.getTimestampCreated());
        dateEditText.setText(dateString);
        goalEditText.setText(selectedSession.getGoal());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_save:
                saveSession();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSession() {
        // get the session goal from edit text
        mSessionGoal = goalEditText.getText().toString().trim();
        mSessionDate = dateEditText.getText().toString().trim();

        // check if date and goal have been edited
        if (TextUtils.isEmpty(mSessionDate)) {
            Toast.makeText(getActivity(), getString(R.string.session_date_needed), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mSessionGoal)) {
            Toast.makeText(getActivity(), getString(R.string.session_goal_needed), Toast.LENGTH_SHORT).show();
            return;
        }

        // build session object
        Session session = new Session();
        session.setGoal(mSessionGoal);
        long sessionTimestamp = 0;
        // Ensure date is valid
        if (DateFormatUtil.validate(mSessionDate)){
            try {
                // convert to timestamp in millisec
                sessionTimestamp = DateFormatUtil.convertStringToTimestamp(mSessionDate) * 1000;
                session.setTimestampCreated(sessionTimestamp);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), getString(R.string.date_format_not_valid), Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // date is not valid
            Toast.makeText(getActivity(), getString(R.string.date_format_not_valid), Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNewSession) {
            createSessionDocument(session);
        } else {
            updateSessionDocument(sessionTimestamp, mSessionGoal);
        }
    }

    private void updateSessionDocument(long timestamp, String goal) {

        // build firestore path
        DocumentReference sessionDoc = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid);

        WriteBatch batch = db.batch();

        // update date and session
        if(sessionDataHasChanged) {
            batch.update(sessionDoc, Constants.SESSION_TIMESTAMP, timestamp);
            batch.update(sessionDoc, Constants.SESSION_GOAL, goal);
        }

        // commit the batch if there is any changes
        if (sessionDataHasChanged){
            batch.commit()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), getString(R.string.session_update_success), Toast.LENGTH_SHORT).show();
                            sessionDataHasChanged = false;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), getString(R.string.session_update_failed), Toast.LENGTH_SHORT).show();
                            Log.w(LOG_TAG, "error updating session: " + e);
                        }
                    });
        }
    }

    private void createSessionDocument(Session session) {
        // add document to firestore
        db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .add(session)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        sSessionid = documentReference.getId();
                        isNewSession = false;
                        sessionDataHasChanged = false;
                        Toast.makeText(getActivity(), getString(R.string.session_saved), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){

            case QUESTION_REQUEST:
                if (resultCode == RESULT_OK){
                    Toast.makeText(getActivity(), getString(R.string.questionnaire_saved), Toast.LENGTH_SHORT).show();
                }
                else if (resultCode == RESULT_CANCELED){
                    // nothing yet
                }
                mQuestionAdapter.clear();
                break;

            case OBSERVATION_REQUEST:
                if (resultCode == RESULT_OK){
                    Toast.makeText(getActivity(), getString(R.string.observation_saved), Toast.LENGTH_SHORT).show();
                }
                else if (resultCode == RESULT_CANCELED){
                    // nothing yet
                }
                mObservationAdapter.clear();
                break;

            case AUSCULTATION_REQUEST:
                if (resultCode == RESULT_OK){
                    Toast.makeText(getActivity(), getString(R.string.auscultation_saved), Toast.LENGTH_SHORT).show();
                }
                else if (resultCode == RESULT_CANCELED){
                    // nothing yet
                }
                mAuscultationAdapter.clear();
                break;

            case PALPATION_REQUEST:
                if (resultCode == RESULT_OK){
                    Toast.makeText(getActivity(), getString(R.string.palpation_saved), Toast.LENGTH_SHORT).show();
                }
                else if (resultCode == RESULT_CANCELED){
                    // nothing yet
                }
                mPalpationAdapter.clear();
                break;

            case PULSES_REQUEST:
                if (resultCode == RESULT_OK){
                    Toast.makeText(getActivity(), getString(R.string.pulses_saved), Toast.LENGTH_SHORT).show();
                }
                else if (resultCode == RESULT_CANCELED){
                    // nothing yet
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid request code, " + requestCode);
        }
        loadFourSteps();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // session data
        outState.putString(SESSION_DATE, mSessionDate);
        outState.putString(SESSION_GOAL, mSessionGoal);
        outState.putBoolean(SESSION_HAS_CHANGED, sessionDataHasChanged);
        outState.putStringArrayList(QUESTION_ARRAY, mQuestionArray);
        outState.putStringArrayList(OBSERVATION_ARRAY, mObservationArray);
        outState.putStringArrayList(AUSCULTATION_ARRAY, mAuscultationArray);
        outState.putStringArrayList(PALPATION_ARRAY, mPalpationArray);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mQuestionAdapter != null) {
            mQuestionAdapter.clear();
            mQuestionAdapter.notifyDataSetChanged();
        }
        if (mObservationAdapter != null) {
            mObservationAdapter.clear();
            mObservationAdapter.notifyDataSetChanged();
        }
        if (mAuscultationAdapter != null) {
            mAuscultationAdapter.clear();
            mAuscultationAdapter.notifyDataSetChanged();
        }
        if (mPalpationAdapter != null) {
            mPalpationAdapter.clear();
            mPalpationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isNewSession) loadFourSteps();
    }
}

