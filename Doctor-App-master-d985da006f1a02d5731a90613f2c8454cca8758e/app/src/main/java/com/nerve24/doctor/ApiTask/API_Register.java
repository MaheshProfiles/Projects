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
import com.nerve24.doctor.Listeners.Listener_OTP_Register_ResendOTP;
import com.nerve24.doctor.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class API_Register {
    private static final String MODULE = "API_Register";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiRegister;
    private Activity mActivity;

    private Listener_OTP_Register_ResendOTP mCallBack;
    JSONObject obj;

    public API_Register(JSONObject obj, Listener_OTP_Register_ResendOTP mCallBack, Activity mActivity) {
        this.obj = obj;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void register() {

        TAG = "register";
        Log.d(MODULE, TAG);

        try {

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                    Str_Url, obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.e("response", "" + response);
                        //  {"success":true,"statusCode":1,"message":"OTP 53573620 has been sent to your registered Mobile/Email"}
                        if (response.has("success")) {
                            String success = response.getString("success");
                            if (success.equals("true")) {
                                mCallBack.onRegisterSuccess(response.getString("message"));
                            } else {
                                mCallBack.onfailure(response.getString("message"));
                            }
                        } else {
                            mCallBack.onfailure("Try again later!");
                        }

                    } catch (Exception e) {
                        mCallBack.onfailure(e.toString());
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();

                    VolleyLog.e("Error: ", error.getMessage());
                    Log.e(MODULE, TAG + " UnknownResponse");

                    if (error instanceof ParseError) {
                        Str_Msg = "Invalid credentials!";
                        mCallBack.onfailure(Str_Msg);
                    } else {
                        if (error instanceof NetworkError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_no_internet);
                        } else if (error instanceof ServerError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                        } else if (error instanceof AuthFailureError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                        } else if (error instanceof ParseError) {
                            //Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                            Str_Msg = "Invalid credentials!";
                        } else if (error instanceof NoConnectionError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_no_internet);
                        } else if (error instanceof TimeoutError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                        }
                        mCallBack.onfailure(Str_Msg);
                    }
                }
            }) {
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
            mCallBack.onfailure(e.toString());
        }

    }



}
