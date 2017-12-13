package com.acpnctr.acpnctr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acpnctr.acpnctr.adapters.ClientAdapter;
import com.acpnctr.acpnctr.models.Client;
import com.acpnctr.acpnctr.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;
import static com.acpnctr.acpnctr.utils.Constants.INTENT_CURRENT_CLIENT;
import static com.acpnctr.acpnctr.utils.Constants.INTENT_EXTRA_UID;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClientAdapter.OnClientSelectedListener {

    // Tag for the log messages
    public static final String LOG_TAG = DashboardActivity.class.getSimpleName();

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;

    // Flag that indicates if the user has already signed in hence has already a document in db
    private static boolean isAlreadyInDatabase = false;

    // Member variable for uid
    private String mUid;

    // Recycler view variable to display the user's clients list
    private RecyclerView mClientsList;

    // layout to display if the clients list is empty
    private RelativeLayout mEmptyListView;

    // loading indicator
    private ProgressBar mLoadingIndicator;

    // Drawer Navigation View
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        // get the RecyclerView for clients list
        mClientsList = findViewById(R.id.rv_clients_list);

        // get the empty list view
        mEmptyListView = findViewById(R.id.rl_client_list_empty_view);

        // get the loading indicator
        mLoadingIndicator = findViewById(R.id.pb_clients_list_loading_indicator);

        // initialize Firebase components
        mAuth = FirebaseAuth.getInstance();

        // [ START - AuthStateListener ]
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // if the user is signed in get the uid
                    mUid = user.getUid();

                    if (!isAlreadyInDatabase) {
                        // check whether or not uid exists in firestore db
                        final DocumentReference userDoc = db.collection(FIRESTORE_COLLECTION_USERS).document(mUid);
                        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot snapshot = task.getResult();
                                    if (snapshot.exists()) {
                                        // the user already exists hence has a firestore document named by uid
                                        Log.d(LOG_TAG, "user already exists in db: " + snapshot.getData());
                                        isAlreadyInDatabase = true;

                                    } else {
                                        // the user signs in for the first time
                                        Log.d(LOG_TAG, "uid: " + mUid + " doesn't exist");
                                        createUserDocument(user, userDoc);
                                    }

                                } else {
                                    Log.d(LOG_TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                    // show the loading indicator
                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    // fetch data from firestore and display clients list
                    CollectionReference clientsCollection = db.collection(FIRESTORE_COLLECTION_USERS)
                            .document(mUid)
                            .collection(FIRESTORE_COLLECTION_CLIENTS);
                    loadClientsList(clientsCollection);

                    // initialize user's data in the drawer's navigation header
                    onSignedInInitializeNavHeader(user);
                } else {
                    // not signed in
                    launchAuthentication();
                }
            }
        };
        // [ END - AuthStateListener ]

        // [ START - Toolbar ]
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // [ END - Toolbar ]

        // [ START - FAB ]
        // Set up a FAB to create a new client file, launching ClientActivity
        FloatingActionButton fab = findViewById(R.id.fab_add_client);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link ClientActivity} passing in uid
                Intent newClientIntent = new Intent(DashboardActivity.this, ClientActivity.class);
                newClientIntent.putExtra(INTENT_EXTRA_UID, mUid);

                // Start the activity
                startActivity(newClientIntent);
            }
        });
        // [ END - FAB ]

        // [ START - Side Navigation Drawer ]
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        // [ END - Side Navigation Drawer ]
    }

    private void onSignedInInitializeNavHeader(FirebaseUser user) {
        // Collect data from FirebaseUser
        Uri pictureUri = user.getPhotoUrl();
        String name = user.getDisplayName();
        String email = user.getEmail();
        // initialize pic
        View header = mNavigationView.getHeaderView(0);
        ImageView pictureView = header.findViewById(R.id.iv_drawer_user_pic);
        Picasso.with(DashboardActivity.this)
                .load(pictureUri)
                //.resize(50, 50)
                //.centerCrop()
                .placeholder(R.drawable.ic_account_circle_black_36dp)
                .into(pictureView);
        // name
        TextView nameView = header.findViewById(R.id.tv_drawer_user_name);
        nameView.setText(name);
        // and email
        TextView emailView = header.findViewById(R.id.tv_drawer_user_email);
        emailView.setText(email);
    }

    private void loadClientsList(CollectionReference clientsCollection) {

        // query firestore for the user's clients
        Query query = clientsCollection
                .orderBy("timestampCreated", Query.Direction.DESCENDING);

        // configure recycler adapter options
        FirestoreRecyclerOptions<Client> options = new FirestoreRecyclerOptions.Builder<Client>()
                .setQuery(query, Client.class)
                .build();

        adapter = new ClientAdapter(options, this){

            @Override
            public void onDataChanged() {
                // hide the loading indicator
                mLoadingIndicator.setVisibility(View.INVISIBLE);

                // if there are no clients, show the empty_list screen
                mEmptyListView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.d(LOG_TAG, "Error while loading data for clients list" + e);
                Toast.makeText(DashboardActivity.this, getString(R.string.clients_list_error),
                        Toast.LENGTH_SHORT).show();
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mClientsList.setLayoutManager(layoutManager);
        mClientsList.setHasFixedSize(true);
        mClientsList.setAdapter(adapter);
        adapter.startListening();
    }

    // click handler of items of the clients list
    @Override
    public void onClientSelected(Client client) {
        // Create a new intent to open the {@link ClientActivity} passing in uid and Client Object
        Intent clientIntent = new Intent(DashboardActivity.this, ClientActivity.class);
        clientIntent.putExtra(INTENT_EXTRA_UID, mUid);
        clientIntent.putExtra(INTENT_CURRENT_CLIENT, client);

        // start activity
        startActivity(clientIntent);
    }

    /**
     * This method creates a document named by the uid in Firestore db when he signs in for
     * the first time
     *
     * @param user is the FirebaseUser recovered from FirebaseAuth
     * @param userDoc is the reference to the path in Firestore db
     */
    private void createUserDocument(FirebaseUser user, DocumentReference userDoc) {
        // collect user's data from the user and store them
        String email = user.getEmail();
        String fullname = user.getDisplayName();
        List<String> authProvider = user.getProviders();
        long timestampCreated = System.currentTimeMillis();

        // create document for the new user in firestore db
        User userModel = new User(mUid, fullname, email, authProvider != null ? authProvider.get(0) : null, timestampCreated);
        userDoc
                .set(userModel, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_TAG, "DocumentSnapshot successfully written");
                        isAlreadyInDatabase = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error writing the document", e);
                    }
                });
    }

    private String convertTimeStampToString(long timestamp) {
        // TODO: insert auto Locale recog for date pattern
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YY");
        String formattedDate = sdf.format(date);
        Log.v(LOG_TAG, "date: " + formattedDate);
        return formattedDate;
    }

    /**
     * Starts AuthentificationActivity for authentication
     */
    private void launchAuthentication() {
        Intent authIntent = new Intent(DashboardActivity.this, AuthenticationActivity.class);
        startActivity(authIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if the user is signed in (non-null)
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            if(adapter != null){
                adapter.startListening();
            }
        } else {
            launchAuthentication();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Handles action bar menu navigation
     *
     * @param item that is clicked
     * @return true when item is clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_logout:
                signUserOut();
                return true;
            case R.id.action_signin:
                launchAuthentication();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Signs the user out using FirebaseUI API
     */
    private void signUserOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is signed out
                        Toast.makeText(DashboardActivity.this, "You\'ve successfully signed out", Toast.LENGTH_SHORT).show();
                        isAlreadyInDatabase = false;
                    }
                });
    }

    /**
     * Handles side drawer menu navigation
     *
     * @param item that is clicked from the nav menu
     * @return true when item is clicked
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation_client view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_camera:
                break;
            case R.id.nav_gallery:
                break;
            case R.id.nav_slideshow:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
