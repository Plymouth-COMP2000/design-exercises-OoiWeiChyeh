package com.example.mal2017_assessmentmodule;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

/**
 * StaffDashboardActivity - Main dashboard for restaurant staff members.
 *
 * This activity serves as the central hub for staff operations, providing:
 * - Overview statistics (total reservations, menu items, pending bookings)
 * - Quick access to main functions (menu management, reservations)
 * - Recent reservations list
 * - Notification badge for new bookings
 *
 * Design Philosophy:
 * - Large touch targets for busy restaurant environment (Fitt's Law principle)
 * - Color-coded visual indicators for quick scanning
 * - Minimal navigation depth - most functions accessible within 2 taps
 * - Real-time updates for reservations and notifications
 *
 * HCI Principles Applied:
 * 1. Visibility - All key metrics visible at once
 * 2. Feedback - Visual responses to all user actions
 * 3. Consistency - Layout matches other staff screens
 * 4. Error Prevention - Confirmation dialogs for destructive actions
 *
 * Performance Considerations:
 * - RecyclerView for efficient list rendering
 * - Lazy loading for reservation data
 * - Caching of frequently accessed data
 *
 * @author BSCS2509254
 * @version 1.0
 * @since 2025-11-19
 */
public class StaffDashboardActivity extends AppCompatActivity {

    // UI Components - Statistics
    private TextView tvWelcomeMessage;
    private TextView tvTotalReservations;
    private TextView tvMenuItemsCount;
    private TextView tvPendingCount;
    private TextView tvNotificationCount;

    // UI Components - Quick Actions
    private MaterialCardView cardManageMenu;
    private MaterialCardView cardViewReservations;

    // UI Components - Navigation
    private BottomNavigationView bottomNavigation;

    // RecyclerView for recent reservations
    private RecyclerView rvRecentReservations;
    private ReservationAdapter reservationAdapter;

    /**
     * Called when activity is created.
     * Initializes UI components and loads dashboard data.
     *
     * @param savedInstanceState Saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        // Initialize all views
        initializeViews();

        // Set up bottom navigation
        setupBottomNavigation();

        // Set up click listeners for quick actions
        setupClickListeners();

        // Load dashboard data
        loadDashboardData();

        // Set up RecyclerView for recent reservations
        setupRecyclerView();
    }

    /**
     * Initializes all view references.
     * Groups related views together for better code organization.
     */
    private void initializeViews() {
        // Welcome section
        tvWelcomeMessage = findViewById(R.id.tv_welcome_message);

        // Statistics
        tvTotalReservations = findViewById(R.id.tv_total_reservations);
        tvMenuItemsCount = findViewById(R.id.tv_menu_items_count);
        tvPendingCount = findViewById(R.id.tv_pending_count);
        tvNotificationCount = findViewById(R.id.tv_notification_count);

        // Quick action cards
        cardManageMenu = findViewById(R.id.card_manage_menu);
        cardViewReservations = findViewById(R.id.card_view_reservations);

        // Navigation
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // RecyclerView
        rvRecentReservations = findViewById(R.id.rv_recent_reservations);
    }

    /**
     * Sets up bottom navigation with item selection handling.
     *
     * Navigation Pattern:
     * - Dashboard: Current screen (no action needed)
     * - Menu: Navigate to MenuManagementActivity
     * - Reservations: Navigate to ReservationsActivity
     * - Profile: Navigate to ProfileActivity
     *
     * Design Decision:
     * Using bottom navigation provides:
     * - Easy thumb reach on mobile devices
     * - Persistent access to main sections
     * - Clear indication of current section
     */
    private void setupBottomNavigation() {
        // Set Dashboard as selected by default
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);

        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_dashboard) {
                    // Already on dashboard, no action needed
                    return true;
                } else if (itemId == R.id.nav_menu) {
                    // Navigate to menu management
                    Intent intent = new Intent(StaffDashboardActivity.this, MenuManagementActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_reservations) {
                    // Navigate to reservations
                    Intent intent = new Intent(StaffDashboardActivity.this, ReservationsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Navigate to profile
                    Intent intent = new Intent(StaffDashboardActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });
    }

    /**
     * Sets up click listeners for interactive elements.
     *
     * Material Design Ripple Effect:
     * - All clickable cards have ripple feedback
     * - Provides immediate visual confirmation of touch
     * - Enhances perceived responsiveness
     */
    private void setupClickListeners() {
        // Manage Menu card
        cardManageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffDashboardActivity.this, MenuManagementActivity.class);
                startActivity(intent);
            }
        });

        // View Reservations card
        cardViewReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffDashboardActivity.this, ReservationsActivity.class);
                startActivity(intent);
            }
        });

        // Notification badge click
        findViewById(R.id.fl_notification_badge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO (CW2): Open notifications activity or show bottom sheet
                showNotifications();
            }
        });

        // View All reservations link
        findViewById(R.id.tv_view_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffDashboardActivity.this, ReservationsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Loads dashboard data from database or API.
     *
     * TODO (CW2): Replace mock data with actual database queries
     *
     * Implementation Plan:
     * 1. Create DatabaseHelper class for SQLite operations
     * 2. Define data models (Reservation, MenuItem, etc.)
     * 3. Query local database for statistics
     * 4. Sync with API for latest data
     * 5. Update UI with real data
     *
     * Data Flow:
     * Database/API → Process Data → Update UI → Cache Results
     *
     * Why cache results:
     * - Faster subsequent loads
     * - Reduces database queries
     * - Works offline
     * - Better user experience
     */
    private void loadDashboardData() {
        /*
         * Mock Data for UI demonstration
         * In CW2, this will be replaced with actual database queries:
         *
         * int totalReservations = dbHelper.getReservationCount();
         * int menuItems = dbHelper.getMenuItemCount();
         * int pendingBookings = dbHelper.getPendingBookingsCount();
         */

        // Set welcome message with staff name
        // TODO (CW2): Get actual staff name from SharedPreferences or database
        tvWelcomeMessage.setText("Welcome, Wayden");

        // Update statistics with mock data
        tvTotalReservations.setText("24");
        tvMenuItemsCount.setText("45");
        tvPendingCount.setText("8");

        // Update notification badge
        int notificationCount = 3; // Mock value
        if (notificationCount > 0) {
            tvNotificationCount.setVisibility(View.VISIBLE);
            tvNotificationCount.setText(String.valueOf(notificationCount));
        } else {
            tvNotificationCount.setVisibility(View.GONE);
        }
    }

    /**
     * Sets up RecyclerView for displaying recent reservations.
     *
     * RecyclerView Advantages:
     * - Efficient memory usage through view recycling
     * - Smooth scrolling even with large datasets
     * - Easy to add animations and decorations
     * - Better performance than ListView
     *
     * Layout Manager Choice:
     * - LinearLayoutManager for vertical list
     * - Could switch to GridLayoutManager for tablet layouts
     *
     * Why limit to recent items:
     * - Dashboard should show summary, not everything
     * - Keeps screen uncluttered
     * - Full list available through dedicated screen
     */
    private void setupRecyclerView() {
        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvRecentReservations.setLayoutManager(layoutManager);

        // Disable nested scrolling for better performance in ScrollView
        rvRecentReservations.setNestedScrollingEnabled(false);

        /*
         * TODO (CW2): Load actual reservation data
         *
         * List<Reservation> recentReservations = dbHelper.getRecentReservations(5);
         * reservationAdapter = new ReservationAdapter(this, recentReservations);
         * rvRecentReservations.setAdapter(reservationAdapter);
         */

        // For now, we'll just set up the adapter structure
        // The adapter will be implemented in a separate file
    }

    /**
     * Shows notifications to the user.
     *
     * TODO (CW2): Implement notification display
     *
     * Options for Implementation:
     * 1. Bottom Sheet Dialog - Material Design recommended
     * 2. New Activity - For complex notification management
     * 3. Notification Center Fragment - Embedded view
     *
     * Notification Types:
     * - New reservations
     * - Reservation modifications
     * - Cancellations
     * - System alerts
     */
    private void showNotifications() {
        // Placeholder implementation
        // TODO (CW2): Show notification bottom sheet or activity
    }

    /**
     * Called when activity resumes (comes to foreground).
     * Refresh data to show latest information.
     *
     * Why override onResume:
     * - User might return from another screen with updated data
     * - Ensures dashboard always shows current state
     * - Good practice for data-driven screens
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh dashboard data when returning to this screen
        loadDashboardData();
    }
}