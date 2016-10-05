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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nerve24.doctor.AppConfig.Constants_API;
import com.nerve24.doctor.Listeners.Listener_Get_Slots_By_Clinic;
import com.nerve24.doctor.R;
import com.nerve24.doctor.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class API_Get_Slots_By_Clinic {
    private static final String MODULE = "API_Get_Slots_By_Clinic";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiSlotsFromClinics;
    private Activity mActivity;

    private Listener_Get_Slots_By_Clinic mCallBack;
    private String obj;

    public API_Get_Slots_By_Clinic(String obj, Listener_Get_Slots_By_Clinic mCallBack, Activity mActivity) {
        this.obj = obj;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void getSlots() {

        TAG = "getSlots";
        Log.d(MODULE,obj);
        Log.d(MODULE, TAG);
        Log.d(MODULE, Str_Url);
        try {

            JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,
                    Str_Url, obj, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        Log.e("response", "" + response);
                        Utils.LogMine(MODULE,response);
                        if (response.length() > 0) {
                            parseData(response);
                        } else {
                            Map<String,String> map=new HashMap<>();
                            ArrayList<String> timeList = new ArrayList<>();
                            mCallBack.onGetSlots(timeList,map);
                        }
                    } catch (Exception e) {
                        mCallBack.onGetSlotsError(e.toString());
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

                    mCallBack.onGetSlotsError(Str_Msg);
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
            mCallBack.onGetSlotsError(e.toString());
        }

    }

    private void parseData(JSONArray response) {
        try {

            Map<String,String> map=new HashMap<>();
            ArrayList<String> timeList = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                String slotId = "", startTime = "";
                slotId = jsonObject.getString("slotId");
                startTime = jsonObject.getString("startTime");
                startTime=""+Utils.convertTime(startTime,"HH:mm:ss","hh:mm a");
                timeList.add(startTime);
                map.put(startTime,slotId);
            }

            mCallBack.onGetSlots(timeList,map);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
