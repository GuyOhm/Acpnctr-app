package com.acpnctr.acpnctr.fragments;


import android.os.Bundle;
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

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.adapters.SessionAdapter;
import com.acpnctr.acpnctr.models.Session;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import static com.acpnctr.acpnctr.ClientActivity.mClientid;
import static com.acpnctr.acpnctr.ClientActivity.mUid;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_SESSIONS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;

/**
 * A {@link Fragment} to display the sessions history of a client.
 */
public class SessionsListFragment extends Fragment {

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirestoreRecyclerAdapter mAdapter;

    // Recycler view variable to display the user's clients list
    private RecyclerView mSessionsList;

    // loading indicator
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

        if (mClientid != null) {
            displaySessionsList();
        }

        return rootView;
    }

    private void displaySessionsList() {
        // build the firestore path
        CollectionReference sessionsCollection = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(mUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS)
                .document(mClientid)
                .collection(FIRESTORE_COLLECTION_SESSIONS);

        // query firestore for the client's sessions
        Query query = sessionsCollection
                .orderBy("timestampCreated", Query.Direction.DESCENDING);

        // configure recycler adapter options
        FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>()
                .setQuery(query, Session.class)
                .build();

        mAdapter = new SessionAdapter(options){

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
}
