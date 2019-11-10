package com.config;

/**
 * Created by mg on 19/07/16.
 */
public class Config {

    // Server API KEY
    public static final String API_KEY = "450908816KGdcae2aYMK";

    // Change this on your own consumer key
    public static final String TWITTER_CONSUMER_KEY = "R0u1hsVNb1lOEMzbxW6Ju1jcm";

    // Change this on your own consumer secret
    public static final String TWITTER_CONSUMER_SECRET = "CGIZRrb7mabu16g89U1Uz1oDKor7qxWkqpQy4KLfmWzA9VsQBd";

    // You AdMob Banner Unit ID
    public static final String BANNER_AD_UNIT_ID = "ca-app-pub-5258952387280395/6512408744";
    //test
    //public static final String BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    // You AdMob Interstitial Unit ID
    public static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-5258952387280395/1744344943";
    //test
    //public static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

    // public static String BASE_URL = "http://www.joserv.org/akram_new/index.php/api/";
    public static String BASE_URL = "http://akramportal.org/akram/index.php/api/";

    // Set to true if you want to display test ads on your testing device
    public static final boolean TEST_ADS_USING_TESTING_DEVICE = true;

    // Show ads in the app
    public static final boolean SHOW_INTERSTITIAL = true;

    // Number of seconds to show interstitial ads in an interval
    public static final int INTERSTITIAL_DELAY_IN_SECONDS = 60;

    // Add testing device hash
    // It is displayed upon running the app, please check logcat.
    public static final String TESTING_DEVICE_HASH = "962DF709776186FBDC976A72D1FA61BD";

    // Set to true if you want to display ads in all views.
    public static final boolean WILL_SHOW_ADS = false;

    // Home radius min value in KM
    public static final int SLIDER_RADIUS_MIN = 1;

    // Home radius max value in M
    public static final int SLIDER_RADIUS_MAX = 500;

    // Home radius max value in KM
    public static final int SLIDER_RADIUS_MAX_GET = 4;

    // Home radius default value in KM
    public static final int SLIDER_RADIUS_DEFAULT = 20;

    // Search maximum search radius
    public static int MAX_SEARCH_RADIUS = 300;

    // Increase this for maximum review characters can be typed in Flagging content.
    public static int  MAX_CHARS_FLAGGED = 600;

    // DO NOT EDIT THIS
    public final static float CONVERSION_OFFLINE_DATA_DISTANCE_TO_KM = 0.001f;

    // DO NOT EDIT THIS
    public final static float CONVERSION_SERVER_DATA_DISTANCE_TO_KM = 1;


    //Akram 3.0
    public static String GIFT_ON_MAP = BASE_URL + "Map_gifts/g_m_r?";

    public static String GET_MERCHANT = BASE_URL + "Marchant/get_marchant";

    public static String RATE_GIFT = BASE_URL + "Map_gifts/rate_gift";

    public static String REGESTER_USER = BASE_URL + "User/register_user";

    public static String LOGIN_USER = BASE_URL + "User/login_user";

    public static String COLLECT_GIFT = BASE_URL + "Map_gifts/collect_gift";

    public static String GET_CATEGORY = BASE_URL + "Map_gifts/get_category";

    public static String SEND_INTEREST = BASE_URL + "Map_gifts/user_interest";

    public static String GET_MERCHANT_CONTRACT = BASE_URL + "Marchant/get_merchant_contract";

    public static String UPDATE_PROFILE = BASE_URL + "Users/update_user";

    public static String FORGET_PASSWORD = BASE_URL + "User/forget_password";

    public static String REGISTER_USER_FACEBOOK = BASE_URL + "User/register_user_facebook";

    //end Akram 3.0

    // DO NOT EDIT THIS
    public static String FLAG_URL = BASE_URL + "rest/flag.php";

    // DO NOT EDIT THIS
    public static String UPDATE_USER_PROFILE_URL = BASE_URL + "rest/update_user_profile.php";

    // DO NOT EDIT THIS
    public static String REGISTER_USER_URL = BASE_URL + "rest/register_user.php";

    public static String REGISTER_USER_social_URL = BASE_URL + "rest/social_register.php";

    // DO NOT EDIT THIS
    public static String LOGIN_URL = BASE_URL + "rest/login.php";

    // DO NOT EDIT THIS
    public static String REGISTER_URL = BASE_URL + "rest/register.php";

    // DO NOT EDIT THIS
    public static String SEARCH_API_URL = BASE_URL + "rest/search.php";

    // DO NOT EDIT THIS
    public final static String ADD_SIGHTING_URL = BASE_URL + "rest/add_sighting.php";

    // DO NOT EDIT THIS
    public final static String GET_SIGHTING_URL = BASE_URL + "rest/get_pokemons.php";

    // DO NOT EDIT THIS
    public final static String GET_MY_SIGHTINGS_URL = BASE_URL + "rest/get_my_sightings.php";

    // DO NOT EDIT THIS
    public final static String POST_LIKE_DISLIKE_URL = BASE_URL + "rest/post_like_dislike.php";

    public final static String GET_USER = BASE_URL + "rest/get_user.php";

    // DO NOT EDIT THIS
    public final static int DETAIL_ZOOM_OUT_VALUE = 20;

    // DO NOT EDIT THIS
    public final static int DELAY_MAP_SHOW_ANIMATION = 400;

    // DO NOT EDIT THIS
    public final static int DELAY_SHOW_ANIMATION = 300;

    // DO NOT EDIT THIS
    public final static int SPLASH_DELAY_IN_SECONDS = 1;

    // DO NOT EDIT THIS
    public final static boolean DEBUG_LOCATION = false;

    // DO NOT EDIT THIS
    public final static boolean SHOW_LOCATION_COORDINATES_LOG = false;

    // DO NOT EDIT THIS
    public final static double DEBUG_LATITUDE = 31.9494542f;

    // DO NOT EDIT THIS
    public final static double DEBUG_LONGITUDE = 35.9135282f;

    // DO NOT EDIT THIS
    public final static float MAP_INFO_WINDOW_X_OFFSET = 0.5f;

    // DO NOT EDIT THIS
    public static int REQUEST_CODE_ADD_SIGHTING = 8881;

    // DO NOT EDIT THIS
    public static int REQUEST_CODE_EDIT_SIGHTING = 8882;

    public static int REQUEST_CODE_SELECT_POKEMON = 8883;

    // DO NOT EDIT THIS
    public static int REQUEST_CODE_ADD_POKEMON = 8884;

    // DO NOT EDIT THIS
    public static int REQUEST_CODE_ADD_POKESTOP = 8885;

    // DO NOT EDIT THIS
    public static int REQUEST_CODE_ADD_GYM= 8886;

    // DO NOT EDIT THIS
    public static int REQUEST_CODE_REPORT = 8887;

    // DO NOT EDIT THIS
    public static int REQUEST_CODE_SIGHTING_DETAIL = 8888;

    // DO NOT EDIT THIS
    public static int RC_SIGN_IN = 8889;

    // DO NOT EDIT THIS
    public static int MAP_ZOOM_LEVEL = 19;

    public static int MAX_ZOOM_OUT_LEVEL = 18;

    // DO NOT EDIT THIS
    public static float METERS_TO_KM = 0.001f;

    // DO NOT EDIT THIS
    public static int ENTITY_ID_POKEMON = 1;

    // DO NOT EDIT THIS
    public static int ENTITY_ID_POKESTOP = 2;

    // DO NOT EDIT THIS
    public static int ENTITY_ID_GYM = 3;

    // DO NOT EDIT THIS
    public static String SD_CARD_PATH = "pokemonfinder_photos/";

    // DO NOT EDIT THIS
    public final static int PERMISSION_REQUEST_LOCATION_SETTINGS = 8882;
}
