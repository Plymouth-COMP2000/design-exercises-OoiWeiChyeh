package com.example.mal2017_assessmentmodule;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * ReservationsActivity - Full list of reservations for staff.
 * Placeholder implementation for CW1.
 */
public class ReservationsActivity extends AppCompatActivity {

    private RecyclerView rvReservations;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        rvReservations = findViewById(R.id.rv_reservations);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvReservations.setLayoutManager(layoutManager);
        // TODO (CW2): Set adapter
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO (CW2): Refresh data
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}