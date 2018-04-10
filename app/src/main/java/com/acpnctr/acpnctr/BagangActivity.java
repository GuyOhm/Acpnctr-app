package com.acpnctr.acpnctr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.acpnctr.acpnctr.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.acpnctr.acpnctr.SessionActivity.sClientid;
import static com.acpnctr.acpnctr.SessionActivity.sSessionid;
import static com.acpnctr.acpnctr.SessionActivity.sUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_SESSIONS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

public class BagangActivity extends AppCompatActivity {

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Keys to store state information
    private static final String BAGANG_YIN = "is_bagang_yin";
    private static final String BAGANG_YANG = "is_bagang_yang";
    private static final String BAGANG_DEFICIENCY = "is_bagang_deficiency";
    private static final String BAGANG_EXCESS = "is_bagang_excess";
    private static final String BAGANG_COLD  = "is_bagang_cold";
    private static final String BAGANG_HEAT = "is_bagang_heat";
    private static final String BAGANG_INTERIOR  = "is_bagang_interior";
    private static final String BAGANG_EXTERIOR  = "is_bagang_exterior";

    // Get references to views
    @BindView(R.id.rb_bagang_yin) RadioButton mBagangYin;
    @BindView(R.id.rb_bagang_yang) RadioButton mBagangYang;
    @BindView(R.id.rb_bagang_deficiency) RadioButton mBagangDeficiency;
    @BindView(R.id.rb_bagang_excess) RadioButton mBagangExcess;
    @BindView(R.id.rb_bagang_cold) RadioButton mBagangCold;
    @BindView(R.id.rb_bagang_heat) RadioButton mBagangHeat;
    @BindView(R.id.rb_bagang_interior) RadioButton mBagangInterior;
    @BindView(R.id.rb_bagang_exterior) RadioButton mBagangExterior;
    @BindView(R.id.rg_bagang_yin_yang) RadioGroup mYinYangGroup;
    @BindView(R.id.rg_bagang_def_excess) RadioGroup mDefExcessGroup;
    @BindView(R.id.rg_bagang_cold_heat) RadioGroup mColdHeatGroup;
    @BindView(R.id.rg_bagang_int_ext) RadioGroup mIntExtGroup;
    @BindView(R.id.btn_reset_bagang) Button mResetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bagang);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            // Restore radio buttons states
            mBagangYin.setChecked(savedInstanceState.getBoolean(BAGANG_YIN));
            mBagangYang.setChecked(savedInstanceState.getBoolean(BAGANG_YANG));
            mBagangDeficiency.setChecked(savedInstanceState.getBoolean(BAGANG_DEFICIENCY));
            mBagangExcess.setChecked(savedInstanceState.getBoolean(BAGANG_EXCESS));
            mBagangCold.setChecked(savedInstanceState.getBoolean(BAGANG_COLD));
            mBagangHeat.setChecked(savedInstanceState.getBoolean(BAGANG_HEAT));
            mBagangInterior.setChecked(savedInstanceState.getBoolean(BAGANG_INTERIOR));
            mBagangExterior.setChecked(savedInstanceState.getBoolean(BAGANG_EXTERIOR));
        } else {
            // Load data from Firestore
            displayBagangFromFirestore();
        }

        // Set up reset button
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBagang();
            }
        });
    }

    private void resetBagang() {
        mYinYangGroup.clearCheck();
        mDefExcessGroup.clearCheck();
        mColdHeatGroup.clearCheck();
        mIntExtGroup.clearCheck();
    }

    private void displayBagangFromFirestore() {
        // Build the firestore path
        DocumentReference sessionDoc = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid);

        sessionDoc.addSnapshotListener(BagangActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(BagangActivity.this,
                            R.string.generic_data_load_failed, Toast.LENGTH_SHORT).show();
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Map<String, Boolean> bagang = (Map<String, Boolean>) documentSnapshot.getData()
                            .get(Constants.BAGANG_KEY);
                    if (bagang != null) {
                        mBagangYin.setChecked(bagang.get(Constants.BAGANG_YIN_KEY));
                        mBagangYang.setChecked(bagang.get(Constants.BAGANG_YANG_KEY));
                        mBagangDeficiency.setChecked(bagang.get(Constants.BAGANG_DEFICIENCY_KEY));
                        mBagangExcess.setChecked(bagang.get(Constants.BAGANG_EXCESS_KEY));
                        mBagangCold.setChecked(bagang.get(Constants.BAGANG_COLD_KEY));
                        mBagangHeat.setChecked(bagang.get(Constants.BAGANG_HEAT_KEY));
                        mBagangInterior.setChecked(bagang.get(Constants.BAGANG_INTERIOR_KEY));
                        mBagangExterior.setChecked(bagang.get(Constants.BAGANG_EXTERIOR_KEY));
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bagang, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                cancelBagang();
                return true;
            case R.id.action_save:
                saveBagang();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelBagang() {
        Intent cancelIntent = new Intent();
        setResult(RESULT_CANCELED, cancelIntent);
        finish();
    }

    private void saveBagang() {

        // Build the firestore path
        DocumentReference sessionDoc = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid);

        // Add data to a HashMap
        Map<String, Boolean> bagang = new HashMap<>();
        bagang.put(Constants.BAGANG_YIN_KEY, mBagangYin.isChecked());
        bagang.put(Constants.BAGANG_YANG_KEY, mBagangYang.isChecked());
        bagang.put(Constants.BAGANG_DEFICIENCY_KEY, mBagangDeficiency.isChecked());
        bagang.put(Constants.BAGANG_EXCESS_KEY, mBagangExcess.isChecked());
        bagang.put(Constants.BAGANG_COLD_KEY, mBagangCold.isChecked());
        bagang.put(Constants.BAGANG_HEAT_KEY, mBagangHeat.isChecked());
        bagang.put(Constants.BAGANG_INTERIOR_KEY, mBagangInterior.isChecked());
        bagang.put(Constants.BAGANG_EXTERIOR_KEY, mBagangExterior.isChecked());

        // Save data to firestore session document
        sessionDoc.update(Constants.BAGANG_KEY, bagang)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BagangActivity.this,
                                R.string.generic_data_insert_failed, Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BAGANG_YIN, mBagangYin.isChecked());
        outState.putBoolean(BAGANG_YANG, mBagangYang.isChecked());
        outState.putBoolean(BAGANG_DEFICIENCY, mBagangDeficiency.isChecked());
        outState.putBoolean(BAGANG_EXCESS, mBagangExcess.isChecked());
        outState.putBoolean(BAGANG_COLD, mBagangCold.isChecked());
        outState.putBoolean(BAGANG_HEAT, mBagangHeat.isChecked());
        outState.putBoolean(BAGANG_INTERIOR, mBagangInterior.isChecked());
        outState.putBoolean(BAGANG_EXTERIOR, mBagangExterior.isChecked());
    }
}
