package com.nerve24.doctor.ApiTask;


import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nerve24.doctor.AppConfig.Constants_API;
import com.nerve24.doctor.Listeners.Listener_Edit_Price_Clinic;
import com.nerve24.doctor.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class API_Edit_Price_Clinic {
    private static final String MODULE = "API_Edit_Price_Clinic";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiEditPriceClinic;
    private Activity mActivity;

    private Listener_Edit_Price_Clinic mCallBack;
    private JSONObject obj;

    public API_Edit_Price_Clinic(JSONObject obj, Listener_Edit_Price_Clinic mCallBack, Activity mActivity) {
        this.obj = obj;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void edit() {

        TAG = "edit";
        Log.d(MODULE, TAG);
        Log.d(MODULE, Str_Url);
        Log.d(MODULE, obj.toString());
        try {

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                    Str_Url, obj.toString(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    if (error instanceof ParseError) {
                        mCallBack.onPriceEdited("Price successfully updated!");
                    } else {

                        error.printStackTrace();

                        VolleyLog.e("Error: ", error.getMessage());
                        Log.e(MODULE, TAG + " UnknownResponse");

                        if (error instanceof NetworkError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_no_internet);
                        } else if (error instanceof ServerError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                        } else if (error instanceof AuthFailureError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                        } else if (error instanceof NoConnectionError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_no_internet);
                        } else if (error instanceof TimeoutError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                        }

                        mCallBack.onPriceEditedError(Str_Msg);
                    }
                }

            }) {
               /* @Override
                protected Map<String, String> getParams() {

                    return obj;
                }*/

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

            };


            int socketTimeout = 60000;// 30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
            Volley.newRequestQueue(mActivity).add(req);

        } catch (Exception e) {
            Log.e(MODULE, TAG + " Exception Occurs - " + e);
            mCallBack.onPriceEditedError(e.toString());
        }

    }


}
