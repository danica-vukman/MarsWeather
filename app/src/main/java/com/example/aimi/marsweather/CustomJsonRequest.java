package com.example.aimi.marsweather;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONObject;

/**
 * Created by Aimi on 10/05/2016.
 */
public class CustomJsonRequest extends JsonObjectRequest {
    public CustomJsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    private Priority mPriority;

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    public Priority getmPriority() {
        return mPriority == null ? Priority.NORMAL : mPriority;
    }
}
