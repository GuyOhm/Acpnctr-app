package com.acpnctr.acpnctr.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.acpnctr.acpnctr.BagangActivity;
import com.acpnctr.acpnctr.FivePhasesActivity;
import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.utils.Constants;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Request code for result from activity
    private static final int WUXING_REQUEST = 1;
    private static final int BAGANG_REQUEST = 2;

    // Get references to layout views
    @BindView(R.id.btn_add_bagang) Button mBagangBtn;
    @BindView(R.id.rg_diag_bagang_yin_yang) RadioGroup mYinYangGroup;
    @BindView(R.id.rg_diag_bagang_def_excess) RadioGroup mDefExcessGroup;
    @BindView(R.id.rg_diag_bagang_cold_heat) RadioGroup mColdHeatGroup;
    @BindView(R.id.rg_diag_bagang_int_ext) RadioGroup mIntExtGroup;
    @BindView(R.id.rb_bagang_yin_value) RadioButton mBagangYin;
    @BindView(R.id.rb_bagang_yang_value) RadioButton mBagangYang;
    @BindView(R.id.rb_bagang_deficiency_value) RadioButton mBagangDeficiency;
    @BindView(R.id.rb_bagang_excess_value) RadioButton mBagangExcess;
    @BindView(R.id.rb_bagang_cold_value) RadioButton mBagangCold;
    @BindView(R.id.rb_bagang_heat_value) RadioButton mBagangHeat;
    @BindView(R.id.rb_bagang_interior_value) RadioButton mBagangInterior;
    @BindView(R.id.rb_bagang_exterior_value) RadioButton mBagangExterior;
    @BindView(R.id.btn_add_wu_xing) Button mWuxingBtn;
    @BindView(R.id.iv_diag_wuxing) ImageView mWuxingImage;

    public DiagnosisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_diagnosis, container, false);
        ButterKnife.bind(this, rootView);

        if (!isNewSession) {
            initializeScreen();
        } else {
            hideBagang();
            hideWuxing();
        }

        // [START - SET UP BUTTONS FOR THE DIFFERENT SYSTEMS OF DIAGNOSIS]
        // Bagang (8 règles thérapeuthiques)
        mBagangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewSession) {
                    // create a new intent to start Ba Gang activity
                    Intent bagangIntent = new Intent(getActivity(), BagangActivity.class);
                    startActivityForResult(bagangIntent, BAGANG_REQUEST);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.session_not_saved),
                            Toast.LENGTH_SHORT).show();
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
        // create a reference to the firestore document holding data
        DocumentReference sessionDoc = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sSessionid);

        // listen to the session document snapshot and initialize data
        if (getActivity() != null && isAdded()) {
            sessionDoc.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), R.string.generic_data_load_failed, Toast.LENGTH_SHORT).show();
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // store all data to a Map
                        Map<String, Object> sessionMap = documentSnapshot.getData();

                        // get bagang data
                        Map<String, Boolean> bagang = (Map<String, Boolean>) sessionMap.get(Constants.BAGANG_KEY);
                        if (bagang != null) {
                            // Show if and only if at least one bagang value has been edited
                            if (!(bagang.get(Constants.BAGANG_YIN_KEY) == bagang.get(Constants.BAGANG_YANG_KEY)
                                    && bagang.get(Constants.BAGANG_DEFICIENCY_KEY) == bagang.get(Constants.BAGANG_EXCESS_KEY)
                                    && bagang.get(Constants.BAGANG_COLD_KEY) == bagang.get(Constants.BAGANG_HEAT_KEY)
                                    && bagang.get(Constants.BAGANG_INTERIOR_KEY) == bagang.get(Constants.BAGANG_EXTERIOR_KEY))) {
                                showBagang();
                                initializeBagangButtons(bagang);
                            } else {
                                hideBagang();
                            }

                        } else {
                            hideBagang();
                        }

                        // get wuxing (5 phases) data
                        Map<String, Boolean> wuxing = (Map<String, Boolean>) sessionMap.get(Constants.WUXING_KEY);
                        if (wuxing != null) {
                            Bundle wuxingBundle = new Bundle();
                            for (Map.Entry<String, Boolean> entry : wuxing.entrySet()) {
                                wuxingBundle.putBoolean(entry.getKey(), entry.getValue());
                            }
                            setWuxingImage(wuxingBundle);
                        } else {
                            hideWuxing();
                        }

                    // session hasn't been saved yet
                    } else {
                        hideBagang();
                        hideWuxing();
                    }
                }
            });
        }
    }

    private void initializeBagangButtons(Map<String, Boolean> bagang) {
        // First, re-init radio buttons
        clearBagangRadioButtons();

        // Then, initialize them according to stored data
        if (bagang.get(Constants.BAGANG_YIN_KEY)) {
            mBagangYang.setEnabled(false);
            mBagangYin.setEnabled(true);
            mBagangYin.setChecked(true);
        } else if (bagang.get(Constants.BAGANG_YANG_KEY)) {
            mBagangYin.setEnabled(false);
            mBagangYang.setEnabled(true);
            mBagangYang.setChecked(true);
        } else if (!bagang.get(Constants.BAGANG_YIN_KEY) && !bagang.get(Constants.BAGANG_YANG_KEY)) {
            mBagangYin.setEnabled(false);
            mBagangYang.setEnabled(false);
        }

        if (bagang.get(Constants.BAGANG_DEFICIENCY_KEY)) {
            mBagangExcess.setEnabled(false);
            mBagangDeficiency.setEnabled(true);
            mBagangDeficiency.setChecked(true);
        } else if (bagang.get(Constants.BAGANG_EXCESS_KEY)) {
            mBagangDeficiency.setEnabled(false);
            mBagangExcess.setEnabled(true);
            mBagangExcess.setChecked(true);
        } else if (!bagang.get(Constants.BAGANG_DEFICIENCY_KEY) && !bagang.get(Constants.BAGANG_EXCESS_KEY)){
            mBagangDeficiency.setEnabled(false);
            mBagangExcess.setEnabled(false);
        }

        if (bagang.get(Constants.BAGANG_COLD_KEY)) {
            mBagangHeat.setEnabled(false);
            mBagangCold.setEnabled(true);
            mBagangCold.setChecked(true);
        } else if (bagang.get(Constants.BAGANG_HEAT_KEY)) {
            mBagangCold.setEnabled(false);
            mBagangHeat.setEnabled(true);
            mBagangHeat.setChecked(true);
        } else if (!bagang.get(Constants.BAGANG_COLD_KEY) && !bagang.get(Constants.BAGANG_HEAT_KEY)){
            mBagangCold.setEnabled(false);
            mBagangHeat.setEnabled(false);
        }

        if (bagang.get(Constants.BAGANG_INTERIOR_KEY)) {
            mBagangExterior.setEnabled(false);
            mBagangInterior.setEnabled(true);
            mBagangInterior.setChecked(true);
        } else if (bagang.get(Constants.BAGANG_EXTERIOR_KEY)) {
            mBagangInterior.setEnabled(false);
            mBagangExterior.setEnabled(true);
            mBagangExterior.setChecked(true);
        } else if (!bagang.get(Constants.BAGANG_INTERIOR_KEY) && !bagang.get(Constants.BAGANG_EXTERIOR_KEY)){
            mBagangInterior.setEnabled(false);
            mBagangExterior.setEnabled(false);
        }
    }

    private void clearBagangRadioButtons() {
        mYinYangGroup.clearCheck();
        mDefExcessGroup.clearCheck();
        mColdHeatGroup.clearCheck();
        mIntExtGroup.clearCheck();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // [START - RESULT FROM FIVEPHASESACTIVITY]
            case WUXING_REQUEST:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getActivity(), getString(R.string.diag_5_phases_saved), Toast.LENGTH_SHORT).show();
                }
                break;
            // [END - RESULT FROM FIVEPHASESACTIVITY]

            // [START - RESULT FROM BAGANGACTIVITY]
            case BAGANG_REQUEST:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getActivity(), getString(R.string.diag_bagang_saved), Toast.LENGTH_SHORT).show();
                }
                break;
            // [END - RESULT FROM BAGANGACTIVITY]

            default:
                throw new IllegalArgumentException("Invalid request code, " + requestCode);

        }
        initializeScreen();
    }

    private void setWuxingImage(Bundle bundle) {
        // if wood attacks earth AND wood attacks metal
        if (bundle.getBoolean(Constants.WUXING_WOOD_TO_EARTH_KEY) &&
                bundle.getBoolean(Constants.WUXING_WOOD_TO_METAL_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_wood_earth_wood_metal);
            showWuxing();

        }
        // if only wood attacks earth
        else if (bundle.getBoolean(Constants.WUXING_WOOD_TO_EARTH_KEY) &&
                !bundle.getBoolean(Constants.WUXING_WOOD_TO_METAL_KEY) &&
                !bundle.getBoolean(Constants.WUXING_WATER_TO_EARTH_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_wood_earth);
            showWuxing();

        }
        // if only wood attacks metal
        else if (!bundle.getBoolean(Constants.WUXING_WOOD_TO_EARTH_KEY) &&
                bundle.getBoolean(Constants.WUXING_WOOD_TO_METAL_KEY) &&
                !bundle.getBoolean(Constants.WUXING_FIRE_TO_METAL_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_wood_metal);
            showWuxing();

        }
        // if wood attacks metal AND fire attacks metal
        else if (bundle.getBoolean(Constants.WUXING_WOOD_TO_METAL_KEY) &&
                bundle.getBoolean(Constants.WUXING_FIRE_TO_METAL_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_wood_metal_fire_metal);
            showWuxing();

        }
        // if only fire attacks metal
        else if (bundle.getBoolean(Constants.WUXING_FIRE_TO_METAL_KEY) &&
                !bundle.getBoolean(Constants.WUXING_FIRE_TO_WATER_KEY) &&
                !bundle.getBoolean(Constants.WUXING_WOOD_TO_METAL_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_fire_metal);
            showWuxing();

        }
        // if only fire attacks water
        else if (!bundle.getBoolean(Constants.WUXING_FIRE_TO_METAL_KEY) &&
                bundle.getBoolean(Constants.WUXING_FIRE_TO_WATER_KEY) &&
                !bundle.getBoolean(Constants.WUXING_EARTH_TO_WATER_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_fire_water);
            showWuxing();

        }
        // if fire attacks metal AND fire attacks water
        else if (bundle.getBoolean(Constants.WUXING_FIRE_TO_METAL_KEY) &&
                bundle.getBoolean(Constants.WUXING_FIRE_TO_WATER_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_fire_metal_fire_water);
            showWuxing();

        }
        // if only earth attacks water
        else if (bundle.getBoolean(Constants.WUXING_EARTH_TO_WATER_KEY) &&
                !bundle.getBoolean(Constants.WUXING_EARTH_TO_WOOD_KEY) &&
                !bundle.getBoolean(Constants.WUXING_FIRE_TO_WATER_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_earth_water);
            showWuxing();

        }
        // if only earth attacks wood
        else if (!bundle.getBoolean(Constants.WUXING_EARTH_TO_WATER_KEY) &&
                bundle.getBoolean(Constants.WUXING_EARTH_TO_WOOD_KEY) &&
                !bundle.getBoolean(Constants.WUXING_METAL_TO_WOOD_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_earth_wood);
            showWuxing();

        }
        // if earth attacks water AND wood
        else if (bundle.getBoolean(Constants.WUXING_EARTH_TO_WATER_KEY) &&
                bundle.getBoolean(Constants.WUXING_EARTH_TO_WOOD_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_earth_water_earth_wood);
            showWuxing();

        }
        // if earth attacks water AND fire attacks water
        else if (bundle.getBoolean(Constants.WUXING_EARTH_TO_WATER_KEY) &&
                bundle.getBoolean(Constants.WUXING_FIRE_TO_WATER_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_earth_water_fire_water);
            showWuxing();

        }
        // if only metal attacks wood
        else if (bundle.getBoolean(Constants.WUXING_METAL_TO_WOOD_KEY) &&
                !bundle.getBoolean(Constants.WUXING_METAL_TO_FIRE_KEY) &&
                !bundle.getBoolean(Constants.WUXING_EARTH_TO_WOOD_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_metal_wood);
            showWuxing();

        }
        // if only metal attacks fire
        else if (!bundle.getBoolean(Constants.WUXING_METAL_TO_WOOD_KEY) &&
                bundle.getBoolean(Constants.WUXING_METAL_TO_FIRE_KEY) &&
                !bundle.getBoolean(Constants.WUXING_WATER_TO_FIRE_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_metal_fire);
            showWuxing();

        }
        // if metal attacks fire AND wood
        else if (bundle.getBoolean(Constants.WUXING_METAL_TO_WOOD_KEY) &&
                bundle.getBoolean(Constants.WUXING_METAL_TO_FIRE_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_metal_wood_metal_fire);
            showWuxing();

        }
        // if metal attacks wood and earth attacks wood
        else if (bundle.getBoolean(Constants.WUXING_METAL_TO_WOOD_KEY) &&
                bundle.getBoolean(Constants.WUXING_EARTH_TO_WOOD_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_metal_wood_earth_wood);
            showWuxing();

        }
        // if only water attacks fire
        else if (bundle.getBoolean(Constants.WUXING_WATER_TO_FIRE_KEY) &&
                !bundle.getBoolean(Constants.WUXING_WATER_TO_EARTH_KEY) &&
                !bundle.getBoolean(Constants.WUXING_METAL_TO_FIRE_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_water_fire);
            showWuxing();

        }
        // if only water attacks earth
        else if (!bundle.getBoolean(Constants.WUXING_WATER_TO_FIRE_KEY) &&
                bundle.getBoolean(Constants.WUXING_WATER_TO_EARTH_KEY) &&
                !bundle.getBoolean(Constants.WUXING_WOOD_TO_EARTH_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_water_earth);
            showWuxing();

        }
        // if water attacks fire AND earth
        else if (bundle.getBoolean(Constants.WUXING_WATER_TO_FIRE_KEY) &&
                bundle.getBoolean(Constants.WUXING_WATER_TO_EARTH_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_water_fire_water_earth);
            showWuxing();

        }
        // if water attacks fire AND metal attacks fire
        else if (bundle.getBoolean(Constants.WUXING_WATER_TO_FIRE_KEY) &&
                bundle.getBoolean(Constants.WUXING_METAL_TO_FIRE_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_water_fire_metal_fire);
            showWuxing();

        }
        // if water attacks earth AND wood attacks earth
        else if (bundle.getBoolean(Constants.WUXING_WATER_TO_EARTH_KEY) &&
                bundle.getBoolean(Constants.WUXING_WOOD_TO_EARTH_KEY)){
            mWuxingImage.setImageResource(R.drawable.phases_water_earth_wood_earth);
            showWuxing();
        }
        // if nothing has been checked
        else {
            hideWuxing();
        }
    }

    private void showWuxing() {
        mWuxingImage.setVisibility(View.VISIBLE);
    }

    private void hideWuxing() {
        mWuxingImage.setVisibility(View.GONE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_save);
        menuItem.setVisible(false);
    }
}