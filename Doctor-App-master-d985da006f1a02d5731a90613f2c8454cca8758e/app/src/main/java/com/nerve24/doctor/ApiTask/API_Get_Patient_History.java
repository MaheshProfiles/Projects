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
import com.nerve24.doctor.Listeners.Listener_Get_PatientHistory;
import com.nerve24.doctor.R;
import com.nerve24.doctor.pojo.PatientHistory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class API_Get_Patient_History {
    private static final String MODULE = "API_Get_Patient_History";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiGetPatientHistory;
    private Activity mActivity;

    private Listener_Get_PatientHistory mCallBack;
    private String obj;

    public API_Get_Patient_History(String obj, Listener_Get_PatientHistory mCallBack, Activity mActivity) {
        this.obj = obj;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void getPatients() {

        TAG = "getPatients";
        Log.d(MODULE, TAG);
        Log.d(MODULE, obj);
        Log.d(MODULE, Str_Url);
        try {

            JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,
                    Str_Url,obj, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        Log.e("response", "" + response);
                        if (response.length() > 0) {
                            parseData(response);
                        } else {
                            ArrayList<PatientHistory> patientHistory = new ArrayList<>();
                            mCallBack.onGetPatientHistory(patientHistory);
                        }
                    } catch (Exception e) {
                        mCallBack.onGetPatientHistoryError(e.toString());
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

                    mCallBack.onGetPatientHistoryError(Str_Msg);
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
            mCallBack.onGetPatientHistoryError(e.toString());
        }

    }

    private void parseData(JSONArray response) {
        try {

            ArrayList<PatientHistory> patientHistoryArrayList = new ArrayList<>();

            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                String id = "", patientNerve24Id = "", patientName = "", age = "", sex = "",
                        referedBy = "", encounter = "",
                        episodeType = "", clinicId = "", clinicName = "",
                        appointmentDate="", appointmentTime="";

                id = jsonObject.getString("id");
                patientNerve24Id = jsonObject.getString("patientNerve24Id");
                patientName = jsonObject.getString("patientName");
                age = jsonObject.getString("age");
                sex = jsonObject.getString("sex");
                referedBy = jsonObject.getString("referedBy");
                encounter = jsonObject.getString("encounter");
                JSONObject epiObj = jsonObject.getJSONObject("episode");
                episodeType = epiObj.getString("episodeType");
                JSONObject clinicObj = jsonObject.getJSONObject("clinic");
                clinicId = clinicObj.getString("clinicId");
                clinicName = clinicObj.getString("clinicName");
                sex=sex.equals("null")?"":sex;
                appointmentDate=jsonObject.getString("appointmentDate");
                appointmentTime=jsonObject.getString("appointmentTime");


                PatientHistory patientHistory = new PatientHistory(id, patientNerve24Id, patientName,
                        age, sex, referedBy,
                        encounter, episodeType,
                        clinicId, clinicName,appointmentDate,appointmentTime);
                patientHistoryArrayList.add(patientHistory);
            }

            Collections.sort(patientHistoryArrayList, new Comparator<PatientHistory>() {
                @Override
                public int compare(PatientHistory o1, PatientHistory o2) {
                    return o1.appointmentDate.compareTo(o2.appointmentDate);
                }
            });

            Collections.reverse(patientHistoryArrayList);
            mCallBack.onGetPatientHistory(patientHistoryArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
