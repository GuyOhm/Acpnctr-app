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
    public static final String FIRESTORE_COLLECTION_ANAMNESIS = "anamnesis";
    public static final String FIRESTORE_COLLECTION_SESSIONS = "sessions";
    public static final String FIRESTORE_COLLECTION_TREATMENT = "treatment";

    /**
     * Constants for extra key passing to intent
     */
    public static final String INTENT_EXTRA_UID = "uid";
    public static final String INTENT_SELECTED_CLIENT = "client";
    public static final String INTENT_SELECTED_CLIENT_ID = "clientid";
    public static final String INTENT_SELECTED_SESSION = "session";
    public static final String INTENT_SELECTED_SESSION_ID = "sessionid";
}
