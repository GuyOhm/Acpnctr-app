package com.acpnctr.acpnctr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.acpnctr.acpnctr.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

import static com.acpnctr.acpnctr.SessionActivity.sClientid;
import static com.acpnctr.acpnctr.SessionActivity.sSessionid;
import static com.acpnctr.acpnctr.SessionActivity.sUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_SESSIONS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

public class QuestionnaireActivity extends AppCompatActivity {

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText mYinyang;
    EditText mFivephases;
    EditText mDiet;
    EditText mDigestion;
    EditText mWayOfLife;
    EditText mSleep;
    EditText mSymptoms;
    EditText mMedication;
    EditText mEvents;
    EditText mEmotional;
    EditText mPsychological;
    EditText mGynecological;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        // get references to our views
        mYinyang = findViewById(R.id.et_quest_yinyang);
        mFivephases = findViewById(R.id.et_quest_5_phases);
        mDiet = findViewById(R.id.et_quest_diet);
        mDigestion = findViewById(R.id.et_quest_digestion);
        mWayOfLife = findViewById(R.id.et_quest_way_of_life);
        mSleep = findViewById(R.id.et_quest_sleep);
        mSymptoms = findViewById(R.id.et_quest_symptoms);
        mMedication = findViewById(R.id.et_quest_medication);
        mEvents = findViewById(R.id.et_quest_events);
        mEmotional = findViewById(R.id.et_quest_emotional);
        mPsychological = findViewById(R.id.et_quest_psychological);
        mGynecological = findViewById(R.id.et_quest_gynecological);


        // initialize list of questions
        loadQuestionnaire();
    }

    private void loadQuestionnaire() {
        db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid)
                .addSnapshotListener(QuestionnaireActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (e != null){
                            Toast.makeText(QuestionnaireActivity.this,
                                    R.string.generic_data_load_failed, Toast.LENGTH_SHORT).show();
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()){
                            // store all data to a Map
                            Map<String, Object> sessionMap = documentSnapshot.getData();
                            // get questionnaire data and display them to edit views
                            Map<String, String> questionMap = (Map<String, String>) sessionMap.get(Constants.QUEST_KEY);
                            if (questionMap != null){
                                if(questionMap.containsKey(Constants.QUEST_YIN_YANG_KEY)){
                                    mYinyang.setText(questionMap.get(Constants.QUEST_YIN_YANG_KEY));
                                }
                                if(questionMap.containsKey(Constants.QUEST_FIVE_PHASES_KEY)){
                                    mFivephases.setText(questionMap.get(Constants.QUEST_FIVE_PHASES_KEY));
                                }
                                if(questionMap.containsKey(Constants.QUEST_DIET_KEY)){
                                    mDiet.setText(questionMap.get(Constants.QUEST_DIET_KEY));
                                }
                                if(questionMap.containsKey(Constants.QUEST_DIGESTION_KEY)){
                                    mDigestion.setText(questionMap.get(Constants.QUEST_DIGESTION_KEY));
                                }
                                if(questionMap.containsKey(Constants.QUEST_WAY_OF_LIFE_KEY)){
                                    mWayOfLife.setText(questionMap.get(Constants.QUEST_WAY_OF_LIFE_KEY));
                                }
                                if(questionMap.containsKey(Constants.QUEST_SLEEP_KEY)){
                                    mSleep.setText(questionMap.get(Constants.QUEST_SLEEP_KEY));
                                }
                                if(questionMap.containsKey(Constants.QUEST_SYMPTOMS_KEY)){
                                    mSymptoms.setText(questionMap.get(Constants.QUEST_SYMPTOMS_KEY));
                                }
                                if(questionMap.containsKey(Constants.QUEST_MEDICATION_KEY)){
                                    mMedication.setText(questionMap.get(Constants.QUEST_MEDICATION_KEY));
                                }
                                if(questionMap.containsKey(Constants.QUEST_EVENTS_KEY)){
                                    mEvents.setText(questionMap.get(Constants.QUEST_EVENTS_KEY));
                                }
                                if(questionMap.containsKey(Constants.QUEST_EMOTIONAL_KEY)){
                                    mEmotional.setText(questionMap.get(Constants.QUEST_EMOTIONAL_KEY));
                                }
                                if(questionMap.containsKey(Constants.QUEST_PSYCHOLOGICAL_KEY)){
                                    mPsychological.setText(questionMap.get(Constants.QUEST_PSYCHOLOGICAL_KEY));
                                }
                                if(questionMap.containsKey(Constants.QUEST_GYNECOLOGICAL_KEY)){
                                    mGynecological.setText(questionMap.get(Constants.QUEST_GYNECOLOGICAL_KEY));
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.questionnaire, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                cancelQuestion();
                return true;
            case R.id.action_save:
                saveQuestion();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveQuestion() {
        // build firestore path
        DocumentReference sessionDoc = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid);

        // create a batch to write data to firestore at once
        WriteBatch batch = db.batch();
        // create a hashmap to store data fetched from UI
        Map<String, String> questionMap = new HashMap<>();

        questionMap.put(Constants.QUEST_YIN_YANG_KEY, mYinyang.getText().toString().trim());
        questionMap.put(Constants.QUEST_FIVE_PHASES_KEY, mFivephases.getText().toString().trim());
        questionMap.put(Constants.QUEST_DIET_KEY, mDiet.getText().toString().trim());
        questionMap.put(Constants.QUEST_DIGESTION_KEY, mDigestion.getText().toString().trim());
        questionMap.put(Constants.QUEST_WAY_OF_LIFE_KEY, mWayOfLife.getText().toString().trim());
        questionMap.put(Constants.QUEST_SLEEP_KEY, mSleep.getText().toString().trim());
        questionMap.put(Constants.QUEST_SYMPTOMS_KEY, mSymptoms.getText().toString().trim());
        questionMap.put(Constants.QUEST_MEDICATION_KEY, mMedication.getText().toString().trim());
        questionMap.put(Constants.QUEST_EVENTS_KEY, mEvents.getText().toString().trim());
        questionMap.put(Constants.QUEST_EMOTIONAL_KEY, mEmotional.getText().toString().trim());
        questionMap.put(Constants.QUEST_PSYCHOLOGICAL_KEY, mPsychological.getText().toString().trim());
        questionMap.put(Constants.QUEST_GYNECOLOGICAL_KEY, mGynecological.getText().toString().trim());

        // write data to the batch
        batch.update(sessionDoc, Constants.QUEST_KEY, questionMap);

        // commit the batch
        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionnaireActivity.this,
                                R.string.generic_data_insert_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cancelQuestion() {
        Intent cancelIntent = new Intent();
        setResult(RESULT_CANCELED, cancelIntent);
        finish();
    }
}
