package com.example.mal2017_assessmentmodule;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mal2017_assessmentmodule.models.User;
import com.google.gson.Gson;

/**
 * SessionManager - Manages user session and login state.
 *
 * Features:
 * - Store and retrieve user session data
 * - Check login status
 * - Clear session on logout
 * - User type checking
 *
 * Uses SharedPreferences for persistent storage
 * Uses Gson for object serialization
 *
 * @author BSCS2509254
 * @version 2.0
 */
public class SessionManager {

    private static final String PREF_NAME = "RestaurantProSession";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_JSON = "user_json";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    /**
     * Constructor - Initialize session manager
     *
     * @param context Application context
     */
    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
    }

    /**
     * Create login session
     * Saves user data and sets logged in flag
     *
     * @param user User object to save
     */
    public void createLoginSession(User user) {
        // Convert user object to JSON string
        String userJson = gson.toJson(user);

        // Save to preferences
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_JSON, userJson);
        editor.commit();
    }

    /**
     * Check if user is logged in
     *
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get logged in user data
     *
     * @return User object or null if not logged in
     */
    public User getLoggedInUser() {
        if (!isLoggedIn()) {
            return null;
        }

        String userJson = prefs.getString(KEY_USER_JSON, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }

        return null;
    }

    /**
     * Check if logged in user is staff
     *
     * @return true if user is staff, false otherwise
     */
    public boolean isStaff() {
        User user = getLoggedInUser();
        return user != null && Constants.USER_TYPE_STAFF.equalsIgnoreCase(user.getUsertype());
    }

    /**
     * Check if logged in user is guest
     *
     * @return true if user is guest, false otherwise
     */
    public boolean isGuest() {
        User user = getLoggedInUser();
        return user != null && Constants.USER_TYPE_GUEST.equalsIgnoreCase(user.getUsertype());
    }

    /**
     * Update user session data
     * Used when user profile is updated
     *
     * @param user Updated user object
     */
    public void updateUserSession(User user) {
        if (isLoggedIn()) {
            createLoginSession(user);
        }
    }

    /**
     * Clear session and logout user
     * Removes all session data
     */
    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    /**
     * Get user ID of logged in user
     *
     * @return User ID or 0 if not logged in
     */
    public int getUserId() {
        User user = getLoggedInUser();
        return user != null ? user.getUserId() : 0;
    }

    /**
     * Get email of logged in user
     *
     * @return Email or empty string if not logged in
     */
    public String getUserEmail() {
        User user = getLoggedInUser();
        return user != null ? user.getEmail() : "";
    }

    /**
     * Get full name of logged in user
     *
     * @return Full name or empty string if not logged in
     */
    public String getUserFullName() {
        User user = getLoggedInUser();
        return user != null ? user.getFullName() : "";
    }
}