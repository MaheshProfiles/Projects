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
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.pojo.Nerver24Member;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class API_Get_Doctor {
    private static final String MODULE = "API_Get_Doctor";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiGetDoctors;
    private Activity mActivity;


    public API_Get_Doctor(Activity mActivity) {
        this.mActivity = mActivity;
    }


    public void getDoctors() {

        TAG = "getDoctors";
        Log.d(MODULE, TAG);
        Log.d(MODULE, Str_Url);
        try {

            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET,
                    Str_Url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        Log.e("response", "" + response);
                        parseData(response);

                    } catch (Exception e) {
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
        }

    }

    private void parseData(JSONArray response) {
        try {
            ArrayList<Nerver24Member> doctorList = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                String nerve24Id = "", username = "", email = "", primaryphone = "", enabled = "", firstName = "", lastName = "",
                        salutation = "", middleName = "", dob = "", gender = "", userType = "", fullName = "", organizationId = "",
                        roleId = "";

                JSONObject jsonObject = response.getJSONObject(i);

                if (!jsonObject.isNull("nerve24Id"))
                    nerve24Id = jsonObject.getString("nerve24Id");
                if (!jsonObject.isNull("username"))
                    username = jsonObject.getString("username");
                if (!jsonObject.isNull("email"))
                    email = jsonObject.getString("email");
                if (!jsonObject.isNull("primaryphone"))
                    primaryphone = jsonObject.getString("primaryphone");
                if (!jsonObject.isNull("enabled"))
                    enabled = jsonObject.getString("enabled");
                if (!jsonObject.isNull("firstName"))
                    firstName = jsonObject.getString("firstName");
                if (!jsonObject.isNull("lastName"))
                    lastName = jsonObject.getString("lastName");
                if (!jsonObject.isNull("salutation"))
                    salutation = jsonObject.getString("salutation");
                if (!jsonObject.isNull("middleName"))
                    middleName = jsonObject.getString("middleName");
                if (!jsonObject.isNull("dob"))
                    dob = jsonObject.getString("dob");
                if (!jsonObject.isNull("gender"))
                    gender = jsonObject.getString("gender");
                if (!jsonObject.isNull("userType"))
                    userType = jsonObject.getString("userType");
                if (!jsonObject.isNull("fullName"))
                    fullName = jsonObject.getString("fullName");
                if (!jsonObject.isNull("organizationId"))
                    organizationId = jsonObject.getString("organizationId");
                if (!jsonObject.isNull("roleId"))
                    roleId = jsonObject.getString("roleId");

                Nerver24Member nerver24Member = new Nerver24Member(nerve24Id, username, email, primaryphone, enabled, firstName,
                        lastName, salutation, middleName, dob, gender, userType, fullName, organizationId, roleId);
                doctorList.add(nerver24Member);
            }

            new Session(mActivity).saveDoctorList(doctorList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
