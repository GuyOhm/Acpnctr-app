package com.acpnctr.acpnctr.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Anamnesis;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.acpnctr.acpnctr.ClientActivity.isNewClient;
import static com.acpnctr.acpnctr.ClientActivity.mClientid;
import static com.acpnctr.acpnctr.ClientActivity.mUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_ANAMNESIS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

/**
 * {@link Fragment} to display client anamnesis.
 */
public class AnamnesisFragment extends Fragment {

    private final static String LOG_TAG = AnamnesisFragment.class.getSimpleName();

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button buttonAdd;
    private EditText editTextDate;
    private EditText editTextHistory;

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
        buttonAdd = rootView.findViewById(R.id.btn_add_history);
        editTextDate = rootView.findViewById(R.id.et_history_date);
        editTextHistory = rootView.findViewById(R.id.et_history_text);

        // handle click on add button
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewClient) {
                    // get data from the UI
                    String date = editTextDate.getText().toString().trim();
                    String history = editTextHistory.getText().toString().trim();


                    if (!TextUtils.isEmpty(history)) {
                        // the user has entered a history
                        // create a new {@link Anamnesis} object with fetched data
                        Anamnesis anamnesis = new Anamnesis(date, history);

                        // build the firestore path
                        CollectionReference anamCollection = db.collection(FIRESTORE_COLLECTION_USERS)
                                .document(mUid)
                                .collection(FIRESTORE_COLLECTION_CLIENTS)
                                .document(mClientid)
                                .collection(FIRESTORE_COLLECTION_ANAMNESIS);

                        addAnamnesisDocument(anamnesis, anamCollection);

                    } else {
                        Toast.makeText(getActivity(), R.string.client_anam_no_history, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.new_client_not_saved), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
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
                        Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        editTextDate.setText("");
                        editTextHistory.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error adding document", e);
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
}
