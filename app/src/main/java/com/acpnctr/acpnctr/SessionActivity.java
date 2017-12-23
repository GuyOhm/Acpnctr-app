package com.acpnctr.acpnctr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.acpnctr.acpnctr.adapters.SessionFragmentPageAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

public class SessionActivity extends AppCompatActivity {

    private static final String LOG_TAG = SessionActivity.class.getSimpleName();

    // Firebase instance variable
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // static member variables to be used by fragments
    public static String mUid;
    public static String mClientid;
    public static String mSessionid;

    // Final Strings to store state information
    public static final String CLIENT_ID = "client_id";
    public static final String USER_ID = "user_id";
    public static final String SESSION_ID = "session_id";
    public static final String IS_NEW_SESSION = "is_new_session";

    // Flag to track whether or not the session has been created
    public static boolean isNewSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // get the data from the intent that started the activity
            Intent intent = getIntent();
            mUid = intent.getStringExtra(USER_ID);
            mClientid = intent.getStringExtra(CLIENT_ID);
        } else {
            mUid = savedInstanceState.getString(USER_ID);
            mClientid = savedInstanceState.getString(CLIENT_ID);
            mSessionid = savedInstanceState.getString(SESSION_ID);
            isNewSession = savedInstanceState.getBoolean(IS_NEW_SESSION);
        }

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = findViewById(R.id.viewpager_session);

        // Create an adapter that knows which fragment should be shown on each page
        SessionFragmentPageAdapter adapter = new SessionFragmentPageAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.session_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CLIENT_ID, mClientid);
        outState.putString(USER_ID, mUid);
        outState.putString(SESSION_ID, mSessionid);
        outState.putBoolean(IS_NEW_SESSION, isNewSession);
    }
}
