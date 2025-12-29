package com.example.mal2017_assessmentmodule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mal2017_assessmentmodule.database.DatabaseHelper;
import com.example.mal2017_assessmentmodule.models.Reservation;

import java.util.ArrayList;
import java.util.List;

/**
 * MyReservationsActivity - FIXED Display user's reservations
 */
public class MyReservationsActivity extends AppCompatActivity {

    private static final String TAG = "MyReservations";

    private RecyclerView rvReservations;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private ReservationAdapter reservationAdapter;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "onCreate started");
            setContentView(R.layout.activity_guest_view_reservations);

            dbHelper = DatabaseHelper.getInstance(this);
            sessionManager = new SessionManager(this);

            setupToolbar();
            initializeViews();
            setupRecyclerView();
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
                getSupportActionBar().setTitle("My Reservations");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar: " + e.getMessage(), e);
        }
    }

    private void initializeViews() {
        try {
            rvReservations = findViewById(R.id.rv_reservations);
            tvEmptyState = findViewById(R.id.tv_empty_state);
            progressBar = findViewById(R.id.progress_bar);

            if (rvReservations == null) Log.e(TAG, "rvReservations is NULL");
            if (tvEmptyState == null) Log.e(TAG, "tvEmptyState is NULL");
            if (progressBar == null) Log.e(TAG, "progressBar is NULL");
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

    private void loadReservations() {
        try {
            showLoading(true);

            if (sessionManager == null || !sessionManager.isLoggedIn()) {
                Log.e(TAG, "User not logged in");
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            int userId = sessionManager.getLoggedInUser().getUserId();
            Log.d(TAG, "Loading reservations for user ID: " + userId);

            if (dbHelper == null) {
                dbHelper = DatabaseHelper.getInstance(this);
            }

            List<Reservation> reservations = dbHelper.getReservationsByUserId(userId);

            if (reservations == null) {
                reservations = new ArrayList<>();
            }

            Log.d(TAG, "Found " + reservations.size() + " reservations");

            showLoading(false);

            if (reservations.isEmpty()) {
                showEmptyState();
            } else {
                showReservations(reservations);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading reservations: " + e.getMessage(), e);
            showLoading(false);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            showEmptyState();
        }
    }

    private void showEmptyState() {
        try {
            if (rvReservations != null) {
                rvReservations.setVisibility(View.GONE);
            }
            if (tvEmptyState != null) {
                tvEmptyState.setVisibility(View.VISIBLE);
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
            if (tvEmptyState != null) {
                tvEmptyState.setVisibility(View.GONE);
            }

            reservationAdapter = new ReservationAdapter(this, reservations);
            reservationAdapter.setOnItemClickListener(reservation -> {
                try {
                    Log.d(TAG, "Reservation clicked: " + reservation.getReservationId());
                    Intent intent = new Intent(MyReservationsActivity.this, ReservationDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_RESERVATION_ID, reservation.getReservationId());
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening reservation detail: " + e.getMessage(), e);
                    Toast.makeText(MyReservationsActivity.this, "Error opening reservation", Toast.LENGTH_SHORT).show();
                }
            });

            rvReservations.setAdapter(reservationAdapter);
            Log.d(TAG, "Adapter set with " + reservations.size() + " items");

        } catch (Exception e) {
            Log.e(TAG, "Error showing reservations: " + e.getMessage(), e);
            Toast.makeText(this, "Error displaying reservations", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean show) {
        try {
            if (progressBar != null) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error toggling loading: " + e.getMessage(), e);
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