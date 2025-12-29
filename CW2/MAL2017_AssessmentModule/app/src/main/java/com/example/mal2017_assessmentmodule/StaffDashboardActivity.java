package com.example.mal2017_assessmentmodule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mal2017_assessmentmodule.database.DatabaseHelper;
import com.example.mal2017_assessmentmodule.models.Reservation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * StaffDashboardActivity - COMPLETE with notifications
 */
public class StaffDashboardActivity extends AppCompatActivity {

    private static final String TAG = "StaffDashboard";

    private TextView tvWelcomeMessage;
    private TextView tvTotalReservations;
    private TextView tvMenuItemsCount;
    private TextView tvPendingCount;
    private TextView tvNotificationCount;

    private MaterialCardView cardManageMenu;
    private MaterialCardView cardViewReservations;
    private BottomNavigationView bottomNavigation;
    private FrameLayout flNotificationBadge;

    private RecyclerView rvRecentReservations;
    private ReservationAdapter reservationAdapter;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "onCreate started");
            setContentView(R.layout.activity_staff_dashboard);

            dbHelper = DatabaseHelper.getInstance(this);
            sessionManager = new SessionManager(this);

            initializeViews();
            setupBottomNavigation();
            setupClickListeners();
            setupRecyclerView();
            loadDashboardData();

            Log.d(TAG, "onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading dashboard: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
            tvTotalReservations = findViewById(R.id.tv_total_reservations);
            tvMenuItemsCount = findViewById(R.id.tv_menu_items_count);
            tvPendingCount = findViewById(R.id.tv_pending_count);
            tvNotificationCount = findViewById(R.id.tv_notification_count);
            cardManageMenu = findViewById(R.id.card_manage_menu);
            cardViewReservations = findViewById(R.id.card_view_reservations);
            bottomNavigation = findViewById(R.id.bottom_navigation);
            rvRecentReservations = findViewById(R.id.rv_recent_reservations);
            flNotificationBadge = findViewById(R.id.fl_notification_badge);

            Log.d(TAG, "Views initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
        }
    }

    private void setupBottomNavigation() {
        try {
            if (bottomNavigation == null) return;

            bottomNavigation.setSelectedItemId(R.id.nav_dashboard);

            bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    try {
                        int itemId = item.getItemId();

                        if (itemId == R.id.nav_dashboard) {
                            return true;
                        } else if (itemId == R.id.nav_menu) {
                            startActivity(new Intent(StaffDashboardActivity.this, MenuManagementActivity.class));
                            return true;
                        } else if (itemId == R.id.nav_reservations) {
                            startActivity(new Intent(StaffDashboardActivity.this, ReservationsActivity.class));
                            return true;
                        } else if (itemId == R.id.nav_profile) {
                            // FIXED: Navigate to ProfileActivity
                            startActivity(new Intent(StaffDashboardActivity.this, ProfileActivity.class));
                            return true;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Navigation error: " + e.getMessage(), e);
                        Toast.makeText(StaffDashboardActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up navigation: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            if (cardManageMenu != null) {
                cardManageMenu.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(StaffDashboardActivity.this, MenuManagementActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error: " + e.getMessage(), e);
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (cardViewReservations != null) {
                cardViewReservations.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(StaffDashboardActivity.this, ReservationsActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error: " + e.getMessage(), e);
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // FIXED: Notification badge click
            if (flNotificationBadge != null) {
                flNotificationBadge.setOnClickListener(v -> showNotifications());
            }

            TextView viewAll = findViewById(R.id.tv_view_all);
            if (viewAll != null) {
                viewAll.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(StaffDashboardActivity.this, ReservationsActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error: " + e.getMessage(), e);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void loadDashboardData() {
        try {
            // Get current user
            if (sessionManager != null && sessionManager.getLoggedInUser() != null) {
                String firstname = sessionManager.getLoggedInUser().getFirstname();
                if (tvWelcomeMessage != null) {
                    tvWelcomeMessage.setText("Welcome, " + firstname);
                }
            }

            // Get statistics from database
            int totalReservations = dbHelper.getReservationsCount();
            int menuItems = dbHelper.getMenuItemsCount();
            int pendingBookings = dbHelper.getPendingReservationsCount();

            // Update UI
            if (tvTotalReservations != null) {
                tvTotalReservations.setText(String.valueOf(totalReservations));
            }
            if (tvMenuItemsCount != null) {
                tvMenuItemsCount.setText(String.valueOf(menuItems));
            }
            if (tvPendingCount != null) {
                tvPendingCount.setText(String.valueOf(pendingBookings));
            }

            // Update notification badge
            if (tvNotificationCount != null) {
                if (pendingBookings > 0) {
                    tvNotificationCount.setVisibility(View.VISIBLE);
                    tvNotificationCount.setText(String.valueOf(pendingBookings));
                } else {
                    tvNotificationCount.setVisibility(View.GONE);
                }
            }

            Log.d(TAG, "Dashboard data loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error loading dashboard data: " + e.getMessage(), e);
        }
    }

    private void setupRecyclerView() {
        try {
            if (rvRecentReservations == null) {
                Log.e(TAG, "RecyclerView is NULL");
                return;
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rvRecentReservations.setLayoutManager(layoutManager);
            rvRecentReservations.setNestedScrollingEnabled(false);

            // Load recent reservations (limit 5)
            List<Reservation> recentReservations = dbHelper.getRecentReservations(5);

            if (recentReservations == null || recentReservations.isEmpty()) {
                recentReservations = new ArrayList<>();
            }

            reservationAdapter = new ReservationAdapter(this, recentReservations);
            reservationAdapter.setOnItemClickListener(reservation -> {
                try {
                    // Navigate to reservation details
                    Intent intent = new Intent(StaffDashboardActivity.this, ReservationDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_RESERVATION_ID, reservation.getReservationId());
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening reservation: " + e.getMessage(), e);
                    Toast.makeText(StaffDashboardActivity.this, "Error opening reservation", Toast.LENGTH_SHORT).show();
                }
            });
            rvRecentReservations.setAdapter(reservationAdapter);

            Log.d(TAG, "RecyclerView setup with " + recentReservations.size() + " items");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
        }
    }

    private void showNotifications() {
        // Navigate to all reservations filtered by pending
        Intent intent = new Intent(this, ReservationsActivity.class);
        intent.putExtra("filter", "pending");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadDashboardData();
            setupRecyclerView();
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage(), e);
        }
    }
}