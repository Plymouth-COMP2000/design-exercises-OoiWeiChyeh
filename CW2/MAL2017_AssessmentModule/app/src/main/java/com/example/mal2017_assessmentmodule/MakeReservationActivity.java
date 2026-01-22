package com.example.mal2017_assessmentmodule;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mal2017_assessmentmodule.database.DatabaseHelper;
import com.example.mal2017_assessmentmodule.models.Reservation;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * MakeReservationActivity - FIXED with push notifications
 */
public class MakeReservationActivity extends AppCompatActivity {

    private static final String TAG = "MakeReservation";
    private static final String CHANNEL_ID = "reservation_confirmations";
    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    private TextInputEditText etGuestName, etDate, etTime, etNotes;
    private AutoCompleteTextView actPartySize;
    private Button btnConfirmReservation;
    private Calendar selectedDate;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_make_reservation);

        selectedDate = Calendar.getInstance();
        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        // Create notification channel
        createNotificationChannel();

        // Request notification permission for Android 13+
        requestNotificationPermission();

        initializeViews();
        setupToolbar();
        setupPartySizeDropdown();
        setupDateTimePickers();
        setupConfirmButton();
        prefillUserData();
    }

    private void initializeViews() {
        etGuestName = findViewById(R.id.et_guest_name);
        actPartySize = findViewById(R.id.act_party_size);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        etNotes = findViewById(R.id.et_notes);
        btnConfirmReservation = findViewById(R.id.btn_confirm_reservation);
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Make Reservation");
        }
    }

    private void setupPartySizeDropdown() {
        String[] partySizes = {"1 person", "2 people", "3 people", "4 people",
                "5 people", "6 people", "7 people", "8 people", "8+ people"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, partySizes);
        actPartySize.setAdapter(adapter);
    }

    private void setupDateTimePickers() {
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
    }

    private void prefillUserData() {
        if (sessionManager.isLoggedIn()) {
            String fullName = sessionManager.getLoggedInUser().getFullName();
            etGuestName.setText(fullName);
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    etDate.setText(sdf.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 3);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    if (hourOfDay < 10 || hourOfDay >= 22) {
                        Toast.makeText(MakeReservationActivity.this,
                                "Please select a time between 10:00 AM and 10:00 PM",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDate.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    etTime.setText(sdf.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.HOUR_OF_DAY),
                selectedDate.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void setupConfirmButton() {
        btnConfirmReservation.setOnClickListener(v -> {
            if (validateInputs()) {
                confirmReservation();
            }
        });
    }

    private boolean validateInputs() {
        String guestName = etGuestName.getText().toString().trim();
        String partySize = actPartySize.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();

        if (TextUtils.isEmpty(guestName)) {
            etGuestName.setError("Guest name is required");
            etGuestName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(partySize)) {
            actPartySize.setError("Party size is required");
            actPartySize.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(date)) {
            etDate.setError("Date is required");
            etDate.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(time)) {
            etTime.setError("Time is required");
            etTime.requestFocus();
            return false;
        }

        Calendar now = Calendar.getInstance();
        if (selectedDate.before(now)) {
            Toast.makeText(this, "Please select a future date and time", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void confirmReservation() {
        btnConfirmReservation.setEnabled(false);
        btnConfirmReservation.setText("Confirming...");

        String guestName = etGuestName.getText().toString().trim();
        String partySizeStr = actPartySize.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        int partySize = parsePartySize(partySizeStr);

        int userId = sessionManager.isLoggedIn() ?
                sessionManager.getLoggedInUser().getUserId() : 0;

        String guestEmail = sessionManager.isLoggedIn() ?
                sessionManager.getLoggedInUser().getEmail() : "";
        String guestContact = sessionManager.isLoggedIn() ?
                sessionManager.getLoggedInUser().getContact() : "";

        // Generate reservation ID
        int reservationId = (int) (System.currentTimeMillis() % 100000);

        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setUserId(userId);
        reservation.setGuestName(guestName);
        reservation.setGuestEmail(guestEmail);
        reservation.setGuestContact(guestContact);
        reservation.setPartySize(partySize);
        reservation.setDateTime(selectedDate.getTimeInMillis());
        reservation.setNotes(notes);
        reservation.setStatus(Constants.STATUS_PENDING);

        long result = dbHelper.addReservation(reservation);

        if (result != -1) {
            // Send notification
            sendReservationConfirmationNotification(reservation);

            Toast.makeText(this,
                    "Reservation confirmed successfully!",
                    Toast.LENGTH_LONG).show();

            finish();
        } else {
            Toast.makeText(this,
                    "Failed to save reservation. Please try again.",
                    Toast.LENGTH_SHORT).show();

            btnConfirmReservation.setEnabled(true);
            btnConfirmReservation.setText("Confirm Reservation");
        }
    }

    private int parsePartySize(String partySizeStr) {
        try {
            String[] parts = partySizeStr.split(" ");
            if (parts[0].equals("8+")) {
                return 8;
            }
            return Integer.parseInt(parts[0]);
        } catch (Exception e) {
            return 1;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reservation Confirmations";
            String description = "Notifications for reservation confirmations";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.enableLights(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted");
            } else {
                Log.d(TAG, "Notification permission denied");
                Toast.makeText(this, "Enable notifications to receive reservation updates", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendReservationConfirmationNotification(Reservation reservation) {
        try {
            // Check permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Notification permission not granted");
                    Toast.makeText(this, "Enable notifications in settings to receive updates", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // Create intent to open MyReservationsActivity when notification is clicked
            Intent intent = new Intent(this, MyReservationsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            String title = "Reservation Confirmed!";
            String message = String.format(
                    "Your reservation for %s on %s at %s has been confirmed. Status: Pending staff confirmation.",
                    reservation.getPartySizeText(),
                    reservation.getFormattedDate(),
                    reservation.getFormattedTime()
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_reservations)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setVibrate(new long[]{0, 500, 250, 500});

            notificationManager.notify(reservation.getReservationId(), builder.build());

            Log.d(TAG, "Notification sent successfully: " + title);
            Toast.makeText(this, "Notification sent! Check your notification bar.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error sending notification: " + e.getMessage(), e);
            Toast.makeText(this, "Could not send notification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}