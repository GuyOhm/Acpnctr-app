package com.acpnctr.acpnctr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Splash screen to be displayed when the user launch the app
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // start dashboard activity
        startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
        // close splash activity
        finish();
    }
}
