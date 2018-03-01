package com.acpnctr.acpnctr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

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

public class ObservationActivity extends AppCompatActivity {

    private static final String LOG_TAG = ObservationActivity.class.getSimpleName();

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText mBehaviour;
    EditText mTongue;
    EditText mLips;
    EditText mLimb;
    EditText mMeridian;
    EditText mMorphology;
    EditText mNails;
    EditText mSkin;
    EditText mHairiness;
    EditText mComplexion;
    EditText mEyes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        // get references to our views
        mBehaviour = findViewById(R.id.et_obs_behaviour);
        mTongue = findViewById(R.id.et_obs_tongue);
        mLips = findViewById(R.id.et_obs_lips);
        mLimb = findViewById(R.id.et_obs_limb);
        mMeridian = findViewById(R.id.et_obs_meridian);
        mMorphology = findViewById(R.id.et_obs_morphology);
        mNails = findViewById(R.id.et_obs_nails);
        mSkin = findViewById(R.id.et_obs_skin);
        mHairiness = findViewById(R.id.et_obs_hairiness);
        mComplexion = findViewById(R.id.et_obs_complexion);
        mEyes = findViewById(R.id.et_obs_eyes);

        // initialize list of observations
        loadObservation();
    }

    private void loadObservation() {
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
                            // store all data to a Map
                            Map<String, Object> sessionMap = documentSnapshot.getData();
                            // get observation data and display them to edit views
                            Map<String, String> observationMap = (Map<String, String>) sessionMap.get(Constants.OBS_KEY);
                            if (observationMap != null){
                                if(observationMap.containsKey(Constants.OBS_BEHAVIOUR_KEY)){
                                    mBehaviour.setText(observationMap.get(Constants.OBS_BEHAVIOUR_KEY));
                                }
                                if(observationMap.containsKey(Constants.OBS_TONGUE_KEY)){
                                    mTongue.setText(observationMap.get(Constants.OBS_TONGUE_KEY));
                                }
                                if(observationMap.containsKey(Constants.OBS_LIPS_KEY)){
                                    mLips.setText(observationMap.get(Constants.OBS_LIPS_KEY));
                                }
                                if(observationMap.containsKey(Constants.OBS_LIMB_KEY)){
                                    mLimb.setText(observationMap.get(Constants.OBS_LIMB_KEY));
                                }
                                if(observationMap.containsKey(Constants.OBS_MERIDIAN_KEY)){
                                    mMeridian.setText(observationMap.get(Constants.OBS_MERIDIAN_KEY));
                                }
                                if(observationMap.containsKey(Constants.OBS_MORPHOLOGY_KEY)){
                                    mMorphology.setText(observationMap.get(Constants.OBS_MORPHOLOGY_KEY));
                                }
                                if(observationMap.containsKey(Constants.OBS_NAILS_KEY)){
                                    mNails.setText(observationMap.get(Constants.OBS_NAILS_KEY));
                                }
                                if(observationMap.containsKey(Constants.OBS_SKIN_KEY)){
                                    mSkin.setText(observationMap.get(Constants.OBS_SKIN_KEY));
                                }
                                if(observationMap.containsKey(Constants.OBS_HAIRINESS_KEY)){
                                    mHairiness.setText(observationMap.get(Constants.OBS_HAIRINESS_KEY));
                                }
                                if(observationMap.containsKey(Constants.OBS_COMPLEXION_KEY)){
                                    mComplexion.setText(observationMap.get(Constants.OBS_COMPLEXION_KEY));
                                }
                                if(observationMap.containsKey(Constants.OBS_EYES_KEY)){
                                    mEyes.setText(observationMap.get(Constants.OBS_EYES_KEY));
                                }
                            } else {
                                Log.d(LOG_TAG, "No Observation data yet!");
                            }
                        } else {
                            Log.d(LOG_TAG, "Current data: null");
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.observation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                cancelObservation();
                return true;
            case R.id.action_save:
                saveObservation();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveObservation() {
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
        Map<String, String> observationMap = new HashMap<>();

        observationMap.put(Constants.OBS_BEHAVIOUR_KEY, mBehaviour.getText().toString().trim());
        observationMap.put(Constants.OBS_TONGUE_KEY, mTongue.getText().toString().trim());
        observationMap.put(Constants.OBS_LIPS_KEY, mLips.getText().toString().trim());
        observationMap.put(Constants.OBS_LIMB_KEY, mLimb.getText().toString().trim());
        observationMap.put(Constants.OBS_MERIDIAN_KEY, mMeridian.getText().toString().trim());
        observationMap.put(Constants.OBS_MORPHOLOGY_KEY, mMorphology.getText().toString().trim());
        observationMap.put(Constants.OBS_NAILS_KEY, mNails.getText().toString().trim());
        observationMap.put(Constants.OBS_SKIN_KEY, mSkin.getText().toString().trim());
        observationMap.put(Constants.OBS_HAIRINESS_KEY, mHairiness.getText().toString().trim());
        observationMap.put(Constants.OBS_COMPLEXION_KEY, mComplexion.getText().toString().trim());
        observationMap.put(Constants.OBS_EYES_KEY, mEyes.getText().toString().trim());

        // write data to the batch
        batch.update(sessionDoc, Constants.OBS_KEY, observationMap);

        // commit the batch
        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(LOG_TAG, "observation saved!");
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "error updating session: " + e);
                    }
                });
    }

    private void cancelObservation() {
        Intent cancelIntent = new Intent();
        setResult(RESULT_CANCELED, cancelIntent);
        finish();
    }
}
