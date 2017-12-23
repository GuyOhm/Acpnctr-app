package com.acpnctr.acpnctr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Session;
import com.acpnctr.acpnctr.utils.DateFormatUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;

import static com.acpnctr.acpnctr.SessionActivity.mClientid;
import static com.acpnctr.acpnctr.SessionActivity.mSessionid;
import static com.acpnctr.acpnctr.SessionActivity.mUid;
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
    EditText goalEditText;
    EditText dateEditText;

    String mSessionDate;
    String mSessionGoal;

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

        goalEditText = rootView.findViewById(R.id.et_session_goal);
        dateEditText = rootView.findViewById(R.id.et_session_date);

        if (savedInstanceState != null){
            mSessionDate = savedInstanceState.getString(SESSION_DATE);
            mSessionGoal = savedInstanceState.getString(SESSION_GOAL);
        }

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_save:
                createSessionDocument();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createSessionDocument() {
        // get the session goal from edit text
        mSessionGoal = goalEditText.getText().toString().trim();
        mSessionDate = dateEditText.getText().toString().trim();
        // build session object
        Session session = new Session();
        session.setGoal(mSessionGoal);
        try {
            long sessionTimestamp = DateFormatUtil.convertStringToTimestamp(mSessionDate) * 1000; // to millisec
            session.setTimestampCreated(sessionTimestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // add document to firestore
        db.collection(FIRESTORE_COLLECTION_USERS)
                .document(mUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(mClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .add(session)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        mSessionid = documentReference.getId();
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

