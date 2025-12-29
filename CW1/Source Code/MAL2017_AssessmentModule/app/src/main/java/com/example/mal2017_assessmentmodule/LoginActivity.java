package com.example.mal2017_assessmentmodule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mal2017_assessmentmodule.R;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * LoginActivity - Authentication entry point for RestaurantPro application.
 *
 * This activity implements a unified login system following Occam's Razor principle.
 * Both Staff and Guest users login through the same interface, and the system
 * automatically routes them to the appropriate dashboard based on their role.
 *
 * Design Rationale:
 * - Single login point reduces confusion and simplifies the user experience
 * - Role-based routing happens automatically after authentication
 * - Similar to WordPress and other CMS systems where user type determines destination
 *
 * Key Features:
 * - Email and password validation
 * - Loading indicator during authentication
 * - Error handling with user-friendly messages
 * - Responsive layout that adapts to different screen sizes
 *
 * Future Implementation (CW2):
 * - Connect to RESTful API for authentication
 * - Implement JWT token management
 * - Add biometric authentication option
 * - Implement "Remember Me" functionality
 *
 * @author BSCS2509254
 * @version 1.0
 * @since 2025-11-19
 */
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword;
    private TextView tvSignup;
    private ProgressBar progressBar;

    /**
     * Called when the activity is first created.
     * Initializes the UI components and sets up event listeners.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        initializeViews();

        // Set up click listeners
        setupClickListeners();
    }

    /**
     * Initializes all view references.
     * This method finds and stores references to all UI elements used in this activity.
     *
     * Why we do this:
     * - findViewById() is an expensive operation
     * - Storing references improves performance
     * - Makes code more readable and maintainable
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
     * Sets up click listeners for interactive UI elements.
     *
     * Design Pattern: Event-Driven Programming
     * - User interactions trigger specific methods
     * - Keeps event handling logic organized
     * - Easy to modify or extend functionality
     */
    private void setupClickListeners() {
        // Login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        // Forgot password click listener
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });

        // Sign up click listener
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignup();
            }
        });
    }

    /**
     * Handles the login process when user clicks the login button.
     *
     * Process Flow:
     * 1. Validate user input (email and password)
     * 2. Show loading indicator
     * 3. Authenticate with API (simulated for now)
     * 4. Route to appropriate dashboard based on user role
     * 5. Handle any errors gracefully
     *
     * Input Validation:
     * - Email: Must not be empty and should match email pattern
     * - Password: Must not be empty and minimum 6 characters
     *
     * Error Handling:
     * - Shows inline error messages in TextInputLayout
     * - Displays Toast messages for authentication failures
     * - Clears errors when user starts typing again
     */
    private void handleLogin() {
        // Clear any previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        // Get input values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Show loading indicator
        showLoading(true);

        // Simulate authentication (for CW1 demonstration)
        btnLogin.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoading(false);

                // Simple routing logic for demonstration:
                // Email containing "staff" → Staff Dashboard
                // Any other email → Guest Menu
                if (email.toLowerCase().contains("staff")) {
                    // Route to staff dashboard
                    Intent intent = new Intent(LoginActivity.this, StaffDashboardActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this,
                            "Welcome Staff!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Route to guest menu
                    Intent intent = new Intent(LoginActivity.this, GuestMenuActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this,
                            "Welcome Guest!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }, 1500);
    }

    /**
     * Validates user inputs before attempting login.
     *
     * Validation Rules:
     * - Email must not be empty
     * - Email must match standard email pattern
     * - Password must not be empty
     * - Password must be at least 6 characters
     *
     * Why validate on client-side:
     * - Immediate feedback to user
     * - Reduces unnecessary server requests
     * - Improves user experience
     *
     * Note: Server-side validation is still required for security
     *
     * @param email User's email input
     * @param password User's password input
     * @return true if all validations pass, false otherwise
     */
    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Shows or hides the loading indicator.
     *
     * UX Consideration:
     * - Provides visual feedback during authentication
     * - Disables login button to prevent duplicate submissions
     * - Makes app feel responsive even during network delays
     *
     * @param show true to show loading, false to hide
     */
    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setAlpha(0.5f); // Visual indication that button is disabled
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnLogin.setAlpha(1.0f);
        }
    }

    /**
     * Handles forgot password functionality.
     *
     * TODO (CW2): Implement password reset flow
     * - Show dialog to enter email
     * - Send password reset request to API
     * - Show confirmation message
     * - Send password reset email
     */
    private void handleForgotPassword() {
        Toast.makeText(this,
                "Forgot password functionality will be implemented in CW2",
                Toast.LENGTH_SHORT).show();

        // TODO: Open ForgotPasswordActivity or show dialog
    }

    /**
     * Handles sign up functionality.
     *
     * TODO (CW2): Implement user registration
     * - Navigate to registration activity
     * - Collect user information
     * - Create new account via API
     * - Handle guest vs staff registration
     */
    private void handleSignup() {
        Toast.makeText(this,
                "Sign up functionality will be implemented in CW2",
                Toast.LENGTH_SHORT).show();

        // TODO: Open SignupActivity
    }

    /**
     * Called when activity is destroyed.
     * Clean up resources and prevent memory leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending callbacks to prevent memory leaks
        btnLogin.removeCallbacks(null);
    }
}