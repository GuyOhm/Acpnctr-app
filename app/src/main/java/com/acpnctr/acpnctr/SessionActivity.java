package com.acpnctr.acpnctr;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.acpnctr.acpnctr.fragment.SessionFragmentPageAdapter;

public class SessionActivity extends AppCompatActivity {

    private static final String LOG_TAG = SessionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

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

}
