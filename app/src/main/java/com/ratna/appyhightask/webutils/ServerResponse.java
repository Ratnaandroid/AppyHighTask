package com.ratna.appyhightask.webutils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ratna.appyhightask.BaseApplication;
import com.ratna.appyhightask.interfaces.IParseListener;


import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class ServerResponse {
    private int requestCode;
    private Context mContext;
    private RequestQueue requestQueue;

    public void serviceRequest(String url) {
        String tag_json_obj = "json_obj_req";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
            }
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void serviceRequest(Context mContext, final String url, final Map<String, String> params,
                               final IParseListener iParseListener, final int requestCode) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response is", response);
                        iParseListener.SuccessResponse(response, requestCode);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        iParseListener.ErrorResponse(error, requestCode);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("service is", url + params + "");
                return params;
            }
        };
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);

        }
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void serviceRequestGet(Context mContext, final String url, final Map<String, String> params, final IParseListener iParseListener,
                                  final int requestCode) {
        this.requestCode = requestCode;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response is", response);
                        iParseListener.SuccessResponse(response, requestCode);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        iParseListener.ErrorResponse(error, requestCode);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("service is", url + params + "");
                return params;
            }
        };
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);

        }
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void serviceJsonRequest(Context mContext, String url, Map<String, String> params, final IParseListener iParseListener,
                                   final int requestCode) {
        this.requestCode = requestCode;

        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("jsonresponse is", response.toString());
                        iParseListener.SuccessResponse(response.toString(), requestCode);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                iParseListener.ErrorResponse(error, requestCode);
            }
        });
        // Volley.newRequestQueue(mContext).add(jsonRequest);
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);

        }
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonRequest);
    }

    private boolean isFile = false;
    private String file_path = "", key = "";
    private HashMap<String, File> mAttachFileList = new HashMap<String, File>();

    public void setAttachFileList(HashMap<String, File> mAttachFileList) {
        this.isFile = true;
        this.mAttachFileList = mAttachFileList;
    }

    public void serviceRequestJSonObject(Context mContext, final String url, final JSONObject params,
                                         final IParseListener iParseListener, final int requestCode) {
        this.requestCode = requestCode;

        //Utility.showLog("Params is", ""+params);
        Log.e(TAG, "serviceRequestJSonObject: Params...." + params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                iParseListener.SuccessResponse("" + response, requestCode);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                iParseListener.ErrorResponse(error, requestCode);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };


        if (requestQueue == null) {
              requestQueue = Volley.newRequestQueue(mContext);

        }
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }


}
