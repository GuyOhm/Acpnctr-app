package com.acpnctr.acpnctr.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.acpnctr.acpnctr.data.ClientContract.ClientEntry;

/**
 * {@link ContentProvider} for Acpnctr app.
 */

public class ClientProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ClientProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the clients table */
    private static final int CLIENTS = 100;

    /** URI matcher code for the content URI for a single client in the clients table */
    private static final int CLIENT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.acpnctr.acpnctr/clients" will map to the
        // integer code {@link #CLIENTS}. This URI is used to provide access to MULTIPLE rows
        // of the clients table.
        sUriMatcher.addURI(ClientContract.CONTENT_AUTHORITY, ClientContract.PATH_CLIENTS, CLIENTS);

        // The content URI of the form "content://com.acpnctr.acpnctr/clients/#" will map to the
        // integer code {@link #CLIENT_ID}. This URI is used to provide access to ONE single row
        // of the client table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.acpnctr.acpnctr/clients/3" matches, but
        // "content://com.acpnctr.acpnctr/clients" (without a number at the end) doesn't match.
        sUriMatcher.addURI(ClientContract.CONTENT_AUTHORITY, ClientContract.PATH_CLIENTS+ "/#", CLIENT_ID);
    }

    /** Database helper object */
    private ClientDbHelper clientDbHelper;

    @Override
    public boolean onCreate() {
        clientDbHelper = new ClientDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = clientDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENTS:
                // For the CLIENTS code, query the clients table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the clients table.
                cursor = database.query(ClientEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CLIENT_ID:
                // For the CLIENT_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.acpnctr.acpnctr/clients/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ClientEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the clients table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ClientEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the Cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENTS:
                return ClientEntry.CONTENT_LIST_TYPE;
            case CLIENT_ID:
                return ClientEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENTS:
                return insertClient(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a client into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertClient(Uri uri, ContentValues values) {
        // Check that the NAME is not null or empty
        String name = values.getAsString(ClientEntry.COLUMN_CLIENT_NAME);
        if (name == null || TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Client requires a name");
        }

        // Check that the GENDER is valid
        Integer gender = values.getAsInteger(ClientEntry.COLUMN_CLIENT_GENDER);
        if (gender == null || !ClientEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Client requires valid gender");
        }

        // Check that the ACQUISITION channel is valid
        Integer acquiChannel = values.getAsInteger(ClientEntry.COLUMN_CLIENT_ACQUISITION);
        if (!ClientEntry.isValidAcquiChannel(acquiChannel)) {
            throw new IllegalArgumentException("Client requires valid acquisition channel");
        }

        // Check that the datetime is not null
        Long datetime = values.getAsLong(ClientEntry.COLUMN_CLIENT_DATETIME);
        if (datetime == null) {
            throw new IllegalArgumentException("Client requires a creation datetime");
        }

        // Get writable database
        SQLiteDatabase database = clientDbHelper.getWritableDatabase();

        // Insert the new client with the given values
        long id = database.insert(ClientEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the client content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENTS:
                return updateClient(uri, contentValues, selection, selectionArgs);
            case CLIENT_ID:
                // For the CLIENT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ClientEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateClient(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update clients in the database with the given content values.
     *
     * @return number of rows that were successfully updated.
     */
    private int updateClient(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link ClientEntry#COLUMN_CLIENT_NAME} key is present,
        // check that the name value is not null or empty
        if (values.containsKey(ClientEntry.COLUMN_CLIENT_NAME)) {
            String name = values.getAsString(ClientEntry.COLUMN_CLIENT_NAME);
            if (name == null || TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Client requires a name");
            }
        }

        // If the {@link ClientEntry#COLUMN_CLIENT_GENDER} key is present,
        // check that the gender value is not null or not valid
        if (values.containsKey(ClientEntry.COLUMN_CLIENT_GENDER)) {
            Integer gender = values.getAsInteger(ClientEntry.COLUMN_CLIENT_GENDER);
            if (gender == null || !ClientEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Client requires a gender");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = clientDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ClientEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
