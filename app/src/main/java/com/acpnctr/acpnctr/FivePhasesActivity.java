package com.acpnctr.acpnctr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.acpnctr.acpnctr.models.Session;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

import static com.acpnctr.acpnctr.SessionActivity.sClientid;
import static com.acpnctr.acpnctr.SessionActivity.sSessionid;
import static com.acpnctr.acpnctr.SessionActivity.sUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_SESSIONS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

public class FivePhasesActivity extends AppCompatActivity {

    private static final String LOG_TAG = FivePhasesActivity.class.getSimpleName();

    RadioButton mWoodEarth;
    RadioButton mWoodMetal;
    RadioButton mFireMetal;
    RadioButton mFireWater;
    RadioButton mEarthWater;
    RadioButton mEarthWood;
    RadioButton mMetalWood;
    RadioButton mMetalFire;
    RadioButton mWaterFire;
    RadioButton mWaterEarth;

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_phases);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get reference to our views
        mWoodEarth = findViewById(R.id.wood_to_earth);
        mWoodMetal = findViewById(R.id.wood_to_metal);
        mFireMetal = findViewById(R.id.fire_to_metal);
        mFireWater = findViewById(R.id.fire_to_water);
        mEarthWater = findViewById(R.id.earth_to_water);
        mEarthWood = findViewById(R.id.earth_to_wood);
        mMetalWood = findViewById(R.id.metal_to_wood);
        mMetalFire = findViewById(R.id.metal_to_fire);
        mWaterFire = findViewById(R.id.water_to_fire);
        mWaterEarth = findViewById(R.id.water_to_earth);

        manageCheckboxesCombination();

        displayWuxingFromFirestore();
    }

    private void displayWuxingFromFirestore() {
        // create a reference to the firestore document holding data
        DocumentReference sessionDoc = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid);

        sessionDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null){
                    Log.w(LOG_TAG, "Listen failed" , e);
                }

                if (documentSnapshot != null && documentSnapshot.exists()){
                    Session session = documentSnapshot.toObject(Session.class);
                    Map<String, Boolean> wuxing = session.getWuxing();
                    if (wuxing != null) {
                        mWoodEarth.setChecked(wuxing.get(Session.WUXING_WOOD_TO_EARTH_KEY));
                        mWoodMetal.setChecked(wuxing.get(Session.WUXING_WOOD_TO_METAL_KEY));
                        mFireMetal.setChecked(wuxing.get(Session.WUXING_FIRE_TO_METAL_KEY));
                        mFireWater.setChecked(wuxing.get(Session.WUXING_FIRE_TO_WATER_KEY));
                        mEarthWater.setChecked(wuxing.get(Session.WUXING_EARTH_TO_WATER_KEY));
                        mEarthWood.setChecked(wuxing.get(Session.WUXING_EARTH_TO_WOOD_KEY));
                        mMetalWood.setChecked(wuxing.get(Session.WUXING_METAL_TO_WOOD_KEY));
                        mMetalFire.setChecked(wuxing.get(Session.WUXING_METAL_TO_FIRE_KEY));
                        mWaterFire.setChecked(wuxing.get(Session.WUXING_WATER_TO_FIRE_KEY));
                        mWaterEarth.setChecked(wuxing.get(Session.WUXING_WATER_TO_EARTH_KEY));
                    }
                } else {
                    Log.d(LOG_TAG, "Current data: null");
                }
            }
        });
    }

    private void manageCheckboxesCombination() {
        // ensure that when mWoodEarth is checked everything is unchecked but:
        // mWoodMetal and mWaterEarth
        mWoodEarth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mFireMetal.setChecked(false);
                    mFireWater.setChecked(false);
                    mEarthWater.setChecked(false);
                    mEarthWood.setChecked(false);
                    mMetalWood.setChecked(false);
                    mMetalFire.setChecked(false);
                    mWaterFire.setChecked(false);
                }
            }
        });

        // ensure that when mWoodMetal is checked everything is unchecked but:
        // mWoodEarth and mFireMetal
        mWoodMetal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mFireWater.setChecked(false);
                    mEarthWater.setChecked(false);
                    mEarthWood.setChecked(false);
                    mMetalWood.setChecked(false);
                    mMetalFire.setChecked(false);
                    mWaterFire.setChecked(false);
                    mWaterEarth.setChecked(false);
                }
            }
        });

        // ensure that when mFireMetal is checked everything is unchecked but:
        // mFireWater and mWoodMetal
        mFireMetal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mWoodEarth.setChecked(false);
                    mEarthWater.setChecked(false);
                    mEarthWood.setChecked(false);
                    mMetalWood.setChecked(false);
                    mMetalFire.setChecked(false);
                    mWaterFire.setChecked(false);
                    mWaterEarth.setChecked(false);
                }
            }
        });

        // ensure that when mFireWater is checked everything is unchecked but:
        // mFireMetal and mEarthWater
        mFireWater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mWoodEarth.setChecked(false);
                    mWoodMetal.setChecked(false);
                    mEarthWood.setChecked(false);
                    mMetalWood.setChecked(false);
                    mMetalFire.setChecked(false);
                    mWaterFire.setChecked(false);
                    mWaterEarth.setChecked(false);
                }
            }
        });

        // ensure that when mEarthWater is checked everything is unchecked but:
        // mEarthWood and mFireWater
        mEarthWater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mWoodEarth.setChecked(false);
                    mWoodMetal.setChecked(false);
                    mFireMetal.setChecked(false);
                    mMetalWood.setChecked(false);
                    mMetalFire.setChecked(false);
                    mWaterFire.setChecked(false);
                    mWaterEarth.setChecked(false);
                }
            }
        });

        // ensure that when mEarthWood is checked everything is unchecked but:
        // mEarthWater and mMetalWood
        mEarthWood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mWoodEarth.setChecked(false);
                    mWoodMetal.setChecked(false);
                    mFireMetal.setChecked(false);
                    mFireWater.setChecked(false);
                    mMetalFire.setChecked(false);
                    mWaterFire.setChecked(false);
                    mWaterEarth.setChecked(false);
                }
            }
        });

        // ensure that when mMetalWood is checked everything is unchecked but:
        // mMetalFire and mEarthWood
        mMetalWood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mWoodEarth.setChecked(false);
                    mWoodMetal.setChecked(false);
                    mFireMetal.setChecked(false);
                    mFireWater.setChecked(false);
                    mEarthWater.setChecked(false);
                    mWaterFire.setChecked(false);
                    mWaterEarth.setChecked(false);
                }
            }
        });

        // ensure that when mMetalFire is checked everything is unchecked but:
        // mMetalWood and mWaterFire
        mMetalFire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mWoodEarth.setChecked(false);
                    mWoodMetal.setChecked(false);
                    mFireMetal.setChecked(false);
                    mFireWater.setChecked(false);
                    mEarthWater.setChecked(false);
                    mEarthWood.setChecked(false);
                    mWaterEarth.setChecked(false);
                }
            }
        });

        // ensure that when mWaterFire is checked everything is unchecked but:
        // mMetalFire and mWaterEarth
        mWaterFire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mWoodEarth.setChecked(false);
                    mWoodMetal.setChecked(false);
                    mFireMetal.setChecked(false);
                    mFireWater.setChecked(false);
                    mEarthWater.setChecked(false);
                    mEarthWood.setChecked(false);
                    mMetalWood.setChecked(false);
                }
            }
        });

        // ensure that when mWaterEarth is checked everything is unchecked but:
        // mWoodEarth and mWaterFire
        mWaterEarth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mWoodMetal.setChecked(false);
                    mFireMetal.setChecked(false);
                    mFireWater.setChecked(false);
                    mEarthWater.setChecked(false);
                    mEarthWood.setChecked(false);
                    mMetalWood.setChecked(false);
                    mMetalFire.setChecked(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.five_phases, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                cancelWuxing();
                return true;
            case R.id.action_save:
                returnWuxing();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void returnWuxing() {
        Intent returnIntent = new Intent();
        // store checkboxes' state into a bundle
        Bundle wuxingBundle = new Bundle();
        wuxingBundle.putBoolean(Session.WUXING_WOOD_TO_EARTH_KEY, mWoodEarth.isChecked());
        wuxingBundle.putBoolean(Session.WUXING_WOOD_TO_METAL_KEY, mWoodMetal.isChecked());
        wuxingBundle.putBoolean(Session.WUXING_FIRE_TO_METAL_KEY, mFireMetal.isChecked());
        wuxingBundle.putBoolean(Session.WUXING_FIRE_TO_WATER_KEY, mFireWater.isChecked());
        wuxingBundle.putBoolean(Session.WUXING_EARTH_TO_WATER_KEY, mEarthWater.isChecked());
        wuxingBundle.putBoolean(Session.WUXING_EARTH_TO_WOOD_KEY, mEarthWood.isChecked());
        wuxingBundle.putBoolean(Session.WUXING_METAL_TO_WOOD_KEY, mMetalWood.isChecked());
        wuxingBundle.putBoolean(Session.WUXING_METAL_TO_FIRE_KEY, mMetalFire.isChecked());
        wuxingBundle.putBoolean(Session.WUXING_WATER_TO_FIRE_KEY, mWaterFire.isChecked());
        wuxingBundle.putBoolean(Session.WUXING_WATER_TO_EARTH_KEY, mWaterEarth.isChecked());
        returnIntent.putExtras(wuxingBundle);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void cancelWuxing() {
        Intent cancelIntent = new Intent();
        setResult(RESULT_CANCELED, cancelIntent);
        finish();
    }
}
