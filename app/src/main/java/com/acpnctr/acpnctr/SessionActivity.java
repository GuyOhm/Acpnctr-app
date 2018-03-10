package com.acpnctr.acpnctr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.acpnctr.acpnctr.adapters.SessionFragmentPageAdapter;
import com.acpnctr.acpnctr.utils.Constants;

public class SessionActivity extends AppCompatActivity {

    // static member variables to be used by fragments
    public static String sUid;
    public static String sClientid;
    public static String sSessionid;

    // Final Strings to store state information
    private static final String CLIENT_ID = "client_id";
    private static final String USER_ID = "user_id";
    private static final String SESSION_ID = "session_id";
    private static final String IS_NEW_SESSION = "is_new_session";

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
            sUid = intent.getStringExtra(Constants.INTENT_EXTRA_UID);
            sClientid = intent.getStringExtra(Constants.INTENT_SELECTED_CLIENT_ID);
            if (intent.hasExtra(Constants.INTENT_SELECTED_SESSION_ID)){
                sSessionid = intent.getStringExtra(Constants.INTENT_SELECTED_SESSION_ID);
                isNewSession = false;
            } else {
                isNewSession = true;
            }
        } else {
            sUid = savedInstanceState.getString(USER_ID);
            sClientid = savedInstanceState.getString(CLIENT_ID);
            sSessionid = savedInstanceState.getString(SESSION_ID);
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
        outState.putString(CLIENT_ID, sClientid);
        outState.putString(USER_ID, sUid);
        outState.putString(SESSION_ID, sSessionid);
        outState.putBoolean(IS_NEW_SESSION, isNewSession);
    }
}
