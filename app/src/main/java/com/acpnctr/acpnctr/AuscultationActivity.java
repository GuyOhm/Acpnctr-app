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

public class AuscultationActivity extends AppCompatActivity {

    private static final String LOG_TAG = AuscultationActivity.class.getSimpleName();

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText mBloodPressure;
    EditText mAbdomen;
    EditText mSmell;
    EditText mBreathing;
    EditText mCough;
    EditText mVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auscultation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        // get references to our views
        mBloodPressure = findViewById(R.id.et_ausc_blood_pressure);
        mAbdomen = findViewById(R.id.et_ausc_abdomen);
        mSmell = findViewById(R.id.et_ausc_smell);
        mBreathing = findViewById(R.id.et_ausc_breathing);
        mCough = findViewById(R.id.et_ausc_cough);
        mVoice = findViewById(R.id.et_ausc_voice);

        // initialize list of auscultations
        loadAuscultation();
    }

    private void loadAuscultation() {
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
                            // get auscultation data and display them to edit views
                            Map<String, String> auscultationMap = (Map<String, String>) sessionMap.get(Constants.AUSC_KEY);
                            if (auscultationMap != null){
                                if(auscultationMap.containsKey(Constants.AUSC_BLOOD_PRESSURE_KEY)){
                                    mBloodPressure.setText(auscultationMap.get(Constants.AUSC_BLOOD_PRESSURE_KEY));
                                }
                                if(auscultationMap.containsKey(Constants.AUSC_ABDOMEN_KEY)){
                                    mAbdomen.setText(auscultationMap.get(Constants.AUSC_ABDOMEN_KEY));
                                }
                                if(auscultationMap.containsKey(Constants.AUSC_SMELL_KEY)){
                                    mSmell.setText(auscultationMap.get(Constants.AUSC_SMELL_KEY));
                                }
                                if(auscultationMap.containsKey(Constants.AUSC_BREATHING_KEY)){
                                    mBreathing.setText(auscultationMap.get(Constants.AUSC_BREATHING_KEY));
                                }
                                if(auscultationMap.containsKey(Constants.AUSC_COUGH_KEY)){
                                    mBreathing.setText(auscultationMap.get(Constants.AUSC_COUGH_KEY));
                                }
                                if(auscultationMap.containsKey(Constants.AUSC_VOICE_KEY)){
                                    mVoice.setText(auscultationMap.get(Constants.AUSC_VOICE_KEY));
                                }
                            } else {
                                Log.d(LOG_TAG, "No Auscultation data yet!");
                            }
                        } else {
                            Log.d(LOG_TAG, "Current data: null");
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.auscultation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                cancelAuscultation();
                return true;
            case R.id.action_save:
                saveAuscultation();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAuscultation() {
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
        Map<String, String> auscultationMap = new HashMap<>();

        auscultationMap.put(Constants.AUSC_BLOOD_PRESSURE_KEY, mBloodPressure.getText().toString().trim());
        auscultationMap.put(Constants.AUSC_ABDOMEN_KEY, mAbdomen.getText().toString().trim());
        auscultationMap.put(Constants.AUSC_SMELL_KEY, mSmell.getText().toString().trim());
        auscultationMap.put(Constants.AUSC_BREATHING_KEY, mBreathing.getText().toString().trim());
        auscultationMap.put(Constants.AUSC_COUGH_KEY, mCough.getText().toString().trim());
        auscultationMap.put(Constants.AUSC_VOICE_KEY, mVoice.getText().toString().trim());

        // write data to the batch
        batch.update(sessionDoc, Constants.AUSC_KEY, auscultationMap);

        // commit the batch
        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(LOG_TAG, "auscultation saved!");
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

    private void cancelAuscultation() {
        Intent cancelIntent = new Intent();
        setResult(RESULT_CANCELED, cancelIntent);
        finish();
    }
}
