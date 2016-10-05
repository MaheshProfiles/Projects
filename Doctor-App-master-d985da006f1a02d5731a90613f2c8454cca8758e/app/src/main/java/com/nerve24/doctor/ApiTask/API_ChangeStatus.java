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
import com.nerve24.doctor.Listeners.Listener_ChangeAppointmentStatus;
import com.nerve24.doctor.R;


public class API_ChangeStatus {
    private static final String MODULE = "API_ChangeStatus";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiChangeStatus;
    private Activity mActivity;

    private Listener_ChangeAppointmentStatus mCallBack;
    String obj;

    public API_ChangeStatus(String obj, Listener_ChangeAppointmentStatus mCallBack, Activity mActivity) {
        this.obj = obj;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void changeStatus() {

        TAG = "changeStatus";
        Log.d(MODULE, TAG);
        Log.d(MODULE, Str_Url);
        Log.d(MODULE, obj.toString());

        try {

            StringRequest req = new StringRequest(Request.Method.POST,
                    Str_Url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e("response", "" + response);
                        if (response.equals("1")) {
                            mCallBack.onChangedStatus("Status changed successfully!");
                        } else {
                            mCallBack.onChangedStatus("Status isn't changed!!");
                        }

                    } catch (Exception e) {
                        mCallBack.onChangedStatusFailure(e.toString());
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

                    mCallBack.onChangedStatusFailure(Str_Msg);
                }
            }) {

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return obj.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
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
            mCallBack.onChangedStatusFailure(e.toString());
        }

    }


}
