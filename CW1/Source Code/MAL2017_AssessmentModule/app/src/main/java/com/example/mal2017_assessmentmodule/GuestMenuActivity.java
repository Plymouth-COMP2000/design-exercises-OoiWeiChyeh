package com.example.mal2017_assessmentmodule;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

/**
 * GuestMenuActivity - Menu browsing interface for restaurant guests.
 *
 * This activity provides an attractive, user-friendly interface for guests to:
 * - Browse restaurant menu items
 * - View item details, prices, and images
 * - Filter by category
 * - Make reservations directly from menu
 *
 * Design Focus:
 * - Visual appeal with large, appetizing food images
 * - Easy navigation with category tabs
 * - Clear pricing and descriptions
 * - Prominent "Make Reservation" call-to-action
 *
 * User Journey (from storyboard):
 * 1. Farah opens app during lunch break
 * 2. Sees warm welcome with gradient design
 * 3. Taps "Browse Menu" to see food photos with prices
 * 4. After browsing, taps "Make Reservation" button
 *
 * @author BSCS2509254
 * @version 1.0
 * @since 2025-11-19
 */
public class GuestMenuActivity extends AppCompatActivity {

    private Button btnMakeReservation;
    private TabLayout tabLayout;
    private RecyclerView rvMenuItems;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_menu);

        initializeViews();
        setupTabLayout();
        setupRecyclerView();
        setupClickListeners();
        setupBottomNavigation();
        loadMenuItems();
    }

    private void initializeViews() {
        btnMakeReservation = findViewById(R.id.btn_make_reservation);
        tabLayout = findViewById(R.id.tab_layout);
        rvMenuItems = findViewById(R.id.rv_menu_items);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Filter menu items by selected category
                filterByCategory(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvMenuItems.setLayoutManager(layoutManager);
        // TODO (CW2): Set adapter with menu data
    }

    private void setupClickListeners() {
        btnMakeReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestMenuActivity.this, MakeReservationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_menu);

        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_menu) {
                    return true;
                } else if (itemId == R.id.nav_reservations) {
                    Intent intent = new Intent(GuestMenuActivity.this, MyReservationsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(GuestMenuActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });
    }

    private void loadMenuItems() {
        // TODO (CW2): Load menu items from database
    }

    private void filterByCategory(int categoryPosition) {
        // TODO (CW2): Filter menu items by category
    }
}