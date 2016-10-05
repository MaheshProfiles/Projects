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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nerve24.doctor.AppConfig.Constants_API;
import com.nerve24.doctor.Listeners.Listener_Get_Profile_Picture;
import com.nerve24.doctor.R;

import java.util.HashMap;
import java.util.Map;


public class API_Get_Profile_Picture {
    private static final String MODULE = "API_Get_Profile_Picture";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiGet_ProfilePicture;
    private Activity mActivity;

    private Listener_Get_Profile_Picture mCallBack;
    private String nerve24ID;

    public API_Get_Profile_Picture(String nerve24ID, Listener_Get_Profile_Picture mCallBack, Activity mActivity) {
        this.nerve24ID = nerve24ID;
        Str_Url = Str_Url + this.nerve24ID;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void getPicture() {

        TAG = "getPicture";
        Log.d(MODULE, TAG);
        Log.d(MODULE, Str_Url);
        try {

            StringRequest req = new StringRequest(Request.Method.GET,
                    Str_Url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e("response", "" + response);
                        if (response.length() > 0) {
                            mCallBack.onGetPicture(response);
                        } else {
                            mCallBack.onGetPicture("");
                        }
                    } catch (Exception e) {
                        mCallBack.onGetPictureError(e.toString());
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();

                    VolleyLog.e("Error: ", error.getMessage());
                    Log.e(MODULE, TAG + " UnknownResponse");


                    if (error instanceof NetworkError) {
                        Str_Msg = mActivity.getResources().getString(R.string.msg_no_internet);
                    } else if (error instanceof ServerError) {
                        Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                    } else if (error instanceof AuthFailureError) {
                        Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                    } else if (error instanceof ParseError) {
                        Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                    } else if (error instanceof NoConnectionError) {
                        Str_Msg = mActivity.getResources().getString(R.string.msg_no_internet);
                    } else if (error instanceof TimeoutError) {
                        Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                    }

                    mCallBack.onGetPictureError(Str_Msg);
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
            mCallBack.onGetPictureError(e.toString());
        }

    }


}
