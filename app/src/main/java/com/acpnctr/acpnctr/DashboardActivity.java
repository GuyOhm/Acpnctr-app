package com.acpnctr.acpnctr;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.acpnctr.acpnctr.adapters.ClientAdapter;
import com.acpnctr.acpnctr.models.Client;
import com.acpnctr.acpnctr.models.User;
import com.acpnctr.acpnctr.utils.AcpnctrUtil;
import com.acpnctr.acpnctr.utils.Constants;
import com.acpnctr.acpnctr.utils.FirebaseUtil;
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

import java.util.List;

import static android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_CLIENTS;
import static com.acpnctr.acpnctr.utils.Constants.FIRESTORE_COLLECTION_USERS;
import static com.acpnctr.acpnctr.utils.Constants.INTENT_EXTRA_UID;
import static com.acpnctr.acpnctr.utils.Constants.INTENT_SELECTED_CLIENT;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClientAdapter.OnClientSelectedListener {

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter mAdapter;

    // Request code for authentication
    private static final int AUTH_REQUEST = 101;

    // Flag that indicates if the user has already signed in hence has already a document in db
    private static boolean isAlreadyInDatabase = false;

    // Flag to indicate if the user is making a search on client
    private boolean isSearchingClient = false;

    // Flag to indicate if user has accepted ToS
    private boolean hasAcceptedTos = false;

    // Member variable for search text
    private String mSearchText;

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

    // TODO: save information state for hasAcceptedTOS...etc.

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
                                        isAlreadyInDatabase = true;
                                        hasAcceptedTos = true;
                                    } else {
                                        // the user signs in for the first time show tos for acceptance
                                        hasAcceptedTos = false;
                                        showTosDialog();
                                    }
                                }
                            }
                        });
                    }
                    // show the loading indicator
                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    // fetch data from firestore and display clients list
                    loadClientsList();

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // [ END - Toolbar ]

        // [ START - FAB ]
        // Set up a FAB to create a new client file, launching ClientActivity
        FloatingActionButton fab = findViewById(R.id.fab_add_client);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasAcceptedTos) {
                    // Create a new intent to open the {@link ClientActivity} passing in uid
                    Intent newClientIntent = new Intent(DashboardActivity.this, ClientActivity.class);
                    newClientIntent.putExtra(INTENT_EXTRA_UID, mUid);

                    // Start the activity
                    startActivity(newClientIntent);
                } else {
                    showTosDialog();
                }
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
        mNavigationView.setCheckedItem(R.id.nav_dashboard);
        // [ END - Side Navigation Drawer ]
    }

    private void showTosDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final FirebaseUser user = mAuth.getCurrentUser();
        final DocumentReference userDoc = db.collection(FIRESTORE_COLLECTION_USERS).document(mUid);
        // Build AlertDialog interface
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder
                .setTitle(getString(R.string.tos_dialog_title))
                .setView(inflater.inflate(R.layout.dialog_legal, null))
                .setPositiveButton(getString(R.string.tos_pos_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // the user signs in for the first time
                        createUserDocument(user, userDoc);
                        Toast.makeText(DashboardActivity.this, getString(R.string.tos_accepted),
                                Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.tos_neg_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Quit app
                        finishAndRemoveTask();
                    }
                });

        // Create and show it
        AlertDialog tosDialog = builder.create();
        tosDialog.show();

        // Set the message and make it clickable
        TextView tosTextView = tosDialog.findViewById(R.id.tos_text_tv);
        String tosMessage = getString(R.string.tos_url);
        tosTextView.setText(Html.fromHtml(tosMessage));
        tosTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // Customize the alert dialog box
        Window alertDialogWindow = tosDialog.getWindow();
        if (alertDialogWindow != null) {
            alertDialogWindow.setBackgroundDrawableResource(R.color.colorAccent);
        }
        Button negButton = tosDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button posButton = tosDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        posButton.setTextColor(getResources().getColor(R.color.colorPrimary));
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
                .placeholder(R.drawable.ic_account_circle_black_36dp)
                .into(pictureView);
        // name
        TextView nameView = header.findViewById(R.id.tv_drawer_user_name);
        nameView.setText(name);
        // and email
        TextView emailView = header.findViewById(R.id.tv_drawer_user_email);
        emailView.setText(email);
    }

    private void loadClientsList() {
        CollectionReference clientsCollection = db.collection(FIRESTORE_COLLECTION_USERS)
                .document(mUid)
                .collection(FIRESTORE_COLLECTION_CLIENTS);

        // declare query variable
        Query query;

        // query firestore for the user's clients
        if (!isSearchingClient) {
            // for the entire collection
            query = clientsCollection
                    .orderBy(Client.CLIENT_TIMESTAMP_KEY, Query.Direction.DESCENDING);
        } else {
            // for a search string
            query = clientsCollection
                    .whereGreaterThanOrEqualTo(Client.CLIENT_NAME_KEY, mSearchText)
                    .whereLessThanOrEqualTo(Client.CLIENT_NAME_KEY, AcpnctrUtil.zeefyForSearch(mSearchText))
                    .orderBy(Client.CLIENT_NAME_KEY, Query.Direction.ASCENDING);
        }

        // configure recycler adapter options
        FirestoreRecyclerOptions<Client> options = new FirestoreRecyclerOptions.Builder<Client>()
                .setQuery(query, Client.class)
                .build();

        mAdapter = new ClientAdapter(options, this){

            @Override
            public void onDataChanged() {
                // hide the loading indicator
                mLoadingIndicator.setVisibility(View.INVISIBLE);

                // if there are no clients, show the empty_list screen
                mEmptyListView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                if (mAuth.getCurrentUser() != null) {
                    Toast.makeText(DashboardActivity.this, getString(R.string.clients_list_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mClientsList.setLayoutManager(layoutManager);
        mClientsList.setHasFixedSize(true);
        mClientsList.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    // click handler of items of the clients list
    @Override
    public void onClientSelected(Client client, int position) {
        // getIdFromSnapshot(mAdapter, position);
        // Create a new intent to open the {@link ClientActivity} passing in uid and Client Object
        Intent clientIntent = new Intent(DashboardActivity.this, ClientActivity.class);
        // add the uid
        clientIntent.putExtra(INTENT_EXTRA_UID, mUid);
        // add the {@link Client} object
        clientIntent.putExtra(INTENT_SELECTED_CLIENT, client);
        // Get the selected client's id from the adapter and add it
        String selectedClientid = FirebaseUtil.getIdFromSnapshot(mAdapter, position);
        clientIntent.putExtra(Constants.INTENT_SELECTED_CLIENT_ID, selectedClientid);
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
        String phone = user.getPhoneNumber();
        long timestampCreated = System.currentTimeMillis();

        // create document for the new user in firestore db
        User userModel = new User(mUid, fullname, email, phone, authProvider != null ? authProvider.get(0) : null, timestampCreated);
        userDoc
                .set(userModel, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isAlreadyInDatabase = true;
                        hasAcceptedTos = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    /**
     * Starts AuthentificationActivity for authentication
     */
    private void launchAuthentication() {
        Intent authIntent = new Intent(DashboardActivity.this, AuthenticationActivity.class);
        startActivityForResult(authIntent, AUTH_REQUEST);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if the user is signed in (non-null)
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            if(mAdapter != null){
                mAdapter.startListening();
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
        if(mAdapter != null){
            mAdapter.stopListening();
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
        
        // Set a search interface in action bar using SearchView widget as per:
        // https://developer.android.com/guide/topics/search/search-dialog.html
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setQueryHint(getString(R.string.search_client_hint));
        searchView.setInputType(TYPE_TEXT_FLAG_CAP_WORDS);
        searchClient(searchView);
        return true;
    }

    /**
     * Handle search client
     * @param searchView for client search
     */
    private void searchClient(final SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mSearchText = s;
                if (!TextUtils.isEmpty(mSearchText)){
                    isSearchingClient = true;
                } else {
                    isSearchingClient = false;
                }
                loadClientsList();
                return true;
            }
        });
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
                        Toast.makeText(DashboardActivity.this, R.string.auth_signed_out, Toast.LENGTH_SHORT).show();
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
            case R.id.nav_dashboard:
                break;
            case R.id.nav_signout:
                signUserOut();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case AUTH_REQUEST:
                if (resultCode == RESULT_OK){
                    Toast.makeText(DashboardActivity.this, R.string.auth_signed_in, Toast.LENGTH_SHORT).show();
                    mNavigationView.setCheckedItem(R.id.nav_dashboard);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid request code, " + requestCode);
        }
    }
}
