package com.example.android.hackbvp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by addie on 10-10-2017.
 */

public final class QueryUtils {
    public static void volleyHttpRequest(Context context, String volleyUrl) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

// Request string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(QueryUtils.class.getSimpleName(),response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(QueryUtils.class.getSimpleName(),error.toString());
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
