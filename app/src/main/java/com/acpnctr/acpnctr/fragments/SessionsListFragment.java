package com.acpnctr.acpnctr.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.SessionActivity;
import com.acpnctr.acpnctr.adapters.SessionAdapter;
import com.acpnctr.acpnctr.models.Session;
import com.acpnctr.acpnctr.utils.Constants;
import com.acpnctr.acpnctr.utils.FirebaseUtil;
import com.acpnctr.acpnctr.utils.SingleToast;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

import static com.acpnctr.acpnctr.ClientActivity.sClientid;
import static com.acpnctr.acpnctr.ClientActivity.sUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_SESSIONS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

/**
 * A {@link Fragment} to display the sessions history of a client.
 */
public class SessionsListFragment extends Fragment implements SessionAdapter.OnSessionSelectedListener{

    private static final String LOG_TAG = SessionsListFragment.class.getSimpleName();

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirestoreRecyclerAdapter mAdapter;

    // Recycler view variable to display the user's clients list
    private RecyclerView mSessionsList;

    // Loading indicator
    private ProgressBar mLoadingIndicator;

    public SessionsListFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_session_list, container, false);

        // hook our members var to the corresponding layout element
        mSessionsList = rootView.findViewById(R.id.rv_sessions_list);
        mLoadingIndicator = rootView.findViewById(R.id.pb_sessions_list_loading_indicator);

        if (sClientid != null) {
            displaySessionsList();
        }

        return rootView;
    }

    private void displaySessionsList() {
        // build the firestore path
        CollectionReference sessionsCollection = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS);

        // query firestore for the client's sessions
        Query query = sessionsCollection
                .orderBy("timestampCreated", Query.Direction.DESCENDING);

        // configure recycler adapter options
        FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>()
                .setQuery(query, Session.class)
                .build();

        mAdapter = new SessionAdapter(options, this){

            @Override
            public void onDataChanged() {
                // hide the loading indicator
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.d("SessionsListFragment", "Error while loading sessions list" + e);
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mSessionsList.setLayoutManager(layoutManager);
        mSessionsList.setHasFixedSize(true);
        mSessionsList.setAdapter(mAdapter);
        mAdapter.startListening();
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

    @Override
    public void onSessionSelected(Session session, int position) {
        Intent intent = new Intent(getActivity(), SessionActivity.class);
        // get unique document id from the adapter
        String sessionid = FirebaseUtil.getIdFromSnapshot(mAdapter, position);
        // pass in uid
        intent.putExtra(Constants.INTENT_EXTRA_UID, sUid);
        // clientid
        intent.putExtra(Constants.INTENT_SELECTED_CLIENT_ID, sClientid);
        // sessionid
        intent.putExtra(Constants.INTENT_SELECTED_SESSION_ID, sessionid);
        // and the session obj to intent
        intent.putExtra(Constants.INTENT_SELECTED_SESSION, session);
        startActivity(intent);
    }

    @Override
    public void onSessionRated(final float rating, int position) {
        // get the id of the session that has been rated
        String sessionid = FirebaseUtil.getIdFromSnapshot(mAdapter, position);
        // build the HashMap with the sessionRating key and it's value
        HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.SESSION_RATING_KEY, rating);
        // path to the session document on firestore
        DocumentReference sessionDoc = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(sUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(sClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS)
                .document(sessionid);
        // update sessionRating value
        sessionDoc.update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (rating <= 1){
                            SingleToast.show(getActivity(), getString(R.string.session_rated_no_improvement), Toast.LENGTH_SHORT);
                        } else if (rating > 1 && rating <= 2){
                            SingleToast.show(getActivity(), getString(R.string.session_rated_small_improvement), Toast.LENGTH_SHORT);
                        } else if (rating > 2 && rating <= 3){
                            SingleToast.show(getActivity(), getString(R.string.session_rated_good_improvement), Toast.LENGTH_SHORT);
                        } else if (rating > 3 && rating <= 4){
                            SingleToast.show(getActivity(), getString(R.string.session_rated_total_improvement), Toast.LENGTH_SHORT);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, getString(R.string.session_rated_failed) + e);
                        Toast.makeText(getActivity(), getString(R.string.session_rated_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
