package com.example.android.hackbvp;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


public final class QueryUtils {
    public static RequestQueue addVolleyHttpRequest(RequestQueue queue, boolean isGetRequest,
                                                    String volleyUrl){
        int requestMethod;
        if (isGetRequest){
            requestMethod = Request.Method.GET;
        }
        else {
            requestMethod = Request.Method.POST;
        }
        StringRequest request = new StringRequest(requestMethod,volleyUrl,
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
        queue.add(request);
        return queue;
    }

}
