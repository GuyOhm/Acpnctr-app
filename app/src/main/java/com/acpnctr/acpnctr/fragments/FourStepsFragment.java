package com.acpnctr.acpnctr.fragments;


import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.acpnctr.acpnctr.QuestionnaireActivity;
import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Session;
import com.acpnctr.acpnctr.utils.Constants;
import com.acpnctr.acpnctr.utils.DateFormatUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
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
    // for questionnaire
    private Button addQuestion;
    private ListView questionContainer;
    private ArrayList<String> mQuestionArray = new ArrayList<>();
    private ArrayAdapter<String> mQuestionAdapter;
    private Map<String, String> mQuestionMap = new HashMap<>();
    private boolean questionnaireHasChanged = false;

    // COMPLETED: create a flag for questionnaire changed
    // COMPLETED : add questionnaire parcel in Session obj
    // COMPLETED: initialize questionnaire if any
    // COMPLETED: add save instance for questionnaire
    // TODO: fix question list bug
    // TODO: polish dialog box (getWindow, WindowManager, LayoutParams.FILL_PARENT)
    // TODO: refactor showEditBow for it to be usable by other lists (pass in data array, Map...)
    // TODO: finish to integrate all question fields

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
    private static final String QUESTION_MAP = "question_map";
    private static final String QUESTION_HAS_CHANGED = "question_map";

    // Request code for result from activity
    public static final int QUESTION_REQUEST = 2;

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
        addQuestion = rootView.findViewById(R.id.btn_add_question);
        questionContainer = rootView.findViewById(R.id.lv_questionnaire_container);

        // set up OnTouchListeners on them
        goalEditText.setOnTouchListener(sessionTouchListener);
        dateEditText.setOnTouchListener(sessionTouchListener);


        // check if there's state to recover after config change
        if (savedInstanceState != null){
            // session data
            mSessionDate = savedInstanceState.getString(SESSION_DATE);
            mSessionGoal = savedInstanceState.getString(SESSION_GOAL);
            sessionDataHasChanged = savedInstanceState.getBoolean(SESSION_HAS_CHANGED);
            // questionnaire data
            mQuestionArray = savedInstanceState.getStringArrayList(QUESTION_ARRAY);
            Bundle questionBundle = savedInstanceState.getBundle(QUESTION_MAP);
            for (String key: questionBundle.keySet()){
                mQuestionMap.put(key, questionBundle.getString(key));
            }
            questionnaireHasChanged = savedInstanceState.getBoolean(QUESTION_HAS_CHANGED);
        } else {

            /** Check if this session was selected from the list at {@link SessionsListFragment} */
            Intent intent = getActivity().getIntent();
            if (intent.hasExtra(Constants.INTENT_SELECTED_SESSION_ID)) {
                Session selectedSession = intent.getParcelableExtra(Constants.INTENT_SELECTED_SESSION);
                onSessionSelectedInitialize(selectedSession);
            }
        }

        // set up button listener for questionnaire
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewSession) {
                    Intent questIntent = new Intent(getActivity(), QuestionnaireActivity.class);
                    if (!mQuestionMap.isEmpty()){
                        for (Map.Entry<String, String> entry: mQuestionMap.entrySet()) {
                            questIntent.putExtra(entry.getKey(), entry.getValue());
                        }
                    }
                    startActivityForResult(questIntent, QUESTION_REQUEST);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.session_not_saved),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set adapter to questionnaire listview
        mQuestionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mQuestionArray);
        questionContainer.setAdapter(mQuestionAdapter);
        questionContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showEditBox(mQuestionArray.get(position), position);
            }
        });

        return rootView;
    }

    private void showEditBox(final String string, final int position) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.edit_box);
        final EditText textToEdit = dialog.findViewById(R.id.et_dialog_text);
        textToEdit.setText(string);
        Button cancelBtn = dialog.findViewById(R.id.btn_dialog_cancel);
        Button saveBtn = dialog.findViewById(R.id.btn_dialog_save);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newString = textToEdit.getText().toString().trim();
                mQuestionArray.set(position, newString);
                updateQuestionMap(string, newString);
                questionnaireHasChanged = true;
                mQuestionAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            private void updateQuestionMap(String string, String newString) {
                for (Map.Entry<String, String> entry: mQuestionMap.entrySet()){
                    if (string.equals(entry.getValue())){
                        mQuestionMap.put(entry.getKey(), newString);
                    }
                }
            }
        });
        dialog.show();

    }

    private void onSessionSelectedInitialize(Session selectedSession) {
        // Get date and goal
        String dateString = DateFormatUtil.convertTimestampToString(selectedSession.getTimestampCreated());
        dateEditText.setText(dateString);
        goalEditText.setText(selectedSession.getGoal());
        // Get questionnaire if any
        mQuestionMap = selectedSession.getQuestionnaire();
        if (!mQuestionMap.isEmpty()){
            for (Map.Entry<String, String> entry : mQuestionMap.entrySet()) {
                mQuestionArray.add(entry.getValue());
            }
        }
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
            batch.update(sessionDoc, Session.SESSION_TIMESTAMP, timestamp);
            batch.update(sessionDoc, Session.SESSION_GOAL, goal);
        }

        // save questionnaire if any
        if (!mQuestionMap.isEmpty() && questionnaireHasChanged){
            batch.update(sessionDoc, Session.QUEST_KEY, mQuestionMap);
        }

        // commit the batch if there is any changes
        if (sessionDataHasChanged || questionnaireHasChanged){
            batch.commit()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), getString(R.string.session_update_success), Toast.LENGTH_SHORT).show();
                            sessionDataHasChanged = false;
                            questionnaireHasChanged = false;
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
        if (requestCode == QUESTION_REQUEST){
            if (resultCode == RESULT_OK){
                Bundle b = data.getExtras();
                if (data.hasExtra(Session.QUEST_YIN_YANG_KEY)){
                    // add data to questionnaire listview
                    mQuestionArray.add(data.getStringExtra(Session.QUEST_YIN_YANG_KEY));
                    // add data to hashmap so we can write it to firestore
                    mQuestionMap.put(Session.QUEST_YIN_YANG_KEY, data.getStringExtra(Session.QUEST_YIN_YANG_KEY));
                }
                questionnaireHasChanged = true;
                // notify the adapter that data have changed
                mQuestionAdapter.notifyDataSetChanged();
            }
            if (resultCode == RESULT_CANCELED){
                mQuestionAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // session data
        outState.putString(SESSION_DATE, mSessionDate);
        outState.putString(SESSION_GOAL, mSessionGoal);
        outState.putBoolean(SESSION_HAS_CHANGED, sessionDataHasChanged);
        // questionnaire data
        outState.putStringArrayList(QUESTION_ARRAY, mQuestionArray);
        outState.putBoolean(QUESTION_HAS_CHANGED, questionnaireHasChanged);
        Bundle questionBundle = new Bundle();
        for (Map.Entry<String, String> entry: mQuestionMap.entrySet()){
            questionBundle.putString(entry.getKey(), entry.getValue());
        }
        outState.putBundle(QUESTION_MAP, questionBundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        mQuestionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mQuestionAdapter != null) {
            mQuestionAdapter.clear();
            mQuestionAdapter.notifyDataSetChanged();
        }
    }
}

