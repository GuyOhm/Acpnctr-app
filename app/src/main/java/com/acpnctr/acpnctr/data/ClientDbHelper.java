package com.acpnctr.acpnctr.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.acpnctr.acpnctr.data.ClientContract.ClientEntry;

/**
 * Database helper for Acpnctr app. Manages database creation and version management.
 */

public class ClientDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "acpnctr.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public ClientDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create a String containing the SQL statement to create the Client table
        String SQL_CREATE_CLIENT_TABLE = "CREATE TABLE " + ClientEntry.TABLE_NAME + " ("
                + ClientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ClientEntry.COLUMN_CLIENT_NAME + " TEXT NOT NULL, "
                + ClientEntry.COLUMN_CLIENT_DATETIME + " INTEGER NOT NULL, "
                + ClientEntry.COLUMN_CLIENT_GENDER + " INTEGER NOT NULL, "
                + ClientEntry.COLUMN_CLIENT_DOB + " TEXT, "
                + ClientEntry.COLUMN_CLIENT_EMAIL + " TEXT, "
                + ClientEntry.COLUMN_CLIENT_PHONE + " TEXT, "
                + ClientEntry.COLUMN_CLIENT_ACQUISITION + " INTEGER);";

        // execute the SQL statement
        db.execSQL(SQL_CREATE_CLIENT_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
