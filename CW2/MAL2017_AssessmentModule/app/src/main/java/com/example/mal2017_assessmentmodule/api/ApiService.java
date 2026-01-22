package com.example.mal2017_assessmentmodule.api;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.mal2017_assessmentmodule.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ApiService - Volley-based API service for API communication.
 *
 * Base URL: http://10.240.72.69/comp2000/coursework/
 *
 * Authentication: None required (as per API docs)
 *
 * Response Format: JSON
 *
 * @author BSCS2509254
 * @version 2.0 (Volley)
 */
public class ApiService {

    private static final String TAG = "ApiService";
    private Context context;
    private RequestQueue requestQueue;
    private Gson gson;

    public ApiService(Context context) {
        this.context = context;
        this.requestQueue = ApiClient.getInstance(context).getRequestQueue();
        this.gson = new Gson();
    }

    /**
     * Create student database
     * POST /create_student/{student_id}
     */
    public void createStudentDatabase(String studentId, 
                                      Response.Listener<Map<String, String>> listener,
                                      Response.ErrorListener errorListener) {
        String url = ApiClient.getBaseUrl() + "create_student/" + studentId;
        
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Map<String, String> result = new HashMap<>();
                        result.put("message", response);
                        listener.onResponse(result);
                    } catch (Exception e) {
                        errorListener.onErrorResponse(new VolleyError("Failed to parse response", e));
                    }
                },
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    /**
     * Create new user
     * POST /create_user/{student_id}
     */
    public void createUser(String studentId, User user,
                           Response.Listener<Map<String, String>> listener,
                           Response.ErrorListener errorListener) {
        String url = ApiClient.getBaseUrl() + "create_user/" + studentId;
        
        try {
            JSONObject jsonBody = new JSONObject(gson.toJson(user));
            
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        try {
                            Map<String, String> result = new HashMap<>();
                            if (response.has("message")) {
                                result.put("message", response.getString("message"));
                            }
                            listener.onResponse(result);
                        } catch (JSONException e) {
                            errorListener.onErrorResponse(new VolleyError("Failed to parse response", e));
                        }
                    },
                    errorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            errorListener.onErrorResponse(new VolleyError("Failed to create JSON body", e));
        }
    }

    /**
     * Get all users
     * GET /read_all_users/{student_id}
     */
    public void getAllUsers(String studentId,
                           Response.Listener<Map<String, List<User>>> listener,
                           Response.ErrorListener errorListener) {
        String url = ApiClient.getBaseUrl() + "read_all_users/" + studentId;
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Type type = new TypeToken<Map<String, List<User>>>(){}.getType();
                        Map<String, List<User>> result = gson.fromJson(response.toString(), type);
                        listener.onResponse(result);
                    } catch (Exception e) {
                        errorListener.onErrorResponse(new VolleyError("Failed to parse response", e));
                    }
                },
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    /**
     * Get specific user
     * GET /read_user/{student_id}/{user_id}
     */
    public void getUser(String studentId, int userId,
                       Response.Listener<Map<String, User>> listener,
                       Response.ErrorListener errorListener) {
        String url = ApiClient.getBaseUrl() + "read_user/" + studentId + "/" + userId;
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Type type = new TypeToken<Map<String, User>>(){}.getType();
                        Map<String, User> result = gson.fromJson(response.toString(), type);
                        listener.onResponse(result);
                    } catch (Exception e) {
                        errorListener.onErrorResponse(new VolleyError("Failed to parse response", e));
                    }
                },
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    /**
     * Update user
     * PUT /update_user/{student_id}/{user_id}
     */
    public void updateUser(String studentId, int userId, User user,
                          Response.Listener<Map<String, String>> listener,
                          Response.ErrorListener errorListener) {
        String url = ApiClient.getBaseUrl() + "update_user/" + studentId + "/" + userId;
        
        try {
            JSONObject jsonBody = new JSONObject(gson.toJson(user));
            
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                    response -> {
                        try {
                            Map<String, String> result = new HashMap<>();
                            if (response.has("message")) {
                                result.put("message", response.getString("message"));
                            }
                            listener.onResponse(result);
                        } catch (JSONException e) {
                            errorListener.onErrorResponse(new VolleyError("Failed to parse response", e));
                        }
                    },
                    errorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            errorListener.onErrorResponse(new VolleyError("Failed to create JSON body", e));
        }
    }

    /**
     * Delete user
     * DELETE /delete_user/{student_id}/{user_id}
     */
    public void deleteUser(String studentId, int userId,
                          Response.Listener<Map<String, String>> listener,
                          Response.ErrorListener errorListener) {
        String url = ApiClient.getBaseUrl() + "delete_user/" + studentId + "/" + userId;
        
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    try {
                        Map<String, String> result = new HashMap<>();
                        result.put("message", response);
                        listener.onResponse(result);
                    } catch (Exception e) {
                        errorListener.onErrorResponse(new VolleyError("Failed to parse response", e));
                    }
                },
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }
}
