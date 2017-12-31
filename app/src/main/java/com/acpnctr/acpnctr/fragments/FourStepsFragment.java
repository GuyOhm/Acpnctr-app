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
import android.widget.EditText;
import android.widget.Toast;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Session;
import com.acpnctr.acpnctr.utils.Constants;
import com.acpnctr.acpnctr.utils.DateFormatUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

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

    // Class members for our views
    private EditText goalEditText;
    private EditText dateEditText;

    private String mSessionDate;
    private String mSessionGoal;
    
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

    public FourStepsFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_four_steps, container, false);

        // hook up the views
        goalEditText = rootView.findViewById(R.id.et_session_goal);
        dateEditText = rootView.findViewById(R.id.et_session_date);

        // set up OnTouchListeners on them
        goalEditText.setOnTouchListener(sessionTouchListener);
        dateEditText.setOnTouchListener(sessionTouchListener);

        if (savedInstanceState != null){
            mSessionDate = savedInstanceState.getString(SESSION_DATE);
            mSessionGoal = savedInstanceState.getString(SESSION_GOAL);
        }

        /** Check if this session was selected from the list at {@link SessionsListFragment} */
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(Constants.INTENT_SELECTED_SESSION_ID)){
            Session selectedSession = intent.getParcelableExtra(Constants.INTENT_SELECTED_SESSION);
            onSessionSelectedInitialize(selectedSession);
        }

        return rootView;
    }

    private void onSessionSelectedInitialize(Session selectedSession) {
        String dateString = DateFormatUtil.convertTimestampToString(selectedSession.getTimestampCreated());
        dateEditText.setText(dateString);
        goalEditText.setText(selectedSession.getGoal());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_save:
                if (sessionDataHasChanged) {
                    saveSession();
                }
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
        // TODO: ensure date is valid
        try {
            sessionTimestamp = DateFormatUtil.convertStringToTimestamp(mSessionDate) * 1000; // to millisec
            session.setTimestampCreated(sessionTimestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (isNewSession) {
            createSessionDocument(session);
        } else {
            updateSessionDocument(sessionTimestamp, mSessionGoal);
        }
    }

    private void updateSessionDocument(long timestamp, String goal) {
        // create a hashmap with values to update
        Map<String, Object> updates = new HashMap<>();
        updates.put(Session.SESSION_TIMESTAMP, timestamp);
        updates.put(Session.SESSION_GOAL, goal);

        db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid)
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SESSION_DATE, mSessionDate);
        outState.putString(SESSION_GOAL, mSessionGoal);
    }
}

