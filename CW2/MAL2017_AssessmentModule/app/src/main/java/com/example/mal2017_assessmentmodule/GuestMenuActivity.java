package com.example.mal2017_assessmentmodule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mal2017_assessmentmodule.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * GuestMenuActivity - COMPLETE with full navigation
 */
public class GuestMenuActivity extends AppCompatActivity {

    private static final String TAG = "GuestMenuActivity";

    private Button btnMakeReservation;
    private TabLayout tabLayout;
    private RecyclerView rvMenuItems;
    private BottomNavigationView bottomNavigation;

    private MenuItemAdapter menuItemAdapter;
    private DatabaseHelper dbHelper;
    private String currentCategory = Constants.CATEGORY_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "onCreate started");
            setContentView(R.layout.activity_guest_dashboard);

            dbHelper = DatabaseHelper.getInstance(this);

            initializeViews();
            setupTabLayout();
            setupRecyclerView();
            setupClickListeners();
            setupBottomNavigation();
            loadMenuItems();

            Log.d(TAG, "onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading menu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            btnMakeReservation = findViewById(R.id.btn_make_reservation);
            tabLayout = findViewById(R.id.tab_layout);
            rvMenuItems = findViewById(R.id.rv_menu_items);
            bottomNavigation = findViewById(R.id.bottom_navigation);

            if (btnMakeReservation == null) Log.e(TAG, "btnMakeReservation is NULL");
            if (tabLayout == null) Log.e(TAG, "tabLayout is NULL");
            if (rvMenuItems == null) Log.e(TAG, "rvMenuItems is NULL");
            if (bottomNavigation == null) Log.e(TAG, "bottomNavigation is NULL");

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
        }
    }

    private void setupTabLayout() {
        try {
            if (tabLayout == null) return;

            tabLayout.addTab(tabLayout.newTab().setText("All"));
            tabLayout.addTab(tabLayout.newTab().setText("Appetizers"));
            tabLayout.addTab(tabLayout.newTab().setText("Main Course"));
            tabLayout.addTab(tabLayout.newTab().setText("Desserts"));
            tabLayout.addTab(tabLayout.newTab().setText("Beverages"));

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    filterByCategory(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up tabs: " + e.getMessage(), e);
        }
    }

    private void setupRecyclerView() {
        try {
            if (rvMenuItems == null) {
                Log.e(TAG, "RecyclerView is NULL!");
                return;
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rvMenuItems.setLayoutManager(layoutManager);
            rvMenuItems.setHasFixedSize(true);

            Log.d(TAG, "RecyclerView setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            if (btnMakeReservation != null) {
                btnMakeReservation.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(GuestMenuActivity.this, MakeReservationActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting MakeReservationActivity: " + e.getMessage(), e);
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void setupBottomNavigation() {
        try {
            if (bottomNavigation == null) return;

            bottomNavigation.setSelectedItemId(R.id.nav_menu);

            bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    try {
                        int itemId = item.getItemId();

                        if (itemId == R.id.nav_menu) {
                            return true;
                        } else if (itemId == R.id.nav_reservations) {
                            startActivity(new Intent(GuestMenuActivity.this, MyReservationsActivity.class));
                            return true;
                        } else if (itemId == R.id.nav_profile) {
                            // FIXED: Navigate to ProfileActivity
                            startActivity(new Intent(GuestMenuActivity.this, ProfileActivity.class));
                            return true;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Navigation error: " + e.getMessage(), e);
                        Toast.makeText(GuestMenuActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up navigation: " + e.getMessage(), e);
        }
    }

    private void loadMenuItems() {
        try {
            Log.d(TAG, "Loading menu items for category: " + currentCategory);

            List<com.example.mal2017_assessmentmodule.models.MenuItem> items;

            if (currentCategory.equals(Constants.CATEGORY_ALL)) {
                items = dbHelper.getAllMenuItems();
            } else {
                items = dbHelper.getMenuItemsByCategory(currentCategory);
            }

            Log.d(TAG, "Found " + items.size() + " menu items");

            if (items.isEmpty()) {
                items = new ArrayList<>();
                Toast.makeText(this, "No menu items found", Toast.LENGTH_SHORT).show();
            }

            menuItemAdapter = new MenuItemAdapter(this, items);
            menuItemAdapter.setOnItemClickListener(item -> {
                Toast.makeText(this, "Selected: " + item.getName(), Toast.LENGTH_SHORT).show();
            });

            if (rvMenuItems != null) {
                rvMenuItems.setAdapter(menuItemAdapter);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading menu items: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading menu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void filterByCategory(int categoryPosition) {
        try {
            switch (categoryPosition) {
                case 0:
                    currentCategory = Constants.CATEGORY_ALL;
                    break;
                case 1:
                    currentCategory = Constants.CATEGORY_APPETIZERS;
                    break;
                case 2:
                    currentCategory = Constants.CATEGORY_MAIN_COURSE;
                    break;
                case 3:
                    currentCategory = Constants.CATEGORY_DESSERTS;
                    break;
                case 4:
                    currentCategory = Constants.CATEGORY_BEVERAGES;
                    break;
            }

            loadMenuItems();
        } catch (Exception e) {
            Log.e(TAG, "Error filtering: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadMenuItems();
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage(), e);
        }
    }
}