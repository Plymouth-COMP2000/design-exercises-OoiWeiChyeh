package com.example.mal2017_assessmentmodule;

/**
 * Constants - Application-wide constant values.
 *
 * Contains:
 * - Category constants for menu items
 * - Status constants for reservations
 * - Mode constants for activities
 * - Extra keys for Intent data passing
 * - Notification channel IDs
 *
 * @author BSCS2509254
 * @version 2.0
 */
public class Constants {

    // Menu Categories
    public static final String CATEGORY_ALL = "All";
    public static final String CATEGORY_APPETIZERS = "Appetizers";
    public static final String CATEGORY_MAIN_COURSE = "Main Course";
    public static final String CATEGORY_DESSERTS = "Desserts";
    public static final String CATEGORY_BEVERAGES = "Beverages";

    // Reservation Status
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_COMPLETED = "completed";

    // Activity Modes
    public static final String MODE_ADD = "add";
    public static final String MODE_EDIT = "edit";
    public static final String MODE_VIEW = "view";

    // Intent Extra Keys
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_ITEM_ID = "item_id";
    public static final String EXTRA_RESERVATION_ID = "reservation_id";
    public static final String EXTRA_USER_ID = "user_id";
    public static final String STUDENT_ID = "2509254";
    // User Types
    public static final String USER_TYPE_STAFF = "staff";
    public static final String USER_TYPE_GUEST = "guest";

    // Notification Channels
    public static final String CHANNEL_ID_RESERVATIONS = "reservations";
    public static final String CHANNEL_NAME_RESERVATIONS = "Reservation Updates";
    public static final String CHANNEL_DESC_RESERVATIONS = "Notifications for reservation confirmations and updates";

    // Notification IDs
    public static final int NOTIFICATION_ID_RESERVATION = 1001;

    // Shared Preferences Keys
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";
    public static final String PREF_USER_DATA = "user_data";

    // API Response Keys
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_ERROR = "error";
    public static final String KEY_DATA = "data";

    // Validation
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_NOTES_LENGTH = 200;
    public static final int MIN_PARTY_SIZE = 1;
    public static final int MAX_PARTY_SIZE = 8;

    // Restaurant Operating Hours
    public static final int RESTAURANT_OPEN_HOUR = 10; // 10 AM
    public static final int RESTAURANT_CLOSE_HOUR = 22; // 10 PM

    // Date Format
    public static final String DATE_FORMAT_DISPLAY = "MMM dd, yyyy";
    public static final String TIME_FORMAT_DISPLAY = "h:mm a";
    public static final String DATETIME_FORMAT_FULL = "MMM dd, yyyy h:mm a";
}