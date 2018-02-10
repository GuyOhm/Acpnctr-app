package com.acpnctr.acpnctr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

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
import java.util.Map;

import static com.acpnctr.acpnctr.SessionActivity.sClientid;
import static com.acpnctr.acpnctr.SessionActivity.sSessionid;
import static com.acpnctr.acpnctr.SessionActivity.sUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_SESSIONS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

public class PalpationActivity extends AppCompatActivity {

    private static final String LOG_TAG = PalpationActivity.class.getSimpleName();

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText mAbdomen;
    EditText mMeridian;
    EditText mPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palpation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references to our views
        mAbdomen = findViewById(R.id.et_palp_abdomen);
        mMeridian = findViewById(R.id.et_palp_meridian);
        mPoint = findViewById(R.id.et_palp_point);

        // initialize list of palpations
        loadPalpation();

    }

    private void loadPalpation() {
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
                            // get palpation data and display them to edit views
                            Map<String, String> palpationMap = (Map<String, String>) sessionMap.get(Session.PALP_KEY);
                            if (palpationMap != null){
                                if(palpationMap.containsKey(Session.PALP_ABDOMEN_KEY)){
                                    mAbdomen.setText(palpationMap.get(Session.PALP_ABDOMEN_KEY));
                                }
                                if(palpationMap.containsKey(Session.PALP_MERIDIAN_KEY)){
                                    mMeridian.setText(palpationMap.get(Session.PALP_MERIDIAN_KEY));
                                }
                                if(palpationMap.containsKey(Session.PALP_POINT_KEY)){
                                    mPoint.setText(palpationMap.get(Session.PALP_POINT_KEY));
                                }

                            } else {
                                Log.d(LOG_TAG, "No Palpation data yet!");
                            }
                        } else {
                            Log.d(LOG_TAG, "Current data: null");
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.palpation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                cancelPalpation();
                return true;
            case R.id.action_save:
                savePalpation();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePalpation() {
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
        Map<String, String> palpationMap = new HashMap<>();

        palpationMap.put(Session.PALP_ABDOMEN_KEY, mAbdomen.getText().toString().trim());
        palpationMap.put(Session.PALP_MERIDIAN_KEY, mMeridian.getText().toString().trim());
        palpationMap.put(Session.PALP_POINT_KEY, mPoint.getText().toString().trim());

        // write data to the batch
        batch.update(sessionDoc, Session.PALP_KEY, palpationMap);

        // commit the batch
        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(LOG_TAG, "palpation saved!");
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

    private void cancelPalpation() {
        Intent cancelIntent = new Intent();
        setResult(RESULT_CANCELED, cancelIntent);
        finish();
    }
}
