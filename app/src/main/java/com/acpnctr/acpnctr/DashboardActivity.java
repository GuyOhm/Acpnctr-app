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

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import java.util.Arrays;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ClientsListAdapter clientsListAdapter;

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = DashboardActivity.class.getSimpleName();

    // declare an arbitrary request code value for authUI
    public static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        // initialize Firebase components
        mAuth = FirebaseAuth.getInstance();

/*        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // check if the user is logged in
                if (user != null) {
                    // already signed in
                    Toast.makeText(DashboardActivity.this, "user is signed in!", Toast.LENGTH_SHORT).show();
                    // TODO: user is signed in then loadClientsList() -> to be written
                } else {
                    // not signed in
                    Toast.makeText(DashboardActivity.this, "user is not signed in.", Toast.LENGTH_SHORT).show();
                    // create a sign in intent using AuthUI.SignInIntentBuilder
                    // a builder instance can be retrieved by calling createSignInIntentBuilder()
                    // TODO: launch the sign in / up flow with authUI (email + google)
                    // TODO: include a terms of service url
                    launchSignInFlow();
                }
            }
        };*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_client);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link ClientActivity}
                Intent clientIntent = new Intent(DashboardActivity.this, ClientActivity.class);

                // Start the new activity
                startActivity(clientIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if the user is signed in (non-null)
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // already signed in
            Toast.makeText(DashboardActivity.this, "user is signed in!", Toast.LENGTH_SHORT).show();
            // TODO: user is signed in then loadClientsList() -> to be written
            // check user data
            String uid = user.getUid();
            String email = user.getEmail();
            String name = user.getDisplayName();
            FirebaseUserMetadata metadata = user.getMetadata();
            long timestampCreated = metadata.getCreationTimestamp();

            String info = "uid: " + uid + "\n";
            info += "email: " + email + "\n";
            info += "name: " + name + "\n";
            info += "timestamp created: " + timestampCreated + "\n";
            Log.v(LOG_TAG, info);

        } else {
            // not signed in
            Toast.makeText(DashboardActivity.this, "user is not signed in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchSignInFlow() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: insert idp response
        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // successfully signed in
            if (resultCode == RESULT_OK){
                Toast.makeText(DashboardActivity.this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else {
                // signed in failed
                if (response == null) {
                    // user pressed back button hence cancelled sign in
                    Toast.makeText(DashboardActivity.this, "Signed in cancelled", Toast.LENGTH_SHORT).show();
                }

                else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(DashboardActivity.this, "Couldn't connect to network", Toast.LENGTH_SHORT).show();
                }

                else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(DashboardActivity.this, "Something went wrong :/", Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(DashboardActivity.this, "Ooops...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_logout:
                logUserOut();
                return true;
            case R.id.action_signin:
                launchSignInFlow();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logUserOut() {
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
