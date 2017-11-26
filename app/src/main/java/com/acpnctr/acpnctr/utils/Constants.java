package com.acpnctr.acpnctr.utils;

/**
 * This class contains constants that we use through the app
 */

public class Constants {

    /**
     *
     * Firestore constants used to identify db nodes i.e. Collection and Document
     */
    public static final String FIRESTORE_COLLECTION_USERS = "users";
    public static final String FIRESTORE_COLLECTION_CLIENTS = "clients";

    /**
     * Constants for extra key passing to intent
     */
    public static final String INTENT_EXTRA_UID = "uid";
    public static final String INTENT_EXTRA_CLIENTID = "clientid";

    /**
     * Possible values for the gender of the client.
     */
    public static final String GENDER_UNKNOWN = "unknown";
    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";

    /**
     * Possible values for the acquisition channel.
     */
    public static final String ACQUI_UNKNOWN = "unknown";
    public static final String ACQUI_WOM = "word of mouth";
    public static final String ACQUI_WEBSITE = "website";
    public static final String ACQUI_FACEBOOK = "facebook";
    public static final String ACQUI_CONFRERE = "confrere";
    public static final String ACQUI_OFFLINE = "offline";
}
