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


    /**********************************************
     * MODEL RELATED CONSTANTS TO BE USED AS KEYS *
     **********************************************/
    // Session data from FourStepsFragment
    public static final String SESSION_RATING_KEY = "sessionRating";
    public static final String SESSION_TIMESTAMP = "timestampCreated";
    public static final String SESSION_GOAL = "goal";
    // Session data updated in TreatmentFragment
    public static final String SESSION_TREATMENT_LIST_KEY = "treatmentList";
    // Bagang from DiagnosisFragment
    public static final String BAGANG_KEY = "bagang";
    public static final String BAGANG_YIN_KEY = "yin";
    public static final String BAGANG_YANG_KEY = "yang";
    public static final String BAGANG_DEFICIENCY_KEY = "deficiency";
    public static final String BAGANG_EXCESS_KEY = "excess";
    public static final String BAGANG_COLD_KEY = "cold";
    public static final String BAGANG_HEAT_KEY = "heat";
    public static final String BAGANG_INTERIOR_KEY = "interior";
    public static final String BAGANG_EXTERIOR_KEY = "exterior";
    // Wu xing (5 phases) from DiagnosisFragment
    public static final String WUXING_KEY = "wuxing";
    public static final String WUXING_WOOD_TO_EARTH_KEY = "wood_attacks_earth";
    public static final String WUXING_WOOD_TO_METAL_KEY = "wood_attacks_metal";
    public static final String WUXING_FIRE_TO_METAL_KEY = "fire_attacks_metal";
    public static final String WUXING_FIRE_TO_WATER_KEY = "fire_attacks_water";
    public static final String WUXING_EARTH_TO_WATER_KEY = "earth_attacks_water";
    public static final String WUXING_EARTH_TO_WOOD_KEY = "earth_attacks_wood";
    public static final String WUXING_METAL_TO_WOOD_KEY = "metal_attacks_wood";
    public static final String WUXING_METAL_TO_FIRE_KEY = "metal_attacks_fire";
    public static final String WUXING_WATER_TO_FIRE_KEY = "water_attacks_fire";
    public static final String WUXING_WATER_TO_EARTH_KEY = "water_attacks_earth";
    // Questionnaire from 4 steps
    public static final String QUEST_KEY = "questionnaire";
    public static final String QUEST_YIN_YANG_KEY = "yin_yang";
    public static final String QUEST_FIVE_PHASES_KEY = "five_phases";
    public static final String QUEST_DIET_KEY = "diet";
    public static final String QUEST_DIGESTION_KEY = "digestion";
    public static final String QUEST_WAY_OF_LIFE_KEY = "way_of_life";
    public static final String QUEST_SLEEP_KEY = "sleep";
    public static final String QUEST_SYMPTOMS_KEY = "symptoms";
    public static final String QUEST_MEDICATION_KEY = "medication";
    public static final String QUEST_EVENTS_KEY = "events";
    public static final String QUEST_EMOTIONAL_KEY = "emotional";
    public static final String QUEST_PSYCHOLOGICAL_KEY = "psychological";
    public static final String QUEST_GYNECOLOGICAL_KEY = "gynecological";
    // Observation from 4 steps
    public static final String OBS_KEY = "observation";
    public static final String OBS_BEHAVIOUR_KEY = "behaviour";
    public static final String OBS_TONGUE_KEY = "tongue";
    public static final String OBS_LIPS_KEY = "lips";
    public static final String OBS_LIMB_KEY = "limb";
    public static final String OBS_MERIDIAN_KEY = "meridian";
    public static final String OBS_MORPHOLOGY_KEY = "morphology";
    public static final String OBS_NAILS_KEY = "nails";
    public static final String OBS_SKIN_KEY = "skin";
    public static final String OBS_HAIRINESS_KEY = "hairiness";
    public static final String OBS_COMPLEXION_KEY = "complexion";
    public static final String OBS_EYES_KEY = "eyes";
    // Auscultation from 4 steps
    public static final String AUSC_KEY = "auscultation";
    public static final String AUSC_BLOOD_PRESSURE_KEY = "blood_pressure";
    public static final String AUSC_ABDOMEN_KEY = "abdomen";
    public static final String AUSC_SMELL_KEY = "smell";
    public static final String AUSC_BREATHING_KEY = "breathing";
    public static final String AUSC_COUGH_KEY = "cough";
    public static final String AUSC_VOICE_KEY = "voice";
    // Palpation from 4 steps
    public static final String PALP_KEY = "palpation";
    public static final String PALP_ABDOMEN_KEY = "abdomen";
    public static final String PALP_MERIDIAN_KEY = "meridian";
    public static final String PALP_POINT_KEY = "point";
    // Pulses from 4 steps
    public static final String PULSES_KEY = "pulses";
    //... Eurythmy
    public static final String PULSES_EURYTHMY_KEY = "eurythmy";
    public static final String PULSES_EURYTHMY_BEAT_KEY = "beat_per_min";
    public static final String PULSES_EURYTHMY_BREATH_KEY = "breath_per_min";
    public static final String PULSES_EURYTHMY_BPB_KEY = "breath_per_breath";
    //... 28 types of pulse
    public static final String PULSES_28_TYPES_KEY = "28Types";
    public static final String PULSES_28_TYPES_FEOU_KEY = "feou";
    public static final String PULSES_28_TYPES_ROA_KEY = "roa";
    public static final String PULSES_28_TYPES_CHE_KEY = "che";
    public static final String PULSES_28_TYPES_RONG_KEY = "rong";
    public static final String PULSES_28_TYPES_TSIN_KEY = "tsin";
    public static final String PULSES_28_TYPES_KREOU_KEY = "kreou";
    public static final String PULSES_28_TYPES_SIEN_KEY = "sien";
    public static final String PULSES_28_TYPES_TCHIN_KEY = "tchin";
    public static final String PULSES_28_TYPES_TCHRE_KEY = "tchre";
    public static final String PULSES_28_TYPES_SEU_KEY = "seu";
    public static final String PULSES_28_TYPES_OE_KEY = "oé";
    public static final String PULSES_28_TYPES_ROAN_KEY = "roan";
    public static final String PULSES_28_TYPES_JOAN_KEY = "joan";
    public static final String PULSES_28_TYPES_JOU_KEY = "jou";
    public static final String PULSES_28_TYPES_FOU_KEY = "fou";
    public static final String PULSES_28_TYPES_CHOU_KEY = "chou";
    public static final String PULSES_28_TYPES_HIU_KEY = "hiu";
    public static final String PULSES_28_TYPES_KO_KEY = "ko";
    public static final String PULSES_28_TYPES_SAN_KEY = "san";
    public static final String PULSES_28_TYPES_SI_KEY = "si";
    public static final String PULSES_28_TYPES_LAO_KEY = "lao";
    public static final String PULSES_28_TYPES_TONG_KEY = "tong";
    public static final String PULSES_28_TYPES_TSOU_KEY = "tsou";
    public static final String PULSES_28_TYPES_TSIE_KEY = "tsie";
    public static final String PULSES_28_TYPES_TA_KEY = "ta";
    public static final String PULSES_28_TYPES_TCHRANG_KEY = "tchrang";
    public static final String PULSES_28_TYPES_TOAN_KEY = "toan";
    public static final String PULSES_28_TYPES_TAE_KEY = "taé";
}
