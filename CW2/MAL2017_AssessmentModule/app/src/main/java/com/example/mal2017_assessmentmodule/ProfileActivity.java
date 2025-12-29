package com.example.mal2017_assessmentmodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mal2017_assessmentmodule.database.DatabaseHelper;
import com.example.mal2017_assessmentmodule.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * ProfileActivity - FIXED VERSION
 * Complete profile management with working functionality
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // UI Components
    private TextView tvUserType, tvUserInitials;
    private TextInputLayout tilFirstname, tilLastname, tilEmail, tilContact, tilUsername;
    private TextInputEditText etFirstname, etLastname, etEmail, etContact, etUsername;
    private Button btnEditProfile, btnSaveProfile, btnCancelEdit;
    private Button btnChangePassword, btnLogout, btnDeleteAccount;
    private ProgressBar progressBar;

    // Data
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private User currentUser;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        try {
            dbHelper = DatabaseHelper.getInstance(this);
            sessionManager = new SessionManager(this);
            currentUser = sessionManager.getLoggedInUser();

            if (currentUser == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            setupToolbar();
            initializeViews();
            loadUserData();
            setupClickListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }
    }

    private void initializeViews() {
        tvUserType = findViewById(R.id.tv_user_type);
        tvUserInitials = findViewById(R.id.tv_user_initials);

        tilFirstname = findViewById(R.id.til_firstname);
        tilLastname = findViewById(R.id.til_lastname);
        tilEmail = findViewById(R.id.til_email);
        tilContact = findViewById(R.id.til_contact);
        tilUsername = findViewById(R.id.til_username);

        etFirstname = findViewById(R.id.et_firstname);
        etLastname = findViewById(R.id.et_lastname);
        etEmail = findViewById(R.id.et_email);
        etContact = findViewById(R.id.et_contact);
        etUsername = findViewById(R.id.et_username);

        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        btnCancelEdit = findViewById(R.id.btn_cancel_edit);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnLogout = findViewById(R.id.btn_logout);
        btnDeleteAccount = findViewById(R.id.btn_delete_account);

        progressBar = findViewById(R.id.progress_bar);
    }

    private void loadUserData() {
        // Set user initials
        String initials = currentUser.getFirstname().substring(0, 1).toUpperCase() +
                currentUser.getLastname().substring(0, 1).toUpperCase();
        tvUserInitials.setText(initials);

        // Set user type
        String userType = currentUser.getUsertype().substring(0, 1).toUpperCase() +
                currentUser.getUsertype().substring(1).toLowerCase();
        tvUserType.setText(userType + " Account");

        // Fill in form fields
        etFirstname.setText(currentUser.getFirstname());
        etLastname.setText(currentUser.getLastname());
        etEmail.setText(currentUser.getEmail());
        etContact.setText(currentUser.getContact());
        etUsername.setText(currentUser.getUsername());

        // Set fields to non-editable by default
        setFieldsEditable(false);
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> enableEditMode());
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnCancelEdit.setOnClickListener(v -> cancelEdit());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountConfirmation());
    }

    private void enableEditMode() {
        isEditMode = true;
        setFieldsEditable(true);
        btnEditProfile.setVisibility(View.GONE);
        btnSaveProfile.setVisibility(View.VISIBLE);
        btnCancelEdit.setVisibility(View.VISIBLE);
        btnChangePassword.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);
        btnDeleteAccount.setVisibility(View.GONE);
    }

    private void setFieldsEditable(boolean editable) {
        etFirstname.setEnabled(editable);
        etLastname.setEnabled(editable);
        etContact.setEnabled(editable);
        etUsername.setEnabled(editable);
        etEmail.setEnabled(false); // Email is never editable
    }

    private void saveProfile() {
        // Clear errors
        tilFirstname.setError(null);
        tilLastname.setError(null);
        tilContact.setError(null);
        tilUsername.setError(null);

        // Get values
        String firstname = etFirstname.getText().toString().trim();
        String lastname = etLastname.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String username = etUsername.getText().toString().trim();

        // Validate
        if (!validateProfileInputs(firstname, lastname, contact, username)) {
            return;
        }

        showLoading(true);

        new Thread(() -> {
            try {
                currentUser.setFirstname(firstname);
                currentUser.setLastname(lastname);
                currentUser.setContact(contact);
                currentUser.setUsername(username);

                boolean success = dbHelper.updateUser(currentUser);

                runOnUiThread(() -> {
                    showLoading(false);

                    if (success) {
                        sessionManager.createLoginSession(currentUser);

                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                        isEditMode = false;
                        setFieldsEditable(false);
                        btnEditProfile.setVisibility(View.VISIBLE);
                        btnSaveProfile.setVisibility(View.GONE);
                        btnCancelEdit.setVisibility(View.GONE);
                        btnChangePassword.setVisibility(View.VISIBLE);
                        btnLogout.setVisibility(View.VISIBLE);
                        btnDeleteAccount.setVisibility(View.VISIBLE);

                        loadUserData();
                    } else {
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error updating profile: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void cancelEdit() {
        isEditMode = false;
        setFieldsEditable(false);
        btnEditProfile.setVisibility(View.VISIBLE);
        btnSaveProfile.setVisibility(View.GONE);
        btnCancelEdit.setVisibility(View.GONE);
        btnChangePassword.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.VISIBLE);
        btnDeleteAccount.setVisibility(View.VISIBLE);
        loadUserData();
    }

    private boolean validateProfileInputs(String firstname, String lastname, String contact, String username) {
        boolean isValid = true;

        if (TextUtils.isEmpty(firstname)) {
            tilFirstname.setError("First name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(lastname)) {
            tilLastname.setError("Last name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(contact)) {
            tilContact.setError("Contact is required");
            isValid = false;
        } else if (!android.util.Patterns.PHONE.matcher(contact).matches()) {
            tilContact.setError("Invalid phone number");
            isValid = false;
        }

        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username is required");
            isValid = false;
        } else if (username.length() < 3) {
            tilUsername.setError("Username must be at least 3 characters");
            isValid = false;
        }

        return isValid;
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);

        TextInputLayout tilCurrentPassword = dialogView.findViewById(R.id.til_current_password);
        TextInputLayout tilNewPassword = dialogView.findViewById(R.id.til_new_password);
        TextInputLayout tilConfirmNewPassword = dialogView.findViewById(R.id.til_confirm_new_password);

        TextInputEditText etCurrentPassword = dialogView.findViewById(R.id.et_current_password);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        TextInputEditText etConfirmNewPassword = dialogView.findViewById(R.id.et_confirm_new_password);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String currentPassword = etCurrentPassword.getText().toString().trim();
                    String newPassword = etNewPassword.getText().toString().trim();
                    String confirmPassword = etConfirmNewPassword.getText().toString().trim();

                    if (!currentPassword.equals(currentUser.getPassword())) {
                        Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (newPassword.length() < 6) {
                        Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    changePassword(newPassword);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void changePassword(String newPassword) {
        showLoading(true);

        new Thread(() -> {
            try {
                currentUser.setPassword(newPassword);
                boolean success = dbHelper.updateUser(currentUser);

                runOnUiThread(() -> {
                    showLoading(false);

                    if (success) {
                        sessionManager.createLoginSession(currentUser);
                        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error changing password: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        sessionManager.logoutUser();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showDeleteAccountConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        showLoading(true);

        new Thread(() -> {
            try {
                boolean success = dbHelper.deleteUser(currentUser.getUserId());

                runOnUiThread(() -> {
                    showLoading(false);

                    if (success) {
                        sessionManager.logoutUser();
                        Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error deleting account: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            cancelEdit();
        } else {
            super.onBackPressed();
        }
    }
}