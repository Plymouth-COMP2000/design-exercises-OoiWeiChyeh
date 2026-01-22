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

/**
 * RegisterActivity - Guest user registration.
 *
 * Features:
 * - New user registration
 * - Input validation
 * - Email uniqueness check
 * - Password confirmation
 * - Auto-generate user ID
 * - Save to local database
 *
 * @author BSCS2509254
 * @version 2.0
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    // UI Components
    private TextInputLayout tilFirstname, tilLastname, tilEmail, tilContact;
    private TextInputLayout tilUsername, tilPassword, tilConfirmPassword;
    private TextInputEditText etFirstname, etLastname, etEmail, etContact;
    private TextInputEditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ProgressBar progressBar;

    // Database
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
            getSupportActionBar().setTitle("Create Account");
        }
    }

    /**
     * Initialize all view components
     */
    private void initializeViews() {
        tilFirstname = findViewById(R.id.til_firstname);
        tilLastname = findViewById(R.id.til_lastname);
        tilEmail = findViewById(R.id.til_email);
        tilContact = findViewById(R.id.til_contact);
        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        etFirstname = findViewById(R.id.et_firstname);
        etLastname = findViewById(R.id.et_lastname);
        etEmail = findViewById(R.id.et_email);
        etContact = findViewById(R.id.et_contact);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());
    }

    /**
     * Handle register button click
     * Validates input and creates new user account
     */
    private void handleRegister() {
        // Clear previous errors
        clearErrors();

        // Get input values
        String firstname = etFirstname.getText().toString().trim();
        String lastname = etLastname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(firstname, lastname, email, contact, username, password, confirmPassword)) {
            return;
        }

        showLoading(true);

        // Register user in background thread
        new Thread(() -> {
            try {
                // Check if email already exists
                User existingUser = dbHelper.getUserByCredentials(email, "");
                if (existingUser != null) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        tilEmail.setError("Email already registered");
                    });
                    return;
                }

                // Generate user ID (auto-increment based on existing users)
                int newUserId = dbHelper.getAllUsers().size() + 1;

                // Create new user object
                User newUser = new User();
                newUser.setUserId(newUserId);
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setFirstname(firstname);
                newUser.setLastname(lastname);
                newUser.setEmail(email);
                newUser.setContact(contact);
                newUser.setUsertype(Constants.USER_TYPE_GUEST);

                // Save to database
                boolean success = dbHelper.addUser(newUser);

                // Update UI on main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    showLoading(false);

                    if (success) {
                        Toast.makeText(RegisterActivity.this,
                                "Account created successfully! Please login.",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Registration failed. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Registration error: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(RegisterActivity.this,
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
    private boolean validateInputs(String firstname, String lastname, String email,
                                   String contact, String username, String password,
                                   String confirmPassword) {
        boolean isValid = true;

        // Validate firstname
        if (TextUtils.isEmpty(firstname)) {
            tilFirstname.setError("First name is required");
            isValid = false;
        }

        // Validate lastname
        if (TextUtils.isEmpty(lastname)) {
            tilLastname.setError("Last name is required");
            isValid = false;
        }

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
            tilContact.setError("Contact number is required");
            isValid = false;
        } else if (!android.util.Patterns.PHONE.matcher(contact).matches()) {
            tilContact.setError("Please enter a valid phone number");
            isValid = false;
        }

        // Validate username
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username is required");
            isValid = false;
        } else if (username.length() < 3) {
            tilUsername.setError("Username must be at least 3 characters");
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            tilPassword.setError("Password must be at least " + Constants.MIN_PASSWORD_LENGTH + " characters");
            isValid = false;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Clear all error messages
     */
    private void clearErrors() {
        tilFirstname.setError(null);
        tilLastname.setError(null);
        tilEmail.setError(null);
        tilContact.setError(null);
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    /**
     * Show or hide loading indicator
     *
     * @param show true to show loading, false to hide
     */
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
        btnRegister.setAlpha(show ? 0.5f : 1.0f);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}