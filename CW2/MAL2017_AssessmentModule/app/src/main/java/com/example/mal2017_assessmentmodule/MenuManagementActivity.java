package com.example.mal2017_assessmentmodule;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mal2017_assessmentmodule.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

/**
 * MenuManagementActivity - Enhanced with database integration.
 *
 * @author BSCS2509254
 * @version 2.0
 */
public class MenuManagementActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnFilter;
    private RecyclerView rvMenuItems;
    private ExtendedFloatingActionButton fabAddMenuItem;
    private BottomNavigationView bottomNavigation;

    private MenuItemAdapter menuItemAdapter;
    private DatabaseHelper dbHelper;
    private String currentCategory = Constants.CATEGORY_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu_management);

        dbHelper = DatabaseHelper.getInstance(this);

        setupToolbar();
        initializeViews();
        setupSearch();
        setupRecyclerView();
        setupClickListeners();
        setupBottomNavigation();
        loadMenuItems();
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initializeViews() {
        etSearch = findViewById(R.id.et_search);
        btnFilter = findViewById(R.id.btn_filter);
        rvMenuItems = findViewById(R.id.rv_menu_items);
        fabAddMenuItem = findViewById(R.id.fab_add_menu_item);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (menuItemAdapter != null) {
                    menuItemAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvMenuItems.setLayoutManager(layoutManager);
        rvMenuItems.setHasFixedSize(true);
    }

    private void setupClickListeners() {
        fabAddMenuItem.setOnClickListener(v -> {
            Intent intent = new Intent(MenuManagementActivity.this, AddEditMenuItemActivity.class);
            intent.putExtra(Constants.EXTRA_MODE, Constants.MODE_ADD);
            startActivity(intent);
        });

        btnFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_menu);

        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_dashboard) {
                    finish();
                    return true;
                } else if (itemId == R.id.nav_menu) {
                    return true;
                } else if (itemId == R.id.nav_reservations) {
                    startActivity(new Intent(MenuManagementActivity.this, ReservationsActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(MenuManagementActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }

                return false;
            }
        });
    }

    /**
     * Load menu items from database
     */
    private void loadMenuItems() {
        List<com.example.mal2017_assessmentmodule.models.MenuItem> items;

        if (currentCategory.equals(Constants.CATEGORY_ALL)) {
            items = dbHelper.getAllMenuItems();
        } else {
            items = dbHelper.getMenuItemsByCategory(currentCategory);
        }

        menuItemAdapter = new MenuItemAdapter(this, items);
        menuItemAdapter.setOnItemClickListener(item -> {
            // Open edit screen
            Intent intent = new Intent(MenuManagementActivity.this, AddEditMenuItemActivity.class);
            intent.putExtra(Constants.EXTRA_MODE, Constants.MODE_EDIT);
            intent.putExtra(Constants.EXTRA_ITEM_ID, item.getItemId());
            startActivity(intent);
        });

        rvMenuItems.setAdapter(menuItemAdapter);
    }

    /**
     * Show category filter dialog
     */
    private void showFilterDialog() {
        String[] categories = {
                Constants.CATEGORY_ALL,
                Constants.CATEGORY_APPETIZERS,
                Constants.CATEGORY_MAIN_COURSE,
                Constants.CATEGORY_DESSERTS,
                Constants.CATEGORY_BEVERAGES
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle("Filter by Category")
                .setSingleChoiceItems(categories, getCurrentCategoryIndex(), (dialog, which) -> {
                    currentCategory = categories[which];
                    loadMenuItems();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int getCurrentCategoryIndex() {
        switch (currentCategory) {
            case Constants.CATEGORY_ALL: return 0;
            case Constants.CATEGORY_APPETIZERS: return 1;
            case Constants.CATEGORY_MAIN_COURSE: return 2;
            case Constants.CATEGORY_DESSERTS: return 3;
            case Constants.CATEGORY_BEVERAGES: return 4;
            default: return 0;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenuItems();
    }
}