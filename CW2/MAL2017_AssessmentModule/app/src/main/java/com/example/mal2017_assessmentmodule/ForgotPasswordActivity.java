package com.example.mal2017_assessmentmodule;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mal2017_assessmentmodule.database.DatabaseHelper;
import com.example.mal2017_assessmentmodule.models.User;
import com.example.mal2017_assessmentmodule.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

/**
 * ForgotPasswordActivity - Password reset functionality.
 *
 * Features:
 * - Email verification
 * - Security question (contact verification)
 * - Password reset
 * - Input validation
 *
 * Note: In production, this would send an email with reset link.
 * For this demo, we verify contact number as security check.
 *
 * @author BSCS2509254
 * @version 2.0
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPassword";

    // UI Components
    private TextInputLayout tilEmail, tilContact, tilNewPassword, tilConfirmPassword;
    private TextInputEditText etEmail, etContact, etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    private ProgressBar progressBar;

    // Database
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        dbHelper = DatabaseHelper.getInstance(this);

        setupToolbar();
        initializeViews();
        setupClickListeners();
    }

    /**
     * Setup toolbar with back button
     */
    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reset Password");
        }
    }

    /**
     * Initialize all view components
     */
    private void initializeViews() {
        tilEmail = findViewById(R.id.til_email);
        tilContact = findViewById(R.id.til_contact);
        tilNewPassword = findViewById(R.id.til_new_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        etEmail = findViewById(R.id.et_email);
        etContact = findViewById(R.id.et_contact);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        btnResetPassword = findViewById(R.id.btn_reset_password);
        progressBar = findViewById(R.id.progress_bar);
    }

    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        btnResetPassword.setOnClickListener(v -> handleResetPassword());
    }

    /**
     * Handle password reset
     * Verifies user identity and updates password
     */
    private void handleResetPassword() {
        // Clear previous errors
        clearErrors();

        // Get input values
        String email = etEmail.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(email, contact, newPassword, confirmPassword)) {
            return;
        }

        showLoading(true);

        // Process password reset in background thread
        new Thread(() -> {
            try {
                // Find user by email
                List<User> allUsers = dbHelper.getAllUsers();
                User user = null;

                for (User u : allUsers) {
                    if (u.getEmail().equalsIgnoreCase(email)) {
                        user = u;
                        break;
                    }
                }

                if (user == null) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        tilEmail.setError("Email not found");
                    });
                    return;
                }

                // Verify contact number matches
                if (!user.getContact().equals(contact)) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        tilContact.setError("Contact number does not match our records");
                    });
                    return;
                }

                // Update password
                user.setPassword(newPassword);
                boolean success = dbHelper.updateUser(user);

                User finalUser = user;
                new Handler(Looper.getMainLooper()).post(() -> {
                    showLoading(false);

                    if (success) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Password reset successfully! Please login with your new password.",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Failed to reset password. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Password reset error: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    /**
     * Validate all input fields
     *
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs(String email, String contact, String newPassword, String confirmPassword) {
        boolean isValid = true;

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        // Validate contact
        if (TextUtils.isEmpty(contact)) {
            tilContact.setError("Contact number is required for verification");
            isValid = false;
        } else if (!android.util.Patterns.PHONE.matcher(contact).matches()) {
            tilContact.setError("Please enter a valid phone number");
            isValid = false;
        }

        // Validate new password
        if (TextUtils.isEmpty(newPassword)) {
            tilNewPassword.setError("New password is required");
            isValid = false;
        } else if (newPassword.length() < Constants.MIN_PASSWORD_LENGTH) {
            tilNewPassword.setError("Password must be at least " + Constants.MIN_PASSWORD_LENGTH + " characters");
            isValid = false;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your new password");
            isValid = false;
        } else if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Clear all error messages
     */
    private void clearErrors() {
        tilEmail.setError(null);
        tilContact.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    /**
     * Show or hide loading indicator
     *
     * @param show true to show loading, false to hide
     */
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnResetPassword.setEnabled(!show);
        btnResetPassword.setAlpha(show ? 0.5f : 1.0f);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}