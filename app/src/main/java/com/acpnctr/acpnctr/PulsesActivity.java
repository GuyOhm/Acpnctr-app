package com.acpnctr.acpnctr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.acpnctr.acpnctr.models.Session;
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
import java.util.Locale;
import java.util.Map;

import static com.acpnctr.acpnctr.SessionActivity.sClientid;
import static com.acpnctr.acpnctr.SessionActivity.sSessionid;
import static com.acpnctr.acpnctr.SessionActivity.sUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_SESSIONS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

public class PulsesActivity extends AppCompatActivity {

    private static final String LOG_TAG = QuestionnaireActivity.class.getSimpleName();

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Member variables
    // ... Eurythmy category
    CheckBox mEurythmyCheckbox;
    EditText mBeatPerMin;
    EditText mBreathPerMin;
    TextView mBeatPerBreath;
    Group mEurythmyGroup;
    // ... 28 types category
    // TODO: create a layout for list item to feed the gridview
    // TODO: create a touch selector for selected item (programmatically?)
    // TODO: create all types of pulses => data
    // TODO: feed these data into the gridview
    // TODO: when selected => boolean pulseType = true
    CheckBox m28TypesCheckbox;
    GridView m28TypesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulses);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get references to our views
        mEurythmyCheckbox = findViewById(R.id.cb_eurythmy);
        mBeatPerMin = findViewById(R.id.et_pulses_beatpm);
        mBreathPerMin = findViewById(R.id.et_pulses_breathpm);
        mBeatPerBreath = findViewById(R.id.tv_pulses_beat_per_breath);
        mEurythmyGroup = findViewById(R.id.group_eurythmy);

        // Listen to Eurythmy checkbox and hide or show accordingly
        mEurythmyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    hideEurythmy();
                } else {
                    showEurythmy();
                }
            }
        });

        // initialize data for pulses activity
        initializePulsesUI();

        // compute Beat per Breath when the user has types both values
        setEurythmyListenerAndCalculate();
    }

    /**
     * Listen to Eurythmy edit texts and calculate eurythmy value when both source values
     * has been entered
     */
    private void setEurythmyListenerAndCalculate() {
        mBeatPerMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String beatString = editable.toString().trim();
                String breathString = mBreathPerMin.getText().toString().trim();
                if (!breathString.isEmpty() && !beatString.isEmpty()) {
                    mBeatPerBreath.setText(calculateBeatPerBreath(beatString, breathString));
                }
            }
        });

        mBreathPerMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String breathString = editable.toString().trim();
                String beatString = mBeatPerMin.getText().toString().trim();
                if (!breathString.isEmpty() && !beatString.isEmpty()) {
                    mBeatPerBreath.setText(calculateBeatPerBreath(beatString, breathString));
                }
            }
        });
    }

    /**
     * Calculate and return the number of Beat per Breath for Eurythmy
     *
     * @param beatString
     * @param breathString
     * @return String which is the number of Beat per Breath
     */
    private String calculateBeatPerBreath(String beatString, String breathString) {
        float beatInt = Integer.parseInt(beatString);
        float breathInt = Integer.parseInt(breathString);
        float beatPerBreath = beatInt / breathInt;
        // TODO: TEST WITH LOCALE US
        return String.format(Locale.US,"%.1f", beatPerBreath);
    }

    /**
     * Initialize pulses UI and load data if any
     */
    private void initializePulsesUI() {
        // load pulses data
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
                            // get pulses data and display them to views
                            Map<String, Object> pulsesMap = (Map<String, Object>) sessionMap.get(Session.PULSES_KEY);
                            if (pulsesMap != null){

                                // get eurythmy data if any
                                Map<String, String> eurythmyMap = (Map<String, String>) pulsesMap.get(Session.PULSES_EURYTHMY_KEY);
                                if (eurythmyMap != null) {
                                    if (eurythmyMap.containsKey(Session.PULSES_EURYTHMY_BEAT_KEY)) {
                                        mBeatPerMin.setText(eurythmyMap.get(Session.PULSES_EURYTHMY_BEAT_KEY));
                                    }
                                    if (eurythmyMap.containsKey(Session.PULSES_EURYTHMY_BREATH_KEY)) {
                                        mBreathPerMin.setText(eurythmyMap.get(Session.PULSES_EURYTHMY_BREATH_KEY));
                                    }
                                    if (eurythmyMap.containsKey(Session.PULSES_EURYTHMY_BPB_KEY)) {
                                        mBeatPerBreath.setText(eurythmyMap.get(Session.PULSES_EURYTHMY_BPB_KEY));
                                    }
                                    mEurythmyCheckbox.setChecked(true);
                                } else {
                                    hideEurythmy();
                                }

                                // get 28 types of pulses data


                            } else {
                                Log.d(LOG_TAG, "No pulses data yet!");
                                hideEurythmy();
                            }
                        } else {
                            Log.d(LOG_TAG, "Current data: null");
                        }
                    }
                });
    }

    /**
     * Show group for views for Eurythmy
     */
    private void showEurythmy(){
        mEurythmyGroup.setVisibility(View.VISIBLE);
    }

    /**
     * Hide group for views for Eurythmy
     */
    private void hideEurythmy(){
        mEurythmyGroup.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pulses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                cancelPulses();
                return true;
            case R.id.action_save:
                savePulses();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePulses() {
        // build firestore path
        DocumentReference sessionDoc = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid);

        // create a batch to write data to firestore at once
        WriteBatch batch = db.batch();
        // create a hashmap to store data for pulses fetched from UI
        Map<String, Object> pulsesMap = new HashMap<>();

        // add eurythmy data to the batch if checkbox is checked
        if (mEurythmyCheckbox.isChecked()) {
            // create a hashmap to store eurythmy data
            Map<String, String> eurythmyMap = new HashMap<>();
            eurythmyMap.put(Session.PULSES_EURYTHMY_BEAT_KEY, mBeatPerMin.getText().toString().trim());
            eurythmyMap.put(Session.PULSES_EURYTHMY_BREATH_KEY, mBreathPerMin.getText().toString().trim());
            eurythmyMap.put(Session.PULSES_EURYTHMY_BPB_KEY, mBeatPerBreath.getText().toString().trim());
            // add this hashmap to main pulses hashmap
            pulsesMap.put(Session.PULSES_EURYTHMY_KEY, eurythmyMap);
        }

        // write pulses data to the batch
        batch.update(sessionDoc, Session.PULSES_KEY, pulsesMap);

        // commit the batch
        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(LOG_TAG, "pulses saved!");
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

    private void cancelPulses() {
        Intent cancelIntent = new Intent();
        setResult(RESULT_CANCELED, cancelIntent);
        finish();
    }
}
