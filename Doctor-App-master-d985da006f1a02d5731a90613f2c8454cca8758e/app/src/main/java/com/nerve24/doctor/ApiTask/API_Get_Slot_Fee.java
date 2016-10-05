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
import com.nerve24.doctor.Listeners.Listener_Get_Slot_Fee;
import com.nerve24.doctor.R;
import com.nerve24.doctor.pojo.SlotFee;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class API_Get_Slot_Fee {
    private static final String MODULE = "API_Get_Slot_Fee";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiGetPremiumSlots;
    private Activity mActivity;

    private Listener_Get_Slot_Fee mCallBack;
    JSONObject obj;

    public API_Get_Slot_Fee(JSONObject obj, Listener_Get_Slot_Fee mCallBack, Activity mActivity) {
        this.obj = obj;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void get() {

        TAG = "get";
        Log.d(MODULE, TAG);
        Log.d(MODULE, Str_Url);
        Log.d(MODULE, obj.toString());

        try {

            JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,
                    Str_Url, obj.toString(), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        Log.e("response", "" + response);
                        parseData(response);
                    } catch (Exception e) {
                        mCallBack.onGetSlotFeeError(e.toString());
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

                    mCallBack.onGetSlotFeeError(Str_Msg);
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
            mCallBack.onGetSlotFeeError(e.toString());
        }

    }

    private void parseData(JSONArray response) {
        try {
            ArrayList<SlotFee> slotFeeList = new ArrayList<>();

            if (response.length() > 0) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.getJSONObject(i);
                    String slotId = "", userName = "", startTime = "", endTime = "", duration = "", fee = "", premium = "", clinicId = "",
                            parentId = "", day = "", override = "", slotSetupId = "", checked = "";

                    if (jsonObject.has("slotId"))
                        slotId = jsonObject.getString("slotId");
                    if (jsonObject.has("userName"))
                        userName = jsonObject.getString("userName");
                    if (jsonObject.has("startTime"))
                        startTime = jsonObject.getString("startTime");
                    if (jsonObject.has("endTime"))
                        endTime = jsonObject.getString("endTime");
                    if (jsonObject.has("duration"))
                        duration = jsonObject.getString("duration");
                    if (jsonObject.has("fee")) {
                        fee = jsonObject.getString("fee");
                        BigDecimal b = new BigDecimal(fee);
                        fee = b.toString();
                    }
                    if (jsonObject.has("premium"))
                        premium = jsonObject.getString("premium");
                    if (jsonObject.has("clinicId"))
                        clinicId = jsonObject.getString("clinicId");
                    if (jsonObject.has("parentId"))
                        parentId = jsonObject.getString("parentId");
                    if (jsonObject.has("day"))
                        day = jsonObject.getString("day");
                    if (jsonObject.has("override"))
                        override = jsonObject.getString("override");
                    if (jsonObject.has("slotSetupId"))
                        slotSetupId = jsonObject.getString("slotSetupId");
                    if (jsonObject.has("checked"))
                        checked = jsonObject.getString("checked");

                    SlotFee slotFee = new SlotFee(slotId, userName, startTime, endTime, duration, fee, premium, clinicId, parentId, day, override, slotSetupId, checked);
                    slotFeeList.add(slotFee);
                }
                mCallBack.onGetSlotFee(slotFeeList, response);
            } else {
                mCallBack.onGetSlotFee(slotFeeList, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
