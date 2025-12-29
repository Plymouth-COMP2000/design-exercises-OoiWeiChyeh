package com.example.mal2017_assessmentmodule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * MakeReservationActivity - Reservation booking interface for guests.
 *
 * User-friendly form following HCI principles:
 * - Native date/time pickers prevent input errors
 * - Dropdowns for party size (faster than typing)
 * - Clear validation feedback
 * - Prominent confirmation button
 *
 * From usability testing:
 * - "Using native date pickers prevents input errors"
 * - "Dropdowns for time and party size are faster than typing"
 *
 * @author BSCS2509254
 * @version 1.0
 * @since 2025-11-19
 */
public class MakeReservationActivity extends AppCompatActivity {

    private TextInputEditText etGuestName, etDate, etTime, etNotes;
    private AutoCompleteTextView actPartySize;
    private Button btnConfirmReservation;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_reservation);

        selectedDate = Calendar.getInstance();

        initializeViews();
        setupToolbar();
        setupPartySizeDropdown();
        setupDateTimePickers();
        setupConfirmButton();
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
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        etDate.setText(sdf.format(selectedDate.getTime()));
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDate.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                        etTime.setText(sdf.format(selectedDate.getTime()));
                    }
                },
                selectedDate.get(Calendar.HOUR_OF_DAY),
                selectedDate.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void setupConfirmButton() {
        btnConfirmReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    confirmReservation();
                }
            }
        });
    }

    private boolean validateInputs() {
        // TODO (CW2): Implement full validation
        return true;
    }

    private void confirmReservation() {
        // TODO (CW2): Save reservation to database
        Toast.makeText(this, "Reservation confirmed!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}