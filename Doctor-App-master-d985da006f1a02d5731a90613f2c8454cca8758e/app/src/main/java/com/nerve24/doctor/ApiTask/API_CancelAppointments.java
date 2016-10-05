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
import com.nerve24.doctor.Listeners.Listener_CancelAppointments;
import com.nerve24.doctor.R;

import org.json.JSONObject;

/**
 * Created by mahesha on 05-08-16.
 */
public class API_CancelAppointments {

    private static final String MODULE = "API_Cancel_Appointments";
    private static String TAG = "", Str_Msg = "";
    JSONObject obj;
    private String Str_Url = Constants_API.ApiCancelAppoinments;
    private Activity mActivity;
    private Listener_CancelAppointments mCallBack;

    public API_CancelAppointments(JSONObject obj, Listener_CancelAppointments mCallBack, Activity mActivity) {
        this.obj = obj;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }

    public void cancelAppointment() {
        TAG = "cancelAppointment";
        Log.d(MODULE, TAG);
        Log.d(MODULE, Str_Url);
        Log.d(MODULE, obj.toString());
        try {
            StringRequest req = new StringRequest(Request.Method.POST,
                    Str_Url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        if (response.equals("true")) {
                            mCallBack.onCancelAppointmentSuccess(mActivity.getResources().getString(R.string.msg_success_error));
                        } else {
                            mCallBack.onCancelAppointmentError(mActivity.getResources().getString(R.string.msg_failure_error));
                        }
                    } catch (Exception e) {
                        mCallBack.onCancelAppointmentError(e.toString());
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

                    mCallBack.onCancelAppointmentError(Str_Msg);
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
            mCallBack.onCancelAppointmentError(e.toString());
        }

    }

}
