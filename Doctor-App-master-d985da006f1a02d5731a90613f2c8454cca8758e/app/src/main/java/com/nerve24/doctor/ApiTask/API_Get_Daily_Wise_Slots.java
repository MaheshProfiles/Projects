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
import com.nerve24.doctor.Listeners.Listener_Get_Daily_Wise_Slots;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.pojo.DailyWiseSlot;
import com.nerve24.doctor.pojo.DailyWiseSlotDay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class API_Get_Daily_Wise_Slots {
    private static final String MODULE = "API_Get_Daily_Wise";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiGetDailyWiseSlots;
    private Activity mActivity;

    private Listener_Get_Daily_Wise_Slots mCallBack;
    private JSONObject obj;
    private Session session;

    public API_Get_Daily_Wise_Slots(JSONObject obj, Listener_Get_Daily_Wise_Slots mCallBack, Activity mActivity) {
        this.obj = obj;
        this.mActivity = mActivity;
        session=new Session(this.mActivity);
        this.mCallBack = mCallBack;
    }


    public void get() {

        TAG = "get";
        Log.d(MODULE, TAG);
        Log.d(MODULE, Str_Url);
        Log.d(MODULE, obj.toString());

        session.saveDailyWiseParams(obj.toString());

        try {

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                    Str_Url, obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.e("response", "" + response);
                        session.saveDailyWise(response.toString());
                        parseData(response);
                    } catch (Exception e) {
                        mCallBack.onGetDailyWiseSlotsError(e.toString());
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

                    mCallBack.onGetDailyWiseSlotsError(Str_Msg);
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
            mCallBack.onGetDailyWiseSlotsError(e.toString());
        }

    }

    private void parseData(JSONObject response) {
        try {
            ArrayList<String> daysList = new ArrayList<>();
            daysList.add("Monday");
            daysList.add("Tuesday");
            daysList.add("Wednesday");
            daysList.add("Thursday");
            daysList.add("Friday");
            daysList.add("Saturday");
            daysList.add("Sunday");

            ArrayList<DailyWiseSlot> dailyWiseSlotList=new ArrayList<>();
            for (int i = 0; i < daysList.size(); i++) {
                JSONArray jsonArray = response.getJSONArray(daysList.get(i));
                ArrayList<DailyWiseSlotDay> dailyWiseSlotDayList=new ArrayList<>();
                if (jsonArray.length() > 0) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        String clinicName = "", clinicID = "", slotsId = "", startTime = "", endTime = "", day = "";
                        if(jsonObject.has("clinicName"))
                            clinicName = jsonObject.getString("clinicName");
                        if(jsonObject.has("clinicID"))
                            clinicID = jsonObject.getString("clinicID");
                        if(jsonObject.has("slotsId"))
                            slotsId = jsonObject.getString("slotsId");
                        if(jsonObject.has("startTime"))
                            startTime = jsonObject.getString("startTime");
                        if(jsonObject.has("endTime"))
                            endTime = jsonObject.getString("endTime");
                        if(jsonObject.has("day"))
                            day = jsonObject.getString("day");

                        DailyWiseSlotDay dailyWiseSlotDay=new DailyWiseSlotDay(clinicName, clinicID, slotsId, startTime, endTime, day);
                        dailyWiseSlotDayList.add(dailyWiseSlotDay);
                    }
                }
                DailyWiseSlot dailyWiseSlot=new DailyWiseSlot(daysList.get(i),dailyWiseSlotDayList);
                dailyWiseSlotList.add(dailyWiseSlot);
            }
            mCallBack.onGetDailyWiseSlots(dailyWiseSlotList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
