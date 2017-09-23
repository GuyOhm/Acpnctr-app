package com.acpnctr.acpnctr.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API contract for acpnctr app.
 */

public final class ClientContract {

    // To prevent from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ClientContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider.
     */
    public static final String CONTENT_AUTHORITY = "com.acpnctr.acpnctr";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_CLIENTS = "clients";

    /**
     * Inner class that defines constant values for the clients database table.
     * Each entry in the table represents a single client.
     */
    public static final class ClientEntry implements BaseColumns {

        /**
         * The content URI to access the client data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CLIENTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of clients.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIENTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single client.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIENTS;

        /**
         * Name of database table for clients.
         */
        public final static String TABLE_NAME = "clients";

        /**
         * Unique ID number for the client (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the client.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CLIENT_NAME = "name";

        /**
         * Time of the client file creation.
         * <p>
         * Type: INTEGER (Unix time)
         */
        public final static String COLUMN_CLIENT_DATETIME = "datetime";

        /**
         * Date of birth of the client.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CLIENT_DOB = "dob";

        /**
         * Gender of the client.
         * <p>
         * The only possible values are {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_CLIENT_GENDER = "gender";

        /**
         * Channel which the client was acquired through.
         * <p>
         * The only possible values are {@link #ACQUI_WOM}, {@link #ACQUI_WEBSITE},
         * {@link #ACQUI_FACEBOOK}, {@link #ACQUI_CONFRERE}, or {@link #ACQUI_OFFLINE}.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_CLIENT_ACQUISITION = "acquisition";

        /**
         * email of the client.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CLIENT_EMAIL = "email";

        /**
         * phone number of the client.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CLIENT_PHONE = "phone";

        /**
         * Possible values for the gender of the client.
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        /**
         * Possible values for the acquisition channel.
         */
        public static final int ACQUI_UNKNOWN = 0;
        public static final int ACQUI_WOM = 1; // Word of mouth
        public static final int ACQUI_WEBSITE = 2;
        public static final int ACQUI_FACEBOOK = 3;
        public static final int ACQUI_CONFRERE = 4;
        public static final int ACQUI_OFFLINE = 5;

        /**
         * Returns whether or not the given gender is {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         */
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }

        /**
         * Returns whether or not the given acquisition channel is one of the possible value
         */
        public static boolean isValidAcquiChannel(int acquiChannel) {
            if (acquiChannel == ACQUI_UNKNOWN || acquiChannel == ACQUI_WOM || acquiChannel == ACQUI_WEBSITE
                    || acquiChannel == ACQUI_FACEBOOK || acquiChannel == ACQUI_CONFRERE
                    || acquiChannel == ACQUI_OFFLINE) {
                return true;
            }
            return false;
        }
    }

    /**
     * Inner class that defines constant values for the clients database table.
     * Each entry in the table represents a single client.
     */
    public static final class AnamnesisEntry implements BaseColumns {

    }
}
