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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.acpnctr.acpnctr.adapters.PulseTypesAdapter;
import com.acpnctr.acpnctr.models.PulseType;
import com.acpnctr.acpnctr.utils.AcpnctrUtil;
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

import java.util.ArrayList;
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

    private static final String LOG_TAG = PulsesActivity.class.getSimpleName();

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // create an array of PulseType object for the 28 types of pulses
    private PulseType[] pulseTypes = AcpnctrUtil.createPulseTypesArray();

    // Member variables
    // ... Eurythmy category
    private CheckBox mEurythmyCheckbox;
    private EditText mBeatPerMin;
    private EditText mBreathPerMin;
    private TextView mBeatPerBreath;
    private Group mEurythmyGroup;
    // ... 28 types category
    private CheckBox m28TypesCheckbox;
    private GridView m28TypesContainer;
    private Map<String, Boolean> m28TypesMap;
    private PulseTypesAdapter mPulseTypesAdapter;

    // Final strings to store state information
    private static final String PULSE_TYPES_SELECTED_KEY = "pulse_types_selected";
    private static final String IS_EURYTHMY_CHECKED_KEY = "eurythmy_checked";
    private static final String IS_PULSE_TYPES_CHECKED_KEY = "pulse_types_checked";


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
        m28TypesCheckbox = findViewById(R.id.cb_28_types);
        m28TypesContainer = findViewById(R.id.gv_28_types);

        if (savedInstanceState != null) {
            // restore pulse types Array and Hashmap
            ArrayList<String> selectedPulseTypes = savedInstanceState.getStringArrayList(PULSE_TYPES_SELECTED_KEY);
            m28TypesMap = new HashMap<>();
            for (String key: selectedPulseTypes){
                for (PulseType pulseType: pulseTypes) {
                    if (pulseType.getKey().equals(key)) {
                        pulseType.setSelected(true);
                        break;
                    }
                }
                m28TypesMap.put(key, true);
            }

            // restore checkboxes' states
            mEurythmyCheckbox.setChecked(savedInstanceState.getBoolean(IS_EURYTHMY_CHECKED_KEY));
            m28TypesCheckbox.setChecked(savedInstanceState.getBoolean(IS_PULSE_TYPES_CHECKED_KEY));
            if (savedInstanceState.getBoolean(IS_EURYTHMY_CHECKED_KEY)){
                showEurythmy();
            } else hideEurythmy();
            if (savedInstanceState.getBoolean(IS_PULSE_TYPES_CHECKED_KEY)){
                show28Types();
            } else hide28Types();

        } else {
            // initialize data for pulses activity
            initializePulsesUI();
        }

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

        // Listen to 28 Types checkbox and hide or show accordingly
        m28TypesCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    hide28Types();
                } else {
                    show28Types();
                }
            }
        });

        // compute Beat per Breath when the user has types both values
        setEurythmyListenerAndCalculate();

        // create a custom adapter and set it the the gridview for 28 types
        mPulseTypesAdapter = new PulseTypesAdapter(this, pulseTypes);
        m28TypesContainer.setAdapter(mPulseTypesAdapter);
        // set click handler for selection
        m28TypesContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // get the PulseType object that is clicked on
                PulseType pulseType = pulseTypes[position];
                // change selection state
                pulseType.toggleSelected();
                // if the state is selected add it to the Map
                if (pulseType.isSelected()){
                    m28TypesMap.put(pulseType.getKey(), true);
                }
                // if it's not selected but has been selected remove from the Map
                else if (m28TypesMap.containsKey(pulseType.getKey())){
                    m28TypesMap.remove(pulseType.getKey());
                }
                // notify the adapter of the state change
                mPulseTypesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void show28Types() {
        m28TypesContainer.setVisibility(View.VISIBLE);
    }

    private void hide28Types() {
        m28TypesContainer.setVisibility(View.GONE);
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
                            Map<String, Object> pulsesMap = (Map<String, Object>) sessionMap.get(Constants.PULSES_KEY);
                            if (pulsesMap != null){

                                // get eurythmy data if any
                                Map<String, String> eurythmyMap = (Map<String, String>) pulsesMap.get(Constants.PULSES_EURYTHMY_KEY);
                                if (eurythmyMap != null) {
                                    if (eurythmyMap.containsKey(Constants.PULSES_EURYTHMY_BEAT_KEY)) {
                                        mBeatPerMin.setText(eurythmyMap.get(Constants.PULSES_EURYTHMY_BEAT_KEY));
                                    }
                                    if (eurythmyMap.containsKey(Constants.PULSES_EURYTHMY_BREATH_KEY)) {
                                        mBreathPerMin.setText(eurythmyMap.get(Constants.PULSES_EURYTHMY_BREATH_KEY));
                                    }
                                    if (eurythmyMap.containsKey(Constants.PULSES_EURYTHMY_BPB_KEY)) {
                                        mBeatPerBreath.setText(eurythmyMap.get(Constants.PULSES_EURYTHMY_BPB_KEY));
                                    }
                                    mEurythmyCheckbox.setChecked(true);
                                } else {
                                    Log.d(LOG_TAG, "No Eurythmy data yet!");
                                    hideEurythmy();
                                }

                                // get 28 types of pulses data
                                m28TypesMap = (Map<String, Boolean>) pulsesMap.get(Constants.PULSES_28_TYPES_KEY);
                                if (m28TypesMap != null){
                                    for (Map.Entry<String, Boolean> entry: m28TypesMap.entrySet()){
                                        for (PulseType pulseType: pulseTypes){
                                            if (pulseType.getKey().equals(entry.getKey())){
                                                pulseType.setSelected(true);
                                                break;
                                            }
                                        }
                                    }
                                    m28TypesCheckbox.setChecked(true);
                                    mPulseTypesAdapter.notifyDataSetChanged();

                                } else {
                                    Log.d(LOG_TAG, "No 28 Pulse Types data yet!");
                                    hide28Types();
                                    // initialize 28 types hashmap to be able to receive data
                                    m28TypesMap = new HashMap<>();
                                }


                            } else {
                                Log.d(LOG_TAG, "No pulses data yet!");
                                hideEurythmy();
                                hide28Types();
                                // initialize 28 types hashmap to be able to receive data
                                m28TypesMap = new HashMap<>();
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
        // create a hashmap to store data for pulses fetched from UI or member variable
        Map<String, Object> pulsesMap = new HashMap<>();

        // add eurythmy data to the batch if checkbox is checked
        if (mEurythmyCheckbox.isChecked()) {
            if (!mBeatPerMin.getText().toString().isEmpty() && !mBreathPerMin.getText().toString().isEmpty()) {
                // create a hashmap to store eurythmy data
                Map<String, String> eurythmyMap = new HashMap<>();
                eurythmyMap.put(Constants.PULSES_EURYTHMY_BEAT_KEY, mBeatPerMin.getText().toString().trim());
                eurythmyMap.put(Constants.PULSES_EURYTHMY_BREATH_KEY, mBreathPerMin.getText().toString().trim());
                eurythmyMap.put(Constants.PULSES_EURYTHMY_BPB_KEY, mBeatPerBreath.getText().toString().trim());
                // add this hashmap to main pulses hashmap
                pulsesMap.put(Constants.PULSES_EURYTHMY_KEY, eurythmyMap);
            } else {
                Toast.makeText(this, getString(R.string.pulses_eurythmy_no_value), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // add 28 types of pulse data to the batch if checkbox is checked
        if (m28TypesCheckbox.isChecked()) {
            // add 28 types hashmap with selected types of pulse to main pulses hashmap if not empty
            if (!m28TypesMap.isEmpty()){
                pulsesMap.put(Constants.PULSES_28_TYPES_KEY, m28TypesMap);
            }
        }

        // write pulses data to the batch
        batch.update(sessionDoc, Constants.PULSES_KEY, pulsesMap);

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // construct a list of pulse types selected
        final ArrayList<String> selectedPulseTypes = new ArrayList<>();
        for (PulseType type: pulseTypes){
            if (type.isSelected()){
                selectedPulseTypes.add(type.getKey());
            }
        }

        outState.putStringArrayList(PULSE_TYPES_SELECTED_KEY, selectedPulseTypes);
        outState.putBoolean(IS_EURYTHMY_CHECKED_KEY, mEurythmyCheckbox.isChecked());
        outState.putBoolean(IS_PULSE_TYPES_CHECKED_KEY, m28TypesCheckbox.isChecked());
    }
}
