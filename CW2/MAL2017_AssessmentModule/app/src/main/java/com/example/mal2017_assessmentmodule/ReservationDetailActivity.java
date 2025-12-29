package com.example.mal2017_assessmentmodule;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mal2017_assessmentmodule.database.DatabaseHelper;
import com.example.mal2017_assessmentmodule.models.Reservation;
import com.google.android.material.chip.Chip;

import java.util.List;

/**
 * ReservationDetailActivity - View and manage reservation details
 * Shows complete reservation info with status management
 */
public class ReservationDetailActivity extends AppCompatActivity {

    private static final String TAG = "ReservationDetail";
    private static final String CHANNEL_ID = "reservation_updates";

    private TextView tvGuestName, tvEmail, tvContact, tvPartySize, tvDateTime, tvNotes;
    private Chip chipStatus;
    private Button btnConfirm, btnCancel, btnComplete;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private Reservation reservation;
    private int reservationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        // Create notification channel
        createNotificationChannel();

        setupToolbar();
        initializeViews();
        loadReservationData();
        setupClickListeners();
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reservation Details");
        }
    }

    private void initializeViews() {
        tvGuestName = findViewById(R.id.tv_guest_name);
        tvEmail = findViewById(R.id.tv_email);
        tvContact = findViewById(R.id.tv_contact);
        tvPartySize = findViewById(R.id.tv_party_size);
        tvDateTime = findViewById(R.id.tv_date_time);
        tvNotes = findViewById(R.id.tv_notes);
        chipStatus = findViewById(R.id.chip_status);

        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        btnComplete = findViewById(R.id.btn_complete);
    }

    private void loadReservationData() {
        try {
            reservationId = getIntent().getIntExtra(Constants.EXTRA_RESERVATION_ID, -1);

            if (reservationId == -1) {
                Toast.makeText(this, "Invalid reservation", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Load reservation from database
            List<Reservation> allReservations = dbHelper.getAllReservations();
            for (Reservation r : allReservations) {
                if (r.getReservationId() == reservationId) {
                    reservation = r;
                    break;
                }
            }

            if (reservation == null) {
                Toast.makeText(this, "Reservation not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            displayReservationData();
        } catch (Exception e) {
            Log.e(TAG, "Error loading reservation: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayReservationData() {
        try {
            tvGuestName.setText(reservation.getGuestName());
            tvEmail.setText(reservation.getGuestEmail());
            tvContact.setText(reservation.getGuestContact());
            tvPartySize.setText(reservation.getPartySizeText());
            tvDateTime.setText(reservation.getFormattedDate() + " " + reservation.getFormattedTime());

            if (reservation.getNotes() != null && !reservation.getNotes().isEmpty()) {
                tvNotes.setText(reservation.getNotes());
                tvNotes.setVisibility(View.VISIBLE);
            } else {
                tvNotes.setText("No special requests");
                tvNotes.setVisibility(View.VISIBLE);
            }

            // Set status chip
            chipStatus.setText(reservation.getStatus().toUpperCase());
            setStatusChipColor(reservation.getStatus());

            // Show/hide action buttons based on status and user type
            updateActionButtons();
        } catch (Exception e) {
            Log.e(TAG, "Error displaying reservation: " + e.getMessage(), e);
        }
    }

    private void setStatusChipColor(String status) {
        int colorResId;
        switch (status.toLowerCase()) {
            case "confirmed":
                colorResId = R.color.status_confirmed;
                break;
            case "pending":
                colorResId = R.color.status_pending;
                break;
            case "cancelled":
                colorResId = R.color.status_cancelled;
                break;
            case "completed":
                colorResId = R.color.status_completed;
                break;
            default:
                colorResId = R.color.status_pending;
        }
        chipStatus.setChipBackgroundColorResource(colorResId);
    }

    private void updateActionButtons() {
        boolean isStaff = sessionManager.isStaff();
        String status = reservation.getStatus().toLowerCase();

        if (isStaff) {
            // Staff can manage reservations
            switch (status) {
                case "pending":
                    btnConfirm.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnComplete.setVisibility(View.GONE);
                    break;
                case "confirmed":
                    btnConfirm.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnComplete.setVisibility(View.VISIBLE);
                    break;
                case "cancelled":
                case "completed":
                    btnConfirm.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    btnComplete.setVisibility(View.GONE);
                    break;
            }
        } else {
            // Guests can only cancel pending/confirmed reservations
            if (status.equals("pending") || status.equals("confirmed")) {
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                btnCancel.setVisibility(View.GONE);
            }
            btnConfirm.setVisibility(View.GONE);
            btnComplete.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        btnConfirm.setOnClickListener(v -> updateReservationStatus("confirmed"));
        btnCancel.setOnClickListener(v -> updateReservationStatus("cancelled"));
        btnComplete.setOnClickListener(v -> updateReservationStatus("completed"));
    }

    private void updateReservationStatus(String newStatus) {
        reservation.setStatus(newStatus);
        boolean success = dbHelper.updateReservation(reservation);

        if (success) {
            // Send notification
            sendStatusUpdateNotification(newStatus);

            Toast.makeText(this, "Reservation " + newStatus, Toast.LENGTH_SHORT).show();
            displayReservationData();
        } else {
            Toast.makeText(this, "Failed to update reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reservation Updates";
            String description = "Notifications for reservation status updates";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendStatusUpdateNotification(String status) {
        try {
            // Check permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Notification permission not granted");
                    return;
                }
            }

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            String title = "Reservation " + status.substring(0, 1).toUpperCase() + status.substring(1);
            String message = "Your reservation for " + reservation.getPartySizeText() +
                    " on " + reservation.getFormattedDate() +
                    " at " + reservation.getFormattedTime() +
                    " has been " + status + ".";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_reservations)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL);

            notificationManager.notify(reservation.getReservationId(), builder.build());

            Log.d(TAG, "Notification sent: " + title);
        } catch (Exception e) {
            Log.e(TAG, "Error sending notification: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}