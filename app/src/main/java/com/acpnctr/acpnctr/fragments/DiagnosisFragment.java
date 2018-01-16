package com.acpnctr.acpnctr.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.acpnctr.acpnctr.FivePhasesActivity;
import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Session;
import com.acpnctr.acpnctr.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.acpnctr.acpnctr.SessionActivity.isNewSession;
import static com.acpnctr.acpnctr.SessionActivity.sClientid;
import static com.acpnctr.acpnctr.SessionActivity.sSessionid;
import static com.acpnctr.acpnctr.SessionActivity.sUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_SESSIONS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

/**
 * {@link Fragment} to display diagnosis of a session.
 */
public class DiagnosisFragment extends Fragment {

    private static final String LOG_TAG = DiagnosisFragment.class.getSimpleName();

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Get a new write batch
    private WriteBatch mBatch = db.batch();

    // Keys to store state information
    private static final String IS_BAGANG_SHOWN = "is_bagang_shown";
    private static final String BAGANG_YIN = "is_bagang_yin";
    private static final String BAGANG_YANG = "is_bagang_yang";
    private static final String BAGANG_DEFICIENCY = "is_bagang_deficiency";
    private static final String BAGANG_EXCESS = "is_bagang_excess";
    private static final String BAGANG_COLD  = "is_bagang_cold";
    private static final String BAGANG_HEAT = "is_bagang_heat";
    private static final String BAGANG_INTERIOR  = "is_bagang_interior";
    private static final String BAGANG_EXTERIOR  = "is_bagang_exterior";

    // Member variables
    private Button mBagangBtn;
    private RadioButton mBagangYin;
    private RadioButton mBagangYang;
    private RadioButton mBagangDeficiency;
    private RadioButton mBagangExcess;
    private RadioButton mBagangCold;
    private RadioButton mBagangHeat;
    private RadioButton mBagangInterior;
    private RadioButton mBagangExterior;

    private Button mWuxingBtn;

    // Flags
    private boolean isBagangShown;

    // Request code for result from activity
    public static final int WUXING_REQUEST = 1;

    public DiagnosisFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_diagnosis, container, false);

        // get reference to our layout views
        mBagangBtn = rootView.findViewById(R.id.btn_add_bagang);
        mBagangYin = rootView.findViewById(R.id.rb_bagang_yin);
        mBagangYang = rootView.findViewById(R.id.rb_bagang_yang);
        mBagangDeficiency = rootView.findViewById(R.id.rb_bagang_deficiency);
        mBagangExcess = rootView.findViewById(R.id.rb_bagang_excess);
        mBagangCold = rootView.findViewById(R.id.rb_bagang_cold);
        mBagangHeat = rootView.findViewById(R.id.rb_bagang_heat);
        mBagangInterior = rootView.findViewById(R.id.rb_bagang_interior);
        mBagangExterior = rootView.findViewById(R.id.rb_bagang_exterior);

        mWuxingBtn = rootView.findViewById(R.id.btn_add_wu_xing);

        if (savedInstanceState != null){
            // BAGANG
            isBagangShown = savedInstanceState.getBoolean(IS_BAGANG_SHOWN);
            mBagangYin.setChecked(savedInstanceState.getBoolean(BAGANG_YIN));
            mBagangYang.setChecked(savedInstanceState.getBoolean(BAGANG_YANG));
            mBagangDeficiency.setChecked(savedInstanceState.getBoolean(BAGANG_DEFICIENCY));
            mBagangExcess.setChecked(savedInstanceState.getBoolean(BAGANG_EXCESS));
            mBagangCold.setChecked(savedInstanceState.getBoolean(BAGANG_COLD));
            mBagangHeat.setChecked(savedInstanceState.getBoolean(BAGANG_HEAT));
            mBagangInterior.setChecked(savedInstanceState.getBoolean(BAGANG_INTERIOR));
            mBagangExterior.setChecked(savedInstanceState.getBoolean(BAGANG_EXTERIOR));
            if (isBagangShown) {
                showBagang();
            } else {
                hideBagang();
            }
        } else {
            initializeScreen();
        }

        // [START - SET UP BUTTONS FOR THE DIFFERENT SYSTEMS OF DIAGNOSIS]
        // Bagang (8 règles thérapeuthiques)
        mBagangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBagangShown) {
                    showBagang();
                } else {
                    hideBagang();
                }
            }
        });

        // Wuxing (5 phases)
        mWuxingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewSession) {
                    // create a new intent to start 5 phases activity
                    Intent fivePhasesIntent = new Intent(getActivity(), FivePhasesActivity.class);
                    startActivityForResult(fivePhasesIntent, WUXING_REQUEST);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.session_not_saved),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        // [END - SET UP BUTTONS FOR THE DIFFERENT SYSTEMS OF DIAGNOSIS]

        return rootView;
    }

    private void initializeScreen() {
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(Constants.INTENT_SELECTED_SESSION_ID)){
            Session selectedSession = intent.getParcelableExtra(Constants.INTENT_SELECTED_SESSION);
            // Init Bagang
            Map<String, Boolean> bagang = selectedSession.getBagang();
            if (!bagang.isEmpty()) {
                showBagang();
                mBagangYin.setChecked(bagang.get(Session.BAGANG_YIN_KEY));
                mBagangYang.setChecked(bagang.get(Session.BAGANG_YANG_KEY));
                mBagangDeficiency.setChecked(bagang.get(Session.BAGANG_DEFICIENCY_KEY));
                mBagangExcess.setChecked(bagang.get(Session.BAGANG_EXCESS_KEY));
                mBagangCold.setChecked(bagang.get(Session.BAGANG_COLD_KEY));
                mBagangHeat.setChecked(bagang.get(Session.BAGANG_HEAT_KEY));
                mBagangInterior.setChecked(bagang.get(Session.BAGANG_INTERIOR_KEY));
                mBagangExterior.setChecked(bagang.get(Session.BAGANG_EXTERIOR_KEY));
            } else {
                hideBagang();
            }
            // init wuxing
            Map<String, Boolean> wuxing = selectedSession.getWuxing();
            if (!wuxing.isEmpty()) {
                Bundle wuxingBundle = new Bundle();
                for (Map.Entry<String, Boolean> entry : wuxing.entrySet()) {
                    wuxingBundle.putBoolean(entry.getKey(), entry.getValue());
                }
                setWuxingImage(wuxingBundle);
            }
        } else {
            hideBagang();
        }
    }

    private void hideBagang() {
        mBagangYin.setVisibility(View.GONE);
        mBagangYang.setVisibility(View.GONE);
        mBagangDeficiency.setVisibility(View.GONE);
        mBagangExcess.setVisibility(View.GONE);
        mBagangCold.setVisibility(View.GONE);
        mBagangHeat.setVisibility(View.GONE);
        mBagangInterior.setVisibility(View.GONE);
        mBagangExterior.setVisibility(View.GONE);

        isBagangShown = false;
    }

    private void showBagang() {
        mBagangYin.setVisibility(View.VISIBLE);
        mBagangYang.setVisibility(View.VISIBLE);
        mBagangDeficiency.setVisibility(View.VISIBLE);
        mBagangExcess.setVisibility(View.VISIBLE);
        mBagangCold.setVisibility(View.VISIBLE);
        mBagangHeat.setVisibility(View.VISIBLE);
        mBagangInterior.setVisibility(View.VISIBLE);
        mBagangExterior.setVisibility(View.VISIBLE);

        isBagangShown = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Toast to ask the user to save the session first
        Toast toastSessionNotSaved = Toast.makeText(getActivity(), getString(R.string.session_not_saved),
                Toast.LENGTH_SHORT);
        switch(id){
            case R.id.action_save:
                if (!isNewSession){
                    saveDiagnosis();
                } else {
                    toastSessionNotSaved.show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasBagangBeenEdited() {
        return !(mBagangYin.isChecked() == mBagangYang.isChecked()
                && mBagangDeficiency.isChecked() == mBagangExcess.isChecked()
                && mBagangCold.isChecked() == mBagangHeat.isChecked()
                && mBagangInterior.isChecked() == mBagangExterior.isChecked());
    }

    private void saveDiagnosis() {
        // build the firestore path
        DocumentReference sessionForDiag = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid);

        // add Bagang to the batch if it has been edited
        if (hasBagangBeenEdited()) {
            // add data to a HashMap
            Map<String, Boolean> bagang = new HashMap<>();
            bagang.put(Session.BAGANG_YIN_KEY, mBagangYin.isChecked());
            bagang.put(Session.BAGANG_YANG_KEY, mBagangYang.isChecked());
            bagang.put(Session.BAGANG_DEFICIENCY_KEY, mBagangDeficiency.isChecked());
            bagang.put(Session.BAGANG_EXCESS_KEY, mBagangExcess.isChecked());
            bagang.put(Session.BAGANG_COLD_KEY, mBagangCold.isChecked());
            bagang.put(Session.BAGANG_HEAT_KEY, mBagangHeat.isChecked());
            bagang.put(Session.BAGANG_INTERIOR_KEY, mBagangInterior.isChecked());
            bagang.put(Session.BAGANG_EXTERIOR_KEY, mBagangExterior.isChecked());

            mBatch.update(sessionForDiag, Session.BAGANG_KEY, bagang);
        }

        // Commit the batch (if and only if at least one system has been edited)
        if (hasBagangBeenEdited()) {
            mBatch.commit()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(LOG_TAG, "batch successfully committed !");
                            Toast.makeText(getContext(), getString(R.string.diag_saved), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(LOG_TAG, "Error committing batch :/ ", e);
                        }
                    });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // BAGANG
        outState.putBoolean(IS_BAGANG_SHOWN, isBagangShown);
        outState.putBoolean(BAGANG_YIN, mBagangYin.isChecked());
        outState.putBoolean(BAGANG_YANG, mBagangYang.isChecked());
        outState.putBoolean(BAGANG_DEFICIENCY, mBagangDeficiency.isChecked());
        outState.putBoolean(BAGANG_EXCESS, mBagangExcess.isChecked());
        outState.putBoolean(BAGANG_COLD, mBagangCold.isChecked());
        outState.putBoolean(BAGANG_HEAT, mBagangHeat.isChecked());
        outState.putBoolean(BAGANG_INTERIOR, mBagangInterior.isChecked());
        outState.putBoolean(BAGANG_EXTERIOR, mBagangExterior.isChecked());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WUXING_REQUEST) {
            if (resultCode == RESULT_OK) {
                // build the firestore path
                DocumentReference sessionForDiag = db.collection(FIRESTORE_COLLECTION_USERS)
                        .document(sUid)
                        .collection(FIRESTORE_COLLECTION_CLIENTS)
                        .document(sClientid)
                        .collection(FIRESTORE_COLLECTION_SESSIONS)
                        .document(sSessionid);
                // get data from itent and store them in a bundle
                Bundle b = data.getExtras();
                // create a hashmap and put data to be send to firestore
                Map<String, Boolean> wuxingMap = new HashMap<>();
                wuxingMap.put(Session.WUXING_WOOD_TO_EARTH_KEY, b.getBoolean(Session.WUXING_WOOD_TO_EARTH_KEY));
                wuxingMap.put(Session.WUXING_WOOD_TO_METAL_KEY, b.getBoolean(Session.WUXING_WOOD_TO_METAL_KEY));
                wuxingMap.put(Session.WUXING_FIRE_TO_METAL_KEY, b.getBoolean(Session.WUXING_FIRE_TO_METAL_KEY));
                wuxingMap.put(Session.WUXING_FIRE_TO_WATER_KEY, b.getBoolean(Session.WUXING_FIRE_TO_WATER_KEY));
                wuxingMap.put(Session.WUXING_EARTH_TO_WATER_KEY, b.getBoolean(Session.WUXING_EARTH_TO_WATER_KEY));
                wuxingMap.put(Session.WUXING_EARTH_TO_WOOD_KEY, b.getBoolean(Session.WUXING_EARTH_TO_WOOD_KEY));
                wuxingMap.put(Session.WUXING_METAL_TO_WOOD_KEY, b.getBoolean(Session.WUXING_METAL_TO_WOOD_KEY));
                wuxingMap.put(Session.WUXING_METAL_TO_FIRE_KEY, b.getBoolean(Session.WUXING_METAL_TO_FIRE_KEY));
                wuxingMap.put(Session.WUXING_WATER_TO_FIRE_KEY, b.getBoolean(Session.WUXING_WATER_TO_FIRE_KEY));
                wuxingMap.put(Session.WUXING_WATER_TO_EARTH_KEY, b.getBoolean(Session.WUXING_WATER_TO_EARTH_KEY));
                // add the hashmap to the batch
                mBatch.update(sessionForDiag, Session.WUXING_KEY, wuxingMap);
                // set the corresponding image for the wuxing diagnosis
                setWuxingImage(b);
            }
        }
    }

    private void setWuxingImage(Bundle bundle) {
        // if wood attacks earth AND wood attacks metal
        if (bundle.getBoolean(Session.WUXING_WOOD_TO_EARTH_KEY) &&
                bundle.getBoolean(Session.WUXING_WOOD_TO_METAL_KEY)){
            Log.d(LOG_TAG, "wood attacks earth AND wood attacks metal");

        }
        // if only wood attacks earth
        else if (bundle.getBoolean(Session.WUXING_WOOD_TO_EARTH_KEY) &&
                !bundle.getBoolean(Session.WUXING_WOOD_TO_METAL_KEY) &&
                !bundle.getBoolean(Session.WUXING_WATER_TO_EARTH_KEY)){
            Log.d(LOG_TAG, "wood attacks earth");

        }
        // if only wood attacks metal
        else if (!bundle.getBoolean(Session.WUXING_WOOD_TO_EARTH_KEY) &&
                bundle.getBoolean(Session.WUXING_WOOD_TO_METAL_KEY) &&
                !bundle.getBoolean(Session.WUXING_FIRE_TO_METAL_KEY)){
            Log.d(LOG_TAG, "wood attacks metal");

        }
        // if wood attacks metal AND fire attacks metal
        else if (bundle.getBoolean(Session.WUXING_WOOD_TO_METAL_KEY) &&
                bundle.getBoolean(Session.WUXING_FIRE_TO_METAL_KEY)){
            Log.d(LOG_TAG, "wood attacks metal AND fire attacks metal");

        }
        // if only fire attacks metal
        else if (bundle.getBoolean(Session.WUXING_FIRE_TO_METAL_KEY) &&
                !bundle.getBoolean(Session.WUXING_FIRE_TO_WATER_KEY) &&
                !bundle.getBoolean(Session.WUXING_WOOD_TO_METAL_KEY)){
            Log.d(LOG_TAG, "fire attacks metal");

        }
        // if only fire attacks water
        else if (!bundle.getBoolean(Session.WUXING_FIRE_TO_METAL_KEY) &&
                bundle.getBoolean(Session.WUXING_FIRE_TO_WATER_KEY) &&
                !bundle.getBoolean(Session.WUXING_EARTH_TO_WATER_KEY)){
            Log.d(LOG_TAG, "fire attacks water");

        }
        // if fire attacks metal AND fire attacks water
        else if (bundle.getBoolean(Session.WUXING_FIRE_TO_METAL_KEY) &&
                bundle.getBoolean(Session.WUXING_FIRE_TO_WATER_KEY)){
            Log.d(LOG_TAG, "fire attacks metal AND fire attacks water");

        }
        // if only earth attacks water
        else if (bundle.getBoolean(Session.WUXING_EARTH_TO_WATER_KEY) &&
                !bundle.getBoolean(Session.WUXING_EARTH_TO_WOOD_KEY) &&
                !bundle.getBoolean(Session.WUXING_FIRE_TO_WATER_KEY)){
            Log.d(LOG_TAG, "earth attacks water");

        }
        // if only earth attacks wood
        else if (!bundle.getBoolean(Session.WUXING_EARTH_TO_WATER_KEY) &&
                bundle.getBoolean(Session.WUXING_EARTH_TO_WOOD_KEY) &&
                !bundle.getBoolean(Session.WUXING_METAL_TO_WOOD_KEY)){
            Log.d(LOG_TAG, "earth attacks wood");

        }
        // if earth attacks water AND wood
        else if (bundle.getBoolean(Session.WUXING_EARTH_TO_WATER_KEY) &&
                bundle.getBoolean(Session.WUXING_EARTH_TO_WOOD_KEY)){
            Log.d(LOG_TAG, "earth attacks water AND wood");

        }
        // if earth attacks water AND fire attacks water
        else if (bundle.getBoolean(Session.WUXING_EARTH_TO_WATER_KEY) &&
                bundle.getBoolean(Session.WUXING_FIRE_TO_WATER_KEY)){
            Log.d(LOG_TAG, "earth attacks water AND fire attacks water");

        }
        // if only metal attacks wood
        else if (bundle.getBoolean(Session.WUXING_METAL_TO_WOOD_KEY) &&
                !bundle.getBoolean(Session.WUXING_METAL_TO_FIRE_KEY) &&
                !bundle.getBoolean(Session.WUXING_EARTH_TO_WOOD_KEY)){
            Log.d(LOG_TAG, "metal attacks wood");

        }
        // if only metal attacks fire
        else if (!bundle.getBoolean(Session.WUXING_METAL_TO_WOOD_KEY) &&
                bundle.getBoolean(Session.WUXING_METAL_TO_FIRE_KEY) &&
                !bundle.getBoolean(Session.WUXING_WATER_TO_FIRE_KEY)){
            Log.d(LOG_TAG, "metal attacks fire");

        }
        // if metal attacks fire AND wood
        else if (bundle.getBoolean(Session.WUXING_METAL_TO_WOOD_KEY) &&
                bundle.getBoolean(Session.WUXING_METAL_TO_FIRE_KEY)){
            Log.d(LOG_TAG, "metal attacks fire AND wood");

        }
        // if metal attacks wood and earth attacks wood
        else if (bundle.getBoolean(Session.WUXING_METAL_TO_WOOD_KEY) &&
                bundle.getBoolean(Session.WUXING_EARTH_TO_WOOD_KEY)){
            Log.d(LOG_TAG, "metal attacks wood and earth attacks wood");

        }
        // if only water attacks fire
        else if (bundle.getBoolean(Session.WUXING_WATER_TO_FIRE_KEY) &&
                !bundle.getBoolean(Session.WUXING_WATER_TO_EARTH_KEY) &&
                !bundle.getBoolean(Session.WUXING_METAL_TO_FIRE_KEY)){
            Log.d(LOG_TAG, "water attacks fire");

        }
        // if only water attacks earth
        else if (!bundle.getBoolean(Session.WUXING_WATER_TO_FIRE_KEY) &&
                bundle.getBoolean(Session.WUXING_WATER_TO_EARTH_KEY) &&
                !bundle.getBoolean(Session.WUXING_WOOD_TO_EARTH_KEY)){
            Log.d(LOG_TAG, "water attacks earth");

        }
        // if water attacks fire AND earth
        else if (bundle.getBoolean(Session.WUXING_WATER_TO_FIRE_KEY) &&
                bundle.getBoolean(Session.WUXING_WATER_TO_EARTH_KEY)){
            Log.d(LOG_TAG, "water attacks fire AND earth");

        }
        // if water attacks fire AND metal attacks fire
        else if (bundle.getBoolean(Session.WUXING_WATER_TO_FIRE_KEY) &&
                bundle.getBoolean(Session.WUXING_METAL_TO_FIRE_KEY)){
            Log.d(LOG_TAG, "water attacks fire AND metal attacks fire");

        }
        // if water attacks earth AND wood attacks earth
        else if (bundle.getBoolean(Session.WUXING_WATER_TO_EARTH_KEY) &&
                bundle.getBoolean(Session.WUXING_WOOD_TO_EARTH_KEY)){
            Log.d(LOG_TAG, "water attacks earth AND wood attacks earth");
        }
    }
}
