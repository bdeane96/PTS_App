package com.example.brona.picasaconnect;

import android.content.Context;
import android.util.Log;

import com.android.volley.Header;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import com.android.volley.Request;
import com.android.volley.Response;

import static com.example.brona.picasaconnect.MainActivity.context;

/**
 * Created by brona on 19/12/2017.
 */

public class HTTPReq {


    public void httpReq() {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://picasaweb.google.com/data/feed/api/user/107263820117403503338/albumid/6475969304088902209?alt=json&imgmax=1600&thumbsize=160c";


        // Request a string response from the provided URL.
        StringRequest stringreq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("http req", "Response is: " + response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("http req", "That didn't work!");
            }
        });

// Add the request to the RequestQueue.

        queue.add(stringreq);
    }

}
