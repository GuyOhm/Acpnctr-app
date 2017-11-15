package com.acpnctr.acpnctr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;
import java.util.List;

/**
 * This activity handles the authentication flow
 */

public class AuthenticationActivity extends AppCompatActivity {

    // tag for the log messages
    private static final String LOG_TAG = AuthenticationActivity.class.getSimpleName();

    // declare an arbitrary request code value for authUI
    public static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launchSignInFlow();
    }

    private void launchSignInFlow() {
        // choose providers for signing in
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
        );

        // create and launch sign in intent
        // TODO: include a terms of service url
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_account_circle_black_36dp)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // successfully signed in
            if (resultCode == RESULT_OK){
                Toast.makeText(AuthenticationActivity.this, "Signed in!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else {
                // signed in failed
                if (response == null) {
                    // user pressed back button hence cancelled sign in
                    Toast.makeText(AuthenticationActivity.this, "Signed in cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(AuthenticationActivity.this, "Couldn't connect to network", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(AuthenticationActivity.this, "Something went wrong :/", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Toast.makeText(AuthenticationActivity.this, "Ooops...", Toast.LENGTH_SHORT).show();
        }
    }
}
