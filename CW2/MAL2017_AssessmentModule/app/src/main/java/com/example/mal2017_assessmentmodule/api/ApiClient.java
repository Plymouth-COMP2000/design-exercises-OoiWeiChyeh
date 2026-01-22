package com.example.mal2017_assessmentmodule.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * ApiClient - Singleton Volley client for API communication.
 *
 * Features:
 * - Request queue management
 * - Singleton pattern for efficiency
 * - Automatic request cancellation
 * - Connection timeout handling
 * - Enhanced logging for debugging
 *
 * IMPORTANT: Update BASE_URL if your API is on different IP/port
 *
 * @author BSCS2509254
 * @version 3.0 (Enhanced Logging)
 */
public class ApiClient {

    private static final String TAG = "ApiClient";

    // IMPORTANT: Update this IP address to match your API server
    // Common options:
    // - "http://10.240.72.69/comp2000/coursework/" (your network IP)
    // - "http://10.0.2.2/comp2000/coursework/" (localhost via emulator)

    private static final String BASE_URL = "http://10.240.72.69/comp2000/coursework/";

    private static ApiClient instance;
    private RequestQueue requestQueue;
    private Context context;

    /**
     * Private constructor for Singleton pattern
     */
    private ApiClient(Context context) {
        this.context = context.getApplicationContext();
        Log.d(TAG, "ApiClient initialized with BASE_URL: " + BASE_URL);
    }

    /**
     * Get singleton instance of ApiClient
     */
    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
            Log.d(TAG, "Created new ApiClient instance");
        }
        return instance;
    }

    /**
     * Get request queue (creates if doesn't exist)
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            if (context == null) {
                throw new IllegalStateException("Context is null. ApiClient must be initialized with a valid context.");
            }
            requestQueue = Volley.newRequestQueue(context);
            Log.d(TAG, "Created new RequestQueue");
        }
        return requestQueue;
    }

    /**
     * Add request to queue with logging
     */
    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        Log.d(TAG, "Adding request to queue: " + request.getUrl());
        getRequestQueue().add(request);
    }

    /**
     * Cancel all requests with specific tag
     */
    public void cancelRequests(Object tag) {
        if (requestQueue != null) {
            Log.d(TAG, "Cancelling requests with tag: " + tag);
            requestQueue.cancelAll(tag);
        }
    }

    /**
     * Get base URL
     */
    public static String getBaseUrl() {
        Log.d(TAG, "Returning BASE_URL: " + BASE_URL);
        return BASE_URL;
    }

    /**
     * Test if API is reachable
     * Call this to verify API connection
     */
    public static void logApiInfo() {
        Log.d(TAG, "========================================");
        Log.d(TAG, "API Configuration:");
        Log.d(TAG, "BASE_URL: " + BASE_URL);
        Log.d(TAG, "========================================");
    }
}