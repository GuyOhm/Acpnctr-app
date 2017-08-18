package com.acpnctr.acpnctr;


import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acpnctr.acpnctr.data.ClientContract.ClientEntry;

import java.util.Date;

import static com.acpnctr.acpnctr.data.ClientContract.ClientEntry.GENDER_UNKNOWN;

/**
 * {@link Fragment} to display client information.
 */
public class InformationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static String LOG_TAG = InformationFragment.class.getSimpleName();

    /**
     * EditText field to enter the client's information
     */
    private TextView clientNameEditText;
    private TextView clientDobEditText;
    private TextView clientPhoneEditText;
    private TextView clientEmailEditText;
    private Spinner clientGenderSpinner;
    private Spinner clientAcquisitionSpinner;

    /**
     * Gender and acquisition of the client.
     */
    private int clientGender = GENDER_UNKNOWN;
    private int clientAcquisition = ClientEntry.ACQUI_UNKNOWN;

    /**
     * Content URI for the existing client (null if it's a new client)
     */
    private Uri currentClientUri;

    /**
     * Identifier for the client data loader
     */
    private static final int EXISTING_CLIENT_LOADER = 0;

    /**
     * Boolean flag that keeps track of whether the client has been edited (true) or not (false)
     */
    public static boolean clientHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the clientHasChanged boolean to true.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            clientHasChanged = true;
            return false;
        }
    };

    public InformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        clientHasChanged = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new client or editing an existing one.
        Intent intent = getActivity().getIntent();
        currentClientUri = intent.getData();

        // If the intent DOES NOT contain a client content URI, then we know that we are
        // creating a new client.
        if (currentClientUri == null) {
            // This is a new pet, so change the app bar to say "Add a client"
            getActivity().setTitle(getString(R.string.add_client_activity_title));

        } else {
            // Initialize a loader to read the client data from the database
            // and display the current values in the editor
            getActivity().getLoaderManager().initLoader(EXISTING_CLIENT_LOADER, null, this);
        }

        // Find views that we need to read input from
        clientNameEditText = (TextView) rootView.findViewById(R.id.edit_client_name);
        clientDobEditText = (TextView) rootView.findViewById(R.id.edit_dob_name);
        clientPhoneEditText = (TextView) rootView.findViewById(R.id.edit_client_phone);
        clientEmailEditText = (TextView) rootView.findViewById(R.id.edit_client_email);
        clientGenderSpinner = (Spinner) rootView.findViewById(R.id.spinner_client_gender);
        clientAcquisitionSpinner = (Spinner) rootView.findViewById(R.id.spinner_client_acquisition);

        // Set up spinners
        setupGenderSpinner();
        setupAcquisitionSpinner();

        // Setup OnTouchListeners on all the input fields.
        clientNameEditText.setOnTouchListener(touchListener);
        clientDobEditText.setOnTouchListener(touchListener);
        clientPhoneEditText.setOnTouchListener(touchListener);
        clientEmailEditText.setOnTouchListener(touchListener);
        clientGenderSpinner.setOnTouchListener(touchListener);
        clientAcquisitionSpinner.setOnTouchListener(touchListener);

        return rootView;
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the client.
     */
    private void setupGenderSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        clientGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        clientGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        clientGender = ClientEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        clientGender = ClientEntry.GENDER_FEMALE;
                    } else {
                        clientGender = GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clientGender = GENDER_UNKNOWN;
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the acquisition channel of the client.
     */
    private void setupAcquisitionSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter acquiSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_acquisition_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        acquiSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        clientAcquisitionSpinner.setAdapter(acquiSpinnerAdapter);

        // Set the integer mSelected to the constant values
        clientAcquisitionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.acquisition_offline))) {
                        clientAcquisition = ClientEntry.ACQUI_OFFLINE;
                    } else if (selection.equals(getString(R.string.acquisition_wom))) {
                        clientAcquisition = ClientEntry.ACQUI_WOM;
                    } else if (selection.equals(getString(R.string.acquisition_website))) {
                        clientAcquisition = ClientEntry.ACQUI_WEBSITE;
                    } else if (selection.equals(getString(R.string.acquisition_facebook))) {
                        clientAcquisition = ClientEntry.ACQUI_FACEBOOK;
                    } else if (selection.equals(getString(R.string.acquisition_confrere))) {
                        clientAcquisition = ClientEntry.ACQUI_CONFRERE;
                    } else {
                        clientAcquisition = ClientEntry.ACQUI_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clientAcquisition = ClientEntry.ACQUI_UNKNOWN;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies columns we're interested in
        String[] projection = {
                ClientEntry._ID,
                ClientEntry.COLUMN_CLIENT_NAME,
                ClientEntry.COLUMN_CLIENT_DOB,
                ClientEntry.COLUMN_CLIENT_PHONE,
                ClientEntry.COLUMN_CLIENT_EMAIL,
                ClientEntry.COLUMN_CLIENT_GENDER,
                ClientEntry.COLUMN_CLIENT_ACQUISITION};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(getContext(),   // Parent activity context
                currentClientUri,               // Provider content URI to query
                projection,                     // Columns to include in the resulting Cursor
                null,                           // No selection clause
                null,                           // No selection arguments
                null);                          // Default sort order;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Move to the first row of the cursor and read data
        if (cursor.moveToFirst()) {
            // Extract data from the cursor
            String name = cursor.getString(cursor.getColumnIndex(ClientEntry.COLUMN_CLIENT_NAME));
            String dob = cursor.getString(cursor.getColumnIndex(ClientEntry.COLUMN_CLIENT_DOB));
            String phone = cursor.getString(cursor.getColumnIndex(ClientEntry.COLUMN_CLIENT_PHONE));
            String email = cursor.getString(cursor.getColumnIndex(ClientEntry.COLUMN_CLIENT_EMAIL));
            int gender = cursor.getInt(cursor.getColumnIndex(ClientEntry.COLUMN_CLIENT_GENDER));
            int acquisition = cursor.getInt(cursor.getColumnIndex(ClientEntry.COLUMN_CLIENT_ACQUISITION));

            // Update the views
            clientNameEditText.setText(name);
            clientDobEditText.setText(dob);
            clientPhoneEditText.setText(phone);
            clientEmailEditText.setText(email);

            // Map the constant value from the database into one of the dropdown options
            // and set the selection of the gender spinner
            switch (gender) {
                case ClientEntry.GENDER_MALE:
                    clientGenderSpinner.setSelection(1);
                    break;
                case ClientEntry.GENDER_FEMALE:
                    clientGenderSpinner.setSelection(2);
                    break;
                default:
                    clientGenderSpinner.setSelection(0);
                    break;
            }

            // Map the constant value from the database into one of the dropdown options
            // and set the selection of the acquisition spinner
            switch (acquisition) {
                case ClientEntry.ACQUI_WOM:
                    clientAcquisitionSpinner.setSelection(1);
                    break;
                case ClientEntry.ACQUI_WEBSITE:
                    clientAcquisitionSpinner.setSelection(2);
                    break;
                case ClientEntry.ACQUI_FACEBOOK:
                    clientAcquisitionSpinner.setSelection(3);
                    break;
                case ClientEntry.ACQUI_CONFRERE:
                    clientAcquisitionSpinner.setSelection(4);
                    break;
                case ClientEntry.ACQUI_OFFLINE:
                    clientAcquisitionSpinner.setSelection(5);
                    break;
                default:
                    clientAcquisitionSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        clientNameEditText.setText("");
        clientDobEditText.setText("");
        clientPhoneEditText.setText("");
        clientEmailEditText.setText("");
        clientGenderSpinner.setSelection(0); // Select "Unknown" gender
        clientAcquisitionSpinner.setSelection(0); // Select "Unknown" acqui channel

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // Save client to database
                saveClient();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveClient() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String name = clientNameEditText.getText().toString().trim();
        String dob = clientDobEditText.getText().toString().trim();
        String phone = clientPhoneEditText.getText().toString().trim();
        String email = clientEmailEditText.getText().toString().trim();


        // If it's a new client check if name and gender has been edited
        if (currentClientUri == null && TextUtils.isEmpty(name)) {
            Toast.makeText(getActivity(), getString(R.string.client_info_name_needed),
                    Toast.LENGTH_SHORT).show();
            // return early
            return;
        } else if (currentClientUri == null && clientGender == ClientEntry.GENDER_UNKNOWN) {
            Toast.makeText(getActivity(), getString(R.string.client_info_gender_needed),
                    Toast.LENGTH_SHORT).show();
            // return early
            return;
        }

        // Create a ContentValues and store the data to be saved
        ContentValues values = new ContentValues();
        values.put(ClientEntry.COLUMN_CLIENT_NAME, name);
        values.put(ClientEntry.COLUMN_CLIENT_DOB, dob);
        values.put(ClientEntry.COLUMN_CLIENT_PHONE, phone);
        values.put(ClientEntry.COLUMN_CLIENT_EMAIL, email);
        values.put(ClientEntry.COLUMN_CLIENT_GENDER, clientGender);
        values.put(ClientEntry.COLUMN_CLIENT_ACQUISITION, clientAcquisition);

        // Determine if this is a new or existing client
        if (currentClientUri == null) {
            // Add creation datetime to the values
            values.put(ClientEntry.COLUMN_CLIENT_DATETIME, new Date().getTime());
            // This is a NEW client, so insert a new client into the provider,
            // returning the content URI for the new client.
            Uri newUri = getActivity().getContentResolver().insert(ClientEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(getActivity(), getString(R.string.client_info_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(getActivity(), getString(R.string.client_info_insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (clientHasChanged) {
            // Otherwise this is an EXISTING client, so update the client with content URI: currentClientUri
            int rowsAffected = getActivity().getContentResolver().update(currentClientUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(getActivity(), getString(R.string.client_info_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(getActivity(), getString(R.string.client_info_update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        clientHasChanged = false;
    }


}
