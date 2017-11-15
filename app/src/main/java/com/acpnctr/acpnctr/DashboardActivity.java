package com.acpnctr.acpnctr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.acpnctr.acpnctr.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Tag for the log messages
    public static final String LOG_TAG = DashboardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        // initialize Firebase components
        mAuth = FirebaseAuth.getInstance();

        // [ START - AuthStateListener ]
        // Set up an {@link FirebaseAuth.AuthStateListener} to listen to authentication state and
        // launch AuthenticationActivity if not signed in
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // already signed in
                    // logUserData(user);
                    // TODO: user is signed in then loadClientsList() -> to be written
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
                // Create a new intent to open the {@link ClientActivity}
                Intent clientIntent = new Intent(DashboardActivity.this, ClientActivity.class);

                // Start the new activity
                startActivity(clientIntent);
            }
        });
        // [ END - FAB ]

        // [ START - Side Navigation Drawer ]
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // [ END - Side Navigation Drawer ]

        // TODO: handle uid data within intent passed on by AuthenticationActivity
        // if (uid exist in db)
            // load clients list
        // else
            // createUserFirestoreDoc
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            // get data from FirebaseAuth object
            String uid = user.getUid();
            String email = user.getEmail();
            String fullname = user.getDisplayName();
            List<String> authProvider = user.getProviders();
            long timestampCreated = System.currentTimeMillis();

            Date date = new Date(timestampCreated);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YY");
            String formattedDate = sdf.format(date);
            Log.v(LOG_TAG, "date: " + formattedDate);

            User userModel = new User(uid, fullname, email, authProvider != null ? authProvider.get(0) : null, timestampCreated);

            // create the user document with the data
            db.collection("users").document(uid)
                    .set(userModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(LOG_TAG, "DocumentSnapshot successfully written");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(LOG_TAG, "Error writing the document", e);
                        }
                    });
        }
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
            // logUserData(user);
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

    private void logUserData(FirebaseUser user) {
        // get and store user's data
        String uid = user.getUid();
        String email = user.getEmail();
        String name = user.getDisplayName();
        List<String> prov = user.getProviders();

        // build string with data
        String info = "uid: " + uid + "\n";
        info += "email: " + email + "\n";
        info += "name: " + name + "\n";
        info += (prov != null ? prov.get(0) : null) + "\n";

        // log data
        Log.d(LOG_TAG, info);
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
                    }
                });
    }

    private void onSignedInInitialize() {
        // TODO : check if user exists in the database
        // TODO: loadUserData() if user already in the database
        // TODO: onFirstTimeSignIn() if user sign in for the first time => create user's data createUserData()
    }

    private void onSignedOutCleanup() {

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
