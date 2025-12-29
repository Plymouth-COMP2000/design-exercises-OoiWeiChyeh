package com.example.mal2017_assessmentmodule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.mal2017_assessmentmodule.api.ApiService;
import com.example.mal2017_assessmentmodule.database.DatabaseHelper;
import com.example.mal2017_assessmentmodule.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

/**
 * LoginActivity - Complete authentication with API integration.
 *
 * Features:
 * - Staff and Guest login (both use email/password)
 * - API authentication with detailed logging
 * - Local database fallback
 * - Input validation
 * - Session management
 *
 * Authentication Flow:
 * 1. Validate email and password
 * 2. Try to authenticate with API first
 * 3. If API succeeds, sync user to local database
 * 4. If API fails or no connection, try local database
 * 5. Create session and navigate to appropriate home screen
 *
 * Test Credentials:
 * From API:
 * - Guest: guest@mail.com / password123
 * - Staff: staff@mail.com / password123
 * - Student: john.doe@example.com / test
 *
 * @author BSCS2509254
 * @version 5.0 (Fixed API Authentication)
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // UI Components
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignup;
    private ProgressBar progressBar;

    // Data & Session
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            // Initialize views
            initializeViews();

            // Initialize services
            sessionManager = new SessionManager(this);
            apiService = new ApiService(this);

            // Check if already logged in
            if (sessionManager.isLoggedIn()) {
                Log.d(TAG, "User already logged in, navigating to home");
                navigateToHome();
                return;
            }

            // Initialize database in background
            new Thread(() -> {
                try {
                    dbHelper = DatabaseHelper.getInstance(LoginActivity.this);
                    Log.d(TAG, "Database initialized successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Database initialization error: " + e.getMessage(), e);
                }
            }).start();

            setupClickListeners();

            // Show test credentials hint
            showTestCredentialsHint();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show test credentials hint
     */
    private void showTestCredentialsHint() {
        Toast.makeText(this,
                "Staff/Guest Login:\n\n" +
                        "Guest: guest@mail.com\n" +
                        "Staff: staff@mail.com\n" +
                        "Password: password123",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Initialize all view components
     */
    private void initializeViews() {
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvSignup = findViewById(R.id.tv_signup);
        progressBar = findViewById(R.id.progress_bar);
    }

    /**
     * Setup click listeners for all interactive elements
     */
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        tvForgotPassword.setOnClickListener(v -> handleForgotPassword());
        tvSignup.setOnClickListener(v -> handleSignup());
    }

    /**
     * Handle login button click
     * Validates input and authenticates user
     */
    private void handleLogin() {
        Log.d(TAG, "=== Login Attempt Started ===");

        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Log.d(TAG, "Email entered: " + email);
        Log.d(TAG, "Password length: " + password.length());

        if (!validateInputs(email, password)) {
            Log.d(TAG, "Validation failed");
            return;
        }

        showLoading(true);

        // Try API authentication first
        authenticateWithApi(email, password);
    }

    /**
     * Authenticate user with API
     * Fetches all users and compares credentials
     */
    private void authenticateWithApi(String email, String password) {
        Log.d(TAG, "=== API Authentication Started ===");
        Log.d(TAG, "Fetching users from API with Student ID: " + Constants.STUDENT_ID);

        apiService.getAllUsers(Constants.STUDENT_ID,
                new Response.Listener<Map<String, List<User>>>() {
                    @Override
                    public void onResponse(Map<String, List<User>> response) {
                        Log.d(TAG, "=== API Response Received ===");
                        Log.d(TAG, "Response map keys: " + response.keySet());

                        List<User> users = response.get("users");

                        if (users == null) {
                            Log.e(TAG, "Users list is NULL in API response");
                            Log.d(TAG, "Falling back to local database");
                            authenticateWithLocalDatabase(email, password);
                            return;
                        }

                        Log.d(TAG, "Number of users fetched from API: " + users.size());

                        // Debug: Print all users from API
                        for (int i = 0; i < users.size(); i++) {
                            User u = users.get(i);
                            Log.d(TAG, String.format("User %d: email=%s, usertype=%s",
                                    i + 1, u.getEmail(), u.getUsertype()));
                        }

                        // Find user with matching email and password
                        User authenticatedUser = null;
                        for (User user : users) {
                            Log.d(TAG, "Comparing with user: " + user.getEmail());

                            // Case-insensitive email comparison
                            boolean emailMatches = user.getEmail().equalsIgnoreCase(email);
                            boolean passwordMatches = user.getPassword().equals(password);

                            Log.d(TAG, "Email matches: " + emailMatches);
                            Log.d(TAG, "Password matches: " + passwordMatches);

                            if (emailMatches && passwordMatches) {
                                authenticatedUser = user;
                                Log.d(TAG, "=== MATCH FOUND ===");
                                Log.d(TAG, "Authenticated user: " + user.getUsername());
                                Log.d(TAG, "User type: " + user.getUsertype());
                                break;
                            }
                        }

                        if (authenticatedUser != null) {
                            Log.d(TAG, "Authentication successful via API");
                            onApiAuthenticationSuccess(authenticatedUser);
                        } else {
                            Log.d(TAG, "No matching user found in API");
                            Log.d(TAG, "Trying local database as fallback");
                            authenticateWithLocalDatabase(email, password);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "=== API Authentication Failed ===");
                        Log.e(TAG, "Error: " + error.toString());
                        if (error.getMessage() != null) {
                            Log.e(TAG, "Error message: " + error.getMessage());
                        }
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Status code: " + error.networkResponse.statusCode);
                        }

                        // Show error to user
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this,
                                    "API connection failed. Trying local database...",
                                    Toast.LENGTH_SHORT).show();
                        });

                        // Fallback to local database
                        authenticateWithLocalDatabase(email, password);
                    }
                });
    }

    /**
     * Handle successful API authentication
     * Sync user to local database and create session
     */
    private void onApiAuthenticationSuccess(User user) {
        Log.d(TAG, "=== Syncing user to local database ===");

        // Sync user to local database in background
        new Thread(() -> {
            try {
                if (dbHelper == null) {
                    dbHelper = DatabaseHelper.getInstance(this);
                }

                // Check if user exists in local database
                User localUser = null;
                List<User> allUsers = dbHelper.getAllUsers();
                for (User u : allUsers) {
                    if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                        localUser = u;
                        break;
                    }
                }

                // If user doesn't exist locally, add them
                if (localUser == null) {
                    // Generate local user ID
                    int newUserId = allUsers.size() + 1;
                    user.setUserId(newUserId);
                    boolean added = dbHelper.addUser(user);
                    Log.d(TAG, "User added to local database: " + added);
                } else {
                    // Use existing local user ID and update data
                    user.setUserId(localUser.getUserId());
                    boolean updated = dbHelper.updateUser(user);
                    Log.d(TAG, "Local user data updated: " + updated);
                }

                // Update UI on main thread
                runOnUiThread(() -> {
                    showLoading(false);
                    onLoginSuccess(user);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error syncing user to database: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    showLoading(false);
                    onLoginSuccess(user);
                });
            }
        }).start();
    }

    /**
     * Authenticate with local database (fallback)
     */
    private void authenticateWithLocalDatabase(String email, String password) {
        Log.d(TAG, "=== Local Database Authentication Started ===");

        new Thread(() -> {
            try {
                if (dbHelper == null) {
                    dbHelper = DatabaseHelper.getInstance(this);
                }

                User localUser = dbHelper.getUserByCredentials(email, password);

                runOnUiThread(() -> {
                    showLoading(false);

                    if (localUser != null) {
                        Log.d(TAG, "=== User authenticated via local database ===");
                        Log.d(TAG, "Username: " + localUser.getUsername());
                        Log.d(TAG, "User type: " + localUser.getUsertype());
                        onLoginSuccess(localUser);
                    } else {
                        Log.d(TAG, "=== Authentication Failed ===");
                        Log.d(TAG, "No matching user in local database");

                        // Show detailed error message
                        Toast.makeText(LoginActivity.this,
                                "Invalid email or password.\n\n" +
                                        "Please check:\n" +
                                        "• Email: guest@mail.com or staff@mail.com\n" +
                                        "• Password: password123\n" +
                                        "• Internet connection for API",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Local authentication error: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this,
                            "Authentication error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    /**
     * Handle successful login
     * Saves session and navigates to appropriate screen
     *
     * @param user Authenticated user object
     */
    private void onLoginSuccess(User user) {
        Log.d(TAG, "=== Login Success ===");
        Log.d(TAG, "User: " + user.getFullName());
        Log.d(TAG, "Type: " + user.getUsertype());

        // Save session
        sessionManager.createLoginSession(user);
        Log.d(TAG, "Session created");

        // Show welcome message
        String userType = user.isStaff() ? "Staff" : "Guest";
        Toast.makeText(this,
                "Welcome " + userType + ", " + user.getFirstname() + "!",
                Toast.LENGTH_SHORT).show();

        // Navigate to appropriate screen
        navigateToHome();
    }

    /**
     * Navigate to home screen based on user type
     */
    private void navigateToHome() {
        try {
            Intent intent;

            if (sessionManager != null && sessionManager.isStaff()) {
                Log.d(TAG, "Navigating to Staff Dashboard");
                intent = new Intent(this, StaffDashboardActivity.class);
            } else {
                Log.d(TAG, "Navigating to Guest Menu");
                intent = new Intent(this, GuestMenuActivity.class);
            }

            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to home: " + e.getMessage(), e);
            Toast.makeText(this, "Navigation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validate email and password inputs
     *
     * @param email User email
     * @param password User password
     * @return true if valid, false otherwise
     */
    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Show or hide loading indicator
     *
     * @param show true to show loading, false to hide
     */
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnLogin.setAlpha(show ? 0.5f : 1.0f);
        etEmail.setEnabled(!show);
        etPassword.setEnabled(!show);
    }

    /**
     * Handle forgot password click
     */
    private void handleForgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    /**
     * Handle signup click
     */
    private void handleSignup() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}