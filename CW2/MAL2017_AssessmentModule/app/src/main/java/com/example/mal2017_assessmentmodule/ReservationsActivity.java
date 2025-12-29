package com.example.mal2017_assessmentmodule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mal2017_assessmentmodule.database.DatabaseHelper;
import com.example.mal2017_assessmentmodule.models.Reservation;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * ReservationsActivity - FIXED Staff view of all reservations
 */
public class ReservationsActivity extends AppCompatActivity {

    private static final String TAG = "ReservationsActivity";

    private RecyclerView rvReservations;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout llEmptyState;
    private ChipGroup chipGroupFilters;
    private ReservationAdapter reservationAdapter;

    private DatabaseHelper dbHelper;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "onCreate started");
            setContentView(R.layout.activity_staff_view_reservations);

            dbHelper = DatabaseHelper.getInstance(this);

            setupToolbar();
            initializeViews();
            setupRecyclerView();
            setupSwipeRefresh();
            setupFilters();

            // Check for filter from intent
            String filterFromIntent = getIntent().getStringExtra("filter");
            if (filterFromIntent != null) {
                currentFilter = filterFromIntent;
            }

            loadReservations();

            Log.d(TAG, "onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading reservations: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupToolbar() {
        try {
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("All Reservations");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar: " + e.getMessage(), e);
        }
    }

    private void initializeViews() {
        try {
            rvReservations = findViewById(R.id.rv_reservations);
            swipeRefresh = findViewById(R.id.swipe_refresh);
            llEmptyState = findViewById(R.id.ll_empty_state);
            chipGroupFilters = findViewById(R.id.chip_group_filters);

            if (rvReservations == null) Log.e(TAG, "rvReservations is NULL");
            if (swipeRefresh == null) Log.e(TAG, "swipeRefresh is NULL");
            if (llEmptyState == null) Log.e(TAG, "llEmptyState is NULL");
            if (chipGroupFilters == null) Log.e(TAG, "chipGroupFilters is NULL");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
        }
    }

    private void setupRecyclerView() {
        try {
            if (rvReservations == null) {
                Log.e(TAG, "Cannot setup RecyclerView - it is NULL");
                return;
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rvReservations.setLayoutManager(layoutManager);
            rvReservations.setHasFixedSize(false);

            Log.d(TAG, "RecyclerView setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
        }
    }

    private void setupSwipeRefresh() {
        try {
            if (swipeRefresh != null) {
                swipeRefresh.setOnRefreshListener(() -> {
                    loadReservations();
                    swipeRefresh.setRefreshing(false);
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up swipe refresh: " + e.getMessage(), e);
        }
    }

    private void setupFilters() {
        try {
            if (chipGroupFilters == null) return;

            chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) return;

                int checkedId = checkedIds.get(0);

                if (checkedId == R.id.chip_all) {
                    currentFilter = "all";
                } else if (checkedId == R.id.chip_confirmed) {
                    currentFilter = Constants.STATUS_CONFIRMED;
                } else if (checkedId == R.id.chip_pending) {
                    currentFilter = Constants.STATUS_PENDING;
                } else if (checkedId == R.id.chip_cancelled) {
                    currentFilter = Constants.STATUS_CANCELLED;
                }

                Log.d(TAG, "Filter changed to: " + currentFilter);
                loadReservations();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up filters: " + e.getMessage(), e);
        }
    }

    private void loadReservations() {
        try {
            Log.d(TAG, "Loading reservations with filter: " + currentFilter);

            if (dbHelper == null) {
                dbHelper = DatabaseHelper.getInstance(this);
            }

            List<Reservation> allReservations = dbHelper.getAllReservations();
            Log.d(TAG, "Total reservations from DB: " + allReservations.size());

            List<Reservation> filteredReservations = new ArrayList<>();

            // Apply filter
            if (currentFilter.equals("all")) {
                filteredReservations = allReservations;
            } else {
                for (Reservation reservation : allReservations) {
                    if (reservation.getStatus().equalsIgnoreCase(currentFilter)) {
                        filteredReservations.add(reservation);
                    }
                }
            }

            Log.d(TAG, "Filtered reservations: " + filteredReservations.size());

            // Update UI
            if (filteredReservations.isEmpty()) {
                showEmptyState();
            } else {
                showReservations(filteredReservations);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading reservations: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            showEmptyState();
        }
    }

    private void showEmptyState() {
        try {
            if (rvReservations != null) {
                rvReservations.setVisibility(View.GONE);
            }
            if (llEmptyState != null) {
                llEmptyState.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing empty state: " + e.getMessage(), e);
        }
    }

    private void showReservations(List<Reservation> reservations) {
        try {
            if (rvReservations == null) {
                Log.e(TAG, "Cannot show reservations - RecyclerView is NULL");
                return;
            }

            rvReservations.setVisibility(View.VISIBLE);
            if (llEmptyState != null) {
                llEmptyState.setVisibility(View.GONE);
            }

            reservationAdapter = new ReservationAdapter(this, reservations);
            reservationAdapter.setOnItemClickListener(reservation -> {
                try {
                    Log.d(TAG, "Reservation clicked: " + reservation.getReservationId());
                    Intent intent = new Intent(ReservationsActivity.this, ReservationDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_RESERVATION_ID, reservation.getReservationId());
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening reservation detail: " + e.getMessage(), e);
                    Toast.makeText(ReservationsActivity.this, "Error opening reservation", Toast.LENGTH_SHORT).show();
                }
            });

            rvReservations.setAdapter(reservationAdapter);
            Log.d(TAG, "Adapter set with " + reservations.size() + " items");

        } catch (Exception e) {
            Log.e(TAG, "Error showing reservations: " + e.getMessage(), e);
            Toast.makeText(this, "Error displaying reservations", Toast.LENGTH_SHORT).show();
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
        try {
            Log.d(TAG, "onResume - reloading reservations");
            loadReservations();
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage(), e);
        }
    }
}