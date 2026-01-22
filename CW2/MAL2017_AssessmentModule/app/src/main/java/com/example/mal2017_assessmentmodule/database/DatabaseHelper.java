package com.example.mal2017_assessmentmodule.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mal2017_assessmentmodule.models.MenuItem;
import com.example.mal2017_assessmentmodule.models.Reservation;
import com.example.mal2017_assessmentmodule.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper - SQLite database manager for local data persistence.
 *
 * Database Architecture:
 * - Users table: Store user accounts (matches API credentials)
 * - MenuItems table: Cache menu items for offline access
 * - Reservations table: Store reservation data
 *
 * Design Patterns:
 * - Singleton pattern for database instance
 * - DAO pattern for data access
 * - Content Values for SQL injection prevention
 *
 * Sample Data:
 * - Matches API credentials exactly for offline/fallback login
 * - Guest: guest@mail.com / password123
 * - Staff: staff@mail.com / password123
 * - Student: john.doe@example.com / test
 *
 * @author BSCS2509254
 * @version 3.0 (API Credential Sync)
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Database Info
    private static final String DATABASE_NAME = "RestaurantPro.db";
    private static final int DATABASE_VERSION = 2; // Incremented for new sample data

    // Singleton instance
    private static DatabaseHelper instance;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_MENU_ITEMS = "menu_items";
    private static final String TABLE_RESERVATIONS = "reservations";

    // Common Column Names
    private static final String KEY_ID = "id";

    // Users Table Columns
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_USERTYPE = "usertype";

    // Menu Items Table Columns
    private static final String KEY_ITEM_ID = "item_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PRICE = "price";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_AVAILABLE = "available";

    // Reservations Table Columns
    private static final String KEY_RESERVATION_ID = "reservation_id";
    private static final String KEY_GUEST_NAME = "guest_name";
    private static final String KEY_GUEST_EMAIL = "guest_email";
    private static final String KEY_GUEST_CONTACT = "guest_contact";
    private static final String KEY_PARTY_SIZE = "party_size";
    private static final String KEY_DATE_TIME = "date_time";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_STATUS = "status";

    /**
     * Private constructor for Singleton pattern
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Get singleton instance of DatabaseHelper
     * Thread-safe implementation using double-checked locking
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Called when database is created for the first time
     * Creates all required tables
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables...");

        try {
            // Enable foreign key support
            db.execSQL("PRAGMA foreign_keys=ON;");

            // Create Users table
            String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_USER_ID + " INTEGER UNIQUE,"
                    + KEY_USERNAME + " TEXT UNIQUE,"
                    + KEY_PASSWORD + " TEXT,"
                    + KEY_FIRSTNAME + " TEXT,"
                    + KEY_LASTNAME + " TEXT,"
                    + KEY_EMAIL + " TEXT UNIQUE,"
                    + KEY_CONTACT + " TEXT,"
                    + KEY_USERTYPE + " TEXT"
                    + ")";
            db.execSQL(CREATE_USERS_TABLE);
            Log.d(TAG, "Users table created");

            // Create Menu Items table
            String CREATE_MENU_ITEMS_TABLE = "CREATE TABLE " + TABLE_MENU_ITEMS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_ITEM_ID + " INTEGER UNIQUE,"
                    + KEY_NAME + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_PRICE + " REAL,"
                    + KEY_CATEGORY + " TEXT,"
                    + KEY_IMAGE_URL + " TEXT,"
                    + KEY_AVAILABLE + " INTEGER"
                    + ")";
            db.execSQL(CREATE_MENU_ITEMS_TABLE);
            Log.d(TAG, "Menu items table created");

            // Create Reservations table
            String CREATE_RESERVATIONS_TABLE = "CREATE TABLE " + TABLE_RESERVATIONS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_RESERVATION_ID + " INTEGER UNIQUE,"
                    + KEY_USER_ID + " INTEGER,"
                    + KEY_GUEST_NAME + " TEXT,"
                    + KEY_GUEST_EMAIL + " TEXT,"
                    + KEY_GUEST_CONTACT + " TEXT,"
                    + KEY_PARTY_SIZE + " INTEGER,"
                    + KEY_DATE_TIME + " INTEGER,"
                    + KEY_NOTES + " TEXT,"
                    + KEY_STATUS + " TEXT"
                    + ")";
            db.execSQL(CREATE_RESERVATIONS_TABLE);
            Log.d(TAG, "Reservations table created");

            Log.d(TAG, "Database tables created successfully");

            // Insert sample data matching API credentials
            insertSampleData(db);
        } catch (Exception e) {
            Log.e(TAG, "Error creating database tables: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create database tables", e);
        }
    }

    /**
     * Called when database needs to be upgraded
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);

        // Create tables again
        onCreate(db);
    }

    // ==================== USER OPERATIONS ====================

    /**
     * Add a new user to the database
     *
     * @param user User object to add
     * @return true if successful, false otherwise
     */
    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_ID, user.getUserId());
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_FIRSTNAME, user.getFirstname());
        values.put(KEY_LASTNAME, user.getLastname());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_CONTACT, user.getContact());
        values.put(KEY_USERTYPE, user.getUsertype());

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    /**
     * Get user by email and password (for login)
     */
    public User getUserByCredentials(String email, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        User user = null;

        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_USERS +
                    " WHERE " + KEY_EMAIL + " = ? AND " + KEY_PASSWORD + " = ?";

            cursor = db.rawQuery(query, new String[]{email, password});

            if (cursor.moveToFirst()) {
                user = cursorToUser(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by credentials: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return user;
    }

    /**
     * Get user by ID
     */
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }

        cursor.close();
        db.close();

        return user;
    }

    /**
     * Get all users from database
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                users.add(cursorToUser(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return users;
    }

    /**
     * Update user information
     */
    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_FIRSTNAME, user.getFirstname());
        values.put(KEY_LASTNAME, user.getLastname());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_CONTACT, user.getContact());
        values.put(KEY_USERTYPE, user.getUsertype());

        int rowsAffected = db.update(TABLE_USERS, values,
                KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getUserId())});
        db.close();

        return rowsAffected > 0;
    }

    /**
     * Delete user from database
     */
    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USERS,
                KEY_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();

        return rowsDeleted > 0;
    }

    /**
     * Convert cursor to User object
     */
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID)));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)));
        user.setFirstname(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FIRSTNAME)));
        user.setLastname(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LASTNAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
        user.setContact(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT)));
        user.setUsertype(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERTYPE)));
        return user;
    }

    // ==================== MENU ITEM OPERATIONS ====================

    /**
     * Add menu item to database
     */
    public boolean addMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_ITEM_ID, item.getItemId());
        values.put(KEY_NAME, item.getName());
        values.put(KEY_DESCRIPTION, item.getDescription());
        values.put(KEY_PRICE, item.getPrice());
        values.put(KEY_CATEGORY, item.getCategory());
        values.put(KEY_IMAGE_URL, item.getImageUrl());
        values.put(KEY_AVAILABLE, item.isAvailable() ? 1 : 0);

        long result = db.insert(TABLE_MENU_ITEMS, null, values);
        db.close();

        return result != -1;
    }

    /**
     * Get all menu items
     */
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_MENU_ITEMS + " ORDER BY " + KEY_CATEGORY + ", " + KEY_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToMenuItem(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return items;
    }

    /**
     * Get menu items by category
     */
    public List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> items = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_MENU_ITEMS +
                " WHERE " + KEY_CATEGORY + " = ? ORDER BY " + KEY_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToMenuItem(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return items;
    }

    /**
     * Search menu items by name
     */
    public List<MenuItem> searchMenuItems(String searchQuery) {
        List<MenuItem> items = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_MENU_ITEMS +
                " WHERE " + KEY_NAME + " LIKE ? OR " + KEY_DESCRIPTION + " LIKE ?" +
                " ORDER BY " + KEY_NAME;

        String searchPattern = "%" + searchQuery + "%";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{searchPattern, searchPattern});

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToMenuItem(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return items;
    }

    /**
     * Update menu item
     */
    public boolean updateMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, item.getName());
        values.put(KEY_DESCRIPTION, item.getDescription());
        values.put(KEY_PRICE, item.getPrice());
        values.put(KEY_CATEGORY, item.getCategory());
        values.put(KEY_IMAGE_URL, item.getImageUrl());
        values.put(KEY_AVAILABLE, item.isAvailable() ? 1 : 0);

        int rowsAffected = db.update(TABLE_MENU_ITEMS, values,
                KEY_ITEM_ID + " = ?",
                new String[]{String.valueOf(item.getItemId())});
        db.close();

        return rowsAffected > 0;
    }

    /**
     * Delete menu item
     */
    public boolean deleteMenuItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_MENU_ITEMS,
                KEY_ITEM_ID + " = ?",
                new String[]{String.valueOf(itemId)});
        db.close();

        return rowsDeleted > 0;
    }

    /**
     * Get menu items count
     */
    public int getMenuItemsCount() {
        String query = "SELECT COUNT(*) FROM " + TABLE_MENU_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    /**
     * Convert cursor to MenuItem object
     */
    private MenuItem cursorToMenuItem(Cursor cursor) {
        MenuItem item = new MenuItem();
        item.setItemId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ITEM_ID)));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
        item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
        item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PRICE)));
        item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)));
        item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_URL)));
        item.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AVAILABLE)) == 1);
        return item;
    }

    // ==================== RESERVATION OPERATIONS ====================

    /**
     * Add reservation to database
     */
    public long addReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_RESERVATION_ID, reservation.getReservationId());
        values.put(KEY_USER_ID, reservation.getUserId());
        values.put(KEY_GUEST_NAME, reservation.getGuestName());
        values.put(KEY_GUEST_EMAIL, reservation.getGuestEmail());
        values.put(KEY_GUEST_CONTACT, reservation.getGuestContact());
        values.put(KEY_PARTY_SIZE, reservation.getPartySize());
        values.put(KEY_DATE_TIME, reservation.getDateTime());
        values.put(KEY_NOTES, reservation.getNotes());
        values.put(KEY_STATUS, reservation.getStatus());

        long result = db.insert(TABLE_RESERVATIONS, null, values);
        db.close();

        return result;
    }

    /**
     * Get all reservations
     */
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_RESERVATIONS +
                " ORDER BY " + KEY_DATE_TIME + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                reservations.add(cursorToReservation(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return reservations;
    }

    /**
     * Get reservations by user ID
     */
    public List<Reservation> getReservationsByUserId(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_RESERVATIONS +
                " WHERE " + KEY_USER_ID + " = ?" +
                " ORDER BY " + KEY_DATE_TIME + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                reservations.add(cursorToReservation(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return reservations;
    }

    /**
     * Get recent reservations (limited)
     */
    public List<Reservation> getRecentReservations(int limit) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_RESERVATIONS +
                " ORDER BY " + KEY_DATE_TIME + " DESC LIMIT ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});

        if (cursor.moveToFirst()) {
            do {
                reservations.add(cursorToReservation(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return reservations;
    }

    /**
     * Get reservations count
     */
    public int getReservationsCount() {
        String query = "SELECT COUNT(*) FROM " + TABLE_RESERVATIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    /**
     * Get pending reservations count
     */
    public int getPendingReservationsCount() {
        String query = "SELECT COUNT(*) FROM " + TABLE_RESERVATIONS +
                " WHERE " + KEY_STATUS + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{"pending"});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    /**
     * Update reservation
     */
    public boolean updateReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_GUEST_NAME, reservation.getGuestName());
        values.put(KEY_GUEST_EMAIL, reservation.getGuestEmail());
        values.put(KEY_GUEST_CONTACT, reservation.getGuestContact());
        values.put(KEY_PARTY_SIZE, reservation.getPartySize());
        values.put(KEY_DATE_TIME, reservation.getDateTime());
        values.put(KEY_NOTES, reservation.getNotes());
        values.put(KEY_STATUS, reservation.getStatus());

        int rowsAffected = db.update(TABLE_RESERVATIONS, values,
                KEY_RESERVATION_ID + " = ?",
                new String[]{String.valueOf(reservation.getReservationId())});
        db.close();

        return rowsAffected > 0;
    }

    /**
     * Delete reservation
     */
    public boolean deleteReservation(int reservationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_RESERVATIONS,
                KEY_RESERVATION_ID + " = ?",
                new String[]{String.valueOf(reservationId)});
        db.close();

        return rowsDeleted > 0;
    }

    /**
     * Convert cursor to Reservation object
     */
    private Reservation cursorToReservation(Cursor cursor) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESERVATION_ID)));
        reservation.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID)));
        reservation.setGuestName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GUEST_NAME)));
        reservation.setGuestEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GUEST_EMAIL)));
        reservation.setGuestContact(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GUEST_CONTACT)));
        reservation.setPartySize(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PARTY_SIZE)));
        reservation.setDateTime(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_DATE_TIME)));
        reservation.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTES)));
        reservation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS)));
        return reservation;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Insert sample data for testing
     * UPDATED: Now matches API credentials exactly
     */

    private void insertSampleData(SQLiteDatabase db) {
        Log.d(TAG, "Inserting sample data (matching API credentials)...");

        // Sample Users - MATCHING API EXACTLY
        insertSampleUser(db, 1, "john_doe", "test", "John", "Doe",
                "john.doe@example.com", "1234567890", "student");

        insertSampleUser(db, 2, "guest_1", "password123", "WeiChyeh", "Ooi",
                "guest@mail.com", "0127306666", "guest");

        insertSampleUser(db, 3, "staff_1", "password123", "Admin", "RestaurantPro",
                "staff@mail.com", "047303344", "staff");

        // Sample Menu Items - USING LOCAL DRAWABLE IMAGES
        insertSampleMenuItem(db, 1, "Nasi Lemak",
                "Traditional Malaysian fragrant rice dish with sambal, anchovies, peanuts, and boiled egg",
                12.90, "Main Course", "nasi_lemak", true);

        insertSampleMenuItem(db, 2, "Roti Canai",
                "Crispy and fluffy flatbread served with dhal curry",
                4.50, "Appetizers", "roti_canai", true);

        insertSampleMenuItem(db, 3, "Satay",
                "Grilled meat skewers with peanut sauce",
                15.90, "Appetizers", "satay", true);

        insertSampleMenuItem(db, 4, "Char Kway Teow",
                "Stir-fried flat rice noodles with prawns, cockles, and bean sprouts",
                13.90, "Main Course", "char_kway_teow", true);

        insertSampleMenuItem(db, 5, "Cendol",
                "Sweet dessert with shaved ice, coconut milk, and green rice flour jelly",
                6.90, "Desserts", "cendol", true);

        insertSampleMenuItem(db, 6, "Teh Tarik",
                "Malaysian pulled tea with condensed milk",
                3.50, "Beverages", "teh_tarik", true);

        insertSampleMenuItem(db, 7, "Laksa",
                "Spicy noodle soup with coconut milk and seafood",
                14.90, "Main Course", "laksa", true);

        insertSampleMenuItem(db, 8, "Nasi Goreng",
                "Malaysian fried rice with egg, vegetables, and chicken",
                11.90, "Main Course", "nasi_goreng", true);

        Log.d(TAG, "Sample data inserted successfully (API credentials synced)");
    }

    private void insertSampleUser(SQLiteDatabase db, int userId, String username, String password,
                                  String firstname, String lastname, String email,
                                  String contact, String usertype) {
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_FIRSTNAME, firstname);
        values.put(KEY_LASTNAME, lastname);
        values.put(KEY_EMAIL, email);
        values.put(KEY_CONTACT, contact);
        values.put(KEY_USERTYPE, usertype);
        db.insert(TABLE_USERS, null, values);
        Log.d(TAG, "Inserted user: " + email + " (type: " + usertype + ")");
    }

    private void insertSampleMenuItem(SQLiteDatabase db, int itemId, String name,
                                      String description, double price, String category,
                                      String imageUrl, boolean available) {
        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_ID, itemId);
        values.put(KEY_NAME, name);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_PRICE, price);
        values.put(KEY_CATEGORY, category);
        values.put(KEY_IMAGE_URL, imageUrl);
        values.put(KEY_AVAILABLE, available ? 1 : 0);
        db.insert(TABLE_MENU_ITEMS, null, values);
    }

    /**
     * Clear all data from database
     */
    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
        db.delete(TABLE_MENU_ITEMS, null, null);
        db.delete(TABLE_RESERVATIONS, null, null);
        db.close();
        Log.d(TAG, "All data cleared from database");
    }
}