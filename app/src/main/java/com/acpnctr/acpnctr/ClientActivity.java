package com.acpnctr.acpnctr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.acpnctr.acpnctr.adapters.ClientFragmentPageAdapter;
import com.acpnctr.acpnctr.utils.Constants;

import static com.acpnctr.acpnctr.fragments.InformationFragment.clientHasChanged;
import static com.acpnctr.acpnctr.utils.Constants.INTENT_EXTRA_UID;
import static com.acpnctr.acpnctr.utils.Constants.INTENT_SELECTED_CLIENT_ID;

public class ClientActivity extends AppCompatActivity {

    private final static String LOG_TAG = ClientActivity.class.getSimpleName();

    // static member variables to be used by fragments
    public static String sUid;
    public static String sClientid;
    public static boolean isNewClient;

    // Final Strings to store state information
    public static final String CLIENT_ID = "client_id";
    public static final String IS_NEW_CLIENT = "is_new_client";
    public static final String USER_ID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        if (savedInstanceState != null){
            sUid = savedInstanceState.getString(USER_ID);
            sClientid = savedInstanceState.getString(CLIENT_ID);
            isNewClient = savedInstanceState.getBoolean(IS_NEW_CLIENT);
        } else {
            // Handles the 2 cases:
            // 1. it's a new client, there's only sUid
            // 2. it's an existing client, there's the Client object to recover
            Intent intent = getIntent();
            sUid = intent.getStringExtra(INTENT_EXTRA_UID);
            if (intent.hasExtra(INTENT_SELECTED_CLIENT_ID)){
                sClientid = intent.getStringExtra(INTENT_SELECTED_CLIENT_ID);
                isNewClient = false;
            } else {
                sClientid = null;
                isNewClient = true;
            }
        }

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = findViewById(R.id.viewpager_client);

        // Create an adapter that knows which fragment should be shown on each page
        ClientFragmentPageAdapter adapter = new ClientFragmentPageAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.client_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // set up the FAB for creating a new session
        FloatingActionButton fab = findViewById(R.id.fab_add_session);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNewClient){
                    Toast.makeText(ClientActivity.this, getString(R.string.new_client_not_saved), Toast.LENGTH_SHORT).show();
                } else {
                    // Create a new intent to open the {@link SessionActivity}
                    Intent sessionIntent = new Intent(ClientActivity.this, SessionActivity.class);
                    // pass in uid and clientid to build firestore path
                    sessionIntent.putExtra(Constants.INTENT_EXTRA_UID, sUid);
                    sessionIntent.putExtra(Constants.INTENT_SELECTED_CLIENT_ID, sClientid);
                    // Start the new activity
                    startActivity(sessionIntent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.client_info, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        // If the client hasn't changed, continue with handling back button press
        if (!clientHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action menu in the app bar
            case R.id.action_create_data:
                return true;
            case R.id.action_get_data:
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the client hasn't changed, continue with navigating up to parent activity
                if (!clientHasChanged) {
                    NavUtils.navigateUpFromSameTask(ClientActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ClientActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    public void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Customize the alert dialog box
        Window alertDialogWindow = alertDialog.getWindow();
        if (alertDialogWindow != null) {
            alertDialogWindow.setBackgroundDrawableResource(R.color.colorAccent);
        }
        Button negButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button posButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        posButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putString(USER_ID, sUid);
        currentState.putString(CLIENT_ID, sClientid);
        currentState.putBoolean(IS_NEW_CLIENT, isNewClient);
    }
}
