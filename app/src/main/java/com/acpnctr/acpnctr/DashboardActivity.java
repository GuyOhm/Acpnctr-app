package com.acpnctr.acpnctr;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ListView;

import com.acpnctr.acpnctr.data.ClientContract.ClientEntry;

import java.util.Date;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    // TODO: implement search (SearchView?) client functionality

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = DashboardActivity.class.getSimpleName();

    /** Identifier for the client data loader */
    private static final int CLIENT_LOADER = 0;

    /** CursorAdapter for the ListView */
    ClientCursorAdapter clientCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TEST: insert client into the database
        // insertDummyClient();
        // TEST: query client into the database
        // testQueryClient();

        // Find the {@link ListView} object which will be populated with the client data.
        ListView listView = (ListView) findViewById(R.id.client_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.client_list_empty_view);
        listView.setEmptyView(emptyView);

        // Create a {@link ClientCursorAdapter} and set it up onto the list view
        clientCursorAdapter = new ClientCursorAdapter(this, null);
        listView.setAdapter(clientCursorAdapter);

        // set an ItemOnClickListener on to the client list to give access to file for each item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create a new intent to open the {@link ClientActivity}
                Intent clientIntent = new Intent(DashboardActivity.this, ClientActivity.class);

                // Start the new activity
                startActivity(clientIntent);
            }
        });

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

        // Kick off the loader
        getLoaderManager().initLoader(CLIENT_LOADER, null, this);
    }

    private void testQueryClient() {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ClientEntry._ID,
                ClientEntry.COLUMN_CLIENT_NAME,
                ClientEntry.COLUMN_CLIENT_DATETIME,
                ClientEntry.COLUMN_CLIENT_GENDER,
                ClientEntry.COLUMN_CLIENT_DOB,
                ClientEntry.COLUMN_CLIENT_EMAIL,
                ClientEntry.COLUMN_CLIENT_PHONE,
                ClientEntry.COLUMN_CLIENT_ACQUISITION};

        // Perform a query on the provider using the ContentResolver.
        // Use the {@link ClientEntry#CONTENT_URI} to access the client data.
        Cursor cursor = getContentResolver().query(
                ClientEntry.CONTENT_URI,   // The content URI of the clients table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows

        try {
            // display cursor content to Logcat
            DatabaseUtils.dumpCursor(cursor);
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }

    }

    private void insertDummyClient() {
        // create a ContentValues object with data associated to columns
        ContentValues values = new ContentValues();
        values.put(ClientEntry.COLUMN_CLIENT_NAME, "Roger Dupont");
        values.put(ClientEntry.COLUMN_CLIENT_DATETIME, new Date().getTime());
        values.put(ClientEntry.COLUMN_CLIENT_GENDER, ClientEntry.GENDER_MALE);
        values.put(ClientEntry.COLUMN_CLIENT_DOB, "01/03/1967");
        values.put(ClientEntry.COLUMN_CLIENT_EMAIL, "roger.dupont@gmail.com");
        values.put(ClientEntry.COLUMN_CLIENT_PHONE, "06 07 45 78 08");
        values.put(ClientEntry.COLUMN_CLIENT_ACQUISITION, ClientEntry.ACQUI_WEBSITE);

        // Insert a new row for client into the provider using the ContentResolver.
        // Use the {@link ClientEntry#CONTENT_URI} to indicate that we want to insert
        // into the clients database table.
        // Receive the new content URI that will allow us to access client's data in the future.
        Uri newUri = getContentResolver().insert(ClientEntry.CONTENT_URI, values);

        Log.v(LOG_TAG, "new uri: " + newUri);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation_client view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies columns we're interested in
        String[] projection = {
                ClientEntry._ID,
                ClientEntry.COLUMN_CLIENT_NAME,
                ClientEntry.COLUMN_CLIENT_DATETIME };

        // TODO: sort data in the descendant order of datetime (eventually must be last session datetime)
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ClientEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update {@link ClientCursorAdapter} with this new cursor containing updated client data
        clientCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        clientCursorAdapter.swapCursor(null);
    }
}
