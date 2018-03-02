package com.acpnctr.acpnctr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.adapters.AnamnesisAdapter;
import com.acpnctr.acpnctr.models.Anamnesis;
import com.acpnctr.acpnctr.utils.DateFormatUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.ParseException;

import static com.acpnctr.acpnctr.ClientActivity.isNewClient;
import static com.acpnctr.acpnctr.ClientActivity.sClientid;
import static com.acpnctr.acpnctr.ClientActivity.sUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_ANAMNESIS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

/**
 * {@link Fragment} to display client anamnesis.
 */
public class AnamnesisFragment extends Fragment {

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editTextDate;
    private EditText editTextHistory;

    private FirestoreRecyclerAdapter mAdapter;

    // Recycler view variable to display the user's clients list
    private RecyclerView mAnamnesisList;

    // loading indicator
    private ProgressBar mLoadingIndicator;

    public AnamnesisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_anamnesis, container, false);

        // hook our members var to the corresponding layout element
        Button buttonAdd = rootView.findViewById(R.id.btn_add_history);
        editTextDate = rootView.findViewById(R.id.et_history_date);
        editTextHistory = rootView.findViewById(R.id.et_history_text);
        mAnamnesisList = rootView.findViewById(R.id.rv_anamnesis_list);
        mLoadingIndicator = rootView.findViewById(R.id.pb_anamnesis_loading_indicator);

        // [START - handle click on add button ]
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewClient) {
                    // get data from the UI
                    String date = editTextDate.getText().toString().trim();
                    String history = editTextHistory.getText().toString().trim();
                    // declare and initialize a timestamp to store the converted data
                    long timestamp = 0;

                    // validate date format if any
                    if (!TextUtils.isEmpty(date)){
                        if (DateFormatUtil.validate(date)){
                            try {
                                timestamp = DateFormatUtil.convertStringToTimestamp(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), getString(R.string.date_format_not_valid), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            // date is not valid
                            Toast.makeText(getActivity(), getString(R.string.date_format_not_valid), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    // if history is not empty save data
                    if (!TextUtils.isEmpty(history)) {
                        // the user has entered a history
                        // create a new {@link Anamnesis} object with fetched data
                        Anamnesis anamnesis = new Anamnesis(timestamp*1000, history);

                        // build the firestore path
                        CollectionReference anamCollection = db.collection(FIRESTORE_COLLECTION_USERS)
                                .document(sUid)
                                .collection(FIRESTORE_COLLECTION_CLIENTS)
                                .document(sClientid)
                                .collection(FIRESTORE_COLLECTION_ANAMNESIS);

                        addAnamnesisDocument(anamnesis, anamCollection);
                        displayAnamnesisItems();

                    } else {
                        Toast.makeText(getActivity(), R.string.client_anam_no_history, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.new_client_not_saved), Toast.LENGTH_SHORT).show();
                }
            }
        });
        // [END - handle click on add button ]

        // Display list of histories from firestore
        if (sClientid != null){
            mLoadingIndicator.setVisibility(View.VISIBLE);
            displayAnamnesisItems();
        }

        return rootView;
    }

    private void displayAnamnesisItems() {

        // build the firestore path
        CollectionReference anamCollection = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_ANAMNESIS);

        // query firestore for the client's anamesis
        Query query = anamCollection
                .orderBy("timestamp", Query.Direction.ASCENDING);

        // configure recycler adapter options
        FirestoreRecyclerOptions<Anamnesis> options = new FirestoreRecyclerOptions.Builder<Anamnesis>()
                .setQuery(query, Anamnesis.class)
                .build();

        mAdapter = new AnamnesisAdapter(options){

            @Override
            public void onDataChanged() {
                // hide the loading indicator
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                if(getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.clients_list_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mAnamnesisList.setLayoutManager(layoutManager);
        mAnamnesisList.setHasFixedSize(false);
        mAnamnesisList.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    /**
     * Create a document in Firestore in collection anamnesis with a unique id
     *
     * @param anamnesis
     * @param anamCollection
     */
    private void addAnamnesisDocument(Anamnesis anamnesis, CollectionReference anamCollection) {
        anamCollection
                .add(anamnesis)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        editTextDate.setText("");
                        editTextHistory.setText("");
                        editTextHistory.clearFocus();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), R.string.generic_data_insert_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_save);
        menuItem.setVisible(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) mAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) mAdapter.stopListening();
    }
}
