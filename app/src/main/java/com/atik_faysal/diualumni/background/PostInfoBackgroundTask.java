package com.atik_faysal.diualumni.background;

import java.util.Map;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;

public class PostInfoBackgroundTask
{
     private Context context;
     private CheckInternetConnection internetIsOn;
     private RequestQueue requestQueue;
     private OnResponseTask onResponseTask;

     //constructor
     public PostInfoBackgroundTask(Context context,OnResponseTask task)
     {
          this.context = context;
          internetIsOn = new CheckInternetConnection(context);
          requestQueue = Volley.newRequestQueue(context);
          this.onResponseTask = task;
     }

     public void InsertData(final String serverUrl, final Map<String,String> dataMap){

          if(internetIsOn.isOnline())
          {
               StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                         @Override
                         public void onResponse(String ServerResponse) {
                              onResponseTask.onResultSuccess(ServerResponse.trim());
                         }
                    },
                    new Response.ErrorListener() {
                         @Override
                         public void onErrorResponse(VolleyError volleyError) {
                              onResponseTask.onResultSuccess(volleyError.toString());
                         }
                    }) {
                    @Override
                    protected Map<String, String> getParams() {

                         // Creating Map String Params.
                         Map<String, String> params;
                         params = dataMap;

                         return params;
                    }
               };
               requestQueue.add(stringRequest);
          }
     }
}
