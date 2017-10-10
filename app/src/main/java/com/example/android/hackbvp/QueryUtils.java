package com.example.android.hackbvp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by addie on 10-10-2017.
 */

public class QueryUtils {
    private static void getVolleyResults(Context context, final VolleyCallback callback, String volleyUrl) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

// Request string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError();
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public interface VolleyCallback {
        void onSuccess(String result);

        void onError();
    }


}
