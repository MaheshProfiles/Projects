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
import com.nerve24.doctor.Listeners.Listener_Get_Slots_with_Appointments;
import com.nerve24.doctor.R;
import com.nerve24.doctor.pojo.Appointment;
import com.nerve24.doctor.pojo.SlotWithAppointment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class API_Get_Slots_with_Appointments_weekly {
    private static final String MODULE = "API_Get_Slots_with_App";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiGetSlots_with_Appointments;
    private Activity mActivity;

    private Listener_Get_Slots_with_Appointments mCallBack;
    private String obj;
    private HashMap<String, ArrayList<SlotWithAppointment>> slotWithAppointmentsMap = new HashMap<>();


    public API_Get_Slots_with_Appointments_weekly(String obj, Listener_Get_Slots_with_Appointments mCallBack, Activity mActivity) {
        this.obj = obj;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void getSlots() {

        TAG = "getSlots";
        Log.d(MODULE, TAG);
        Log.d(MODULE, Str_Url);
        try {

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                    Str_Url, obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.e("response", "" + response);
                        // Utils.LogMine(MODULE,response);

                        if (response.has("Evening") || response.has("Morning") || response.has("Afternoon")) {
                            parseData(response);
                        } else {
                            mCallBack.onGetSlots(slotWithAppointmentsMap);
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

    private void parseData(JSONObject response) {
        try {
            ArrayList<SlotWithAppointment> slots = new ArrayList<>();
            JSONArray eveningArray = new JSONArray();
            JSONArray morningArray = new JSONArray();
            JSONArray noonArray = new JSONArray();

            if (response.has("Evening")) eveningArray = response.getJSONArray("Evening");
            if (response.has("Morning")) morningArray = response.getJSONArray("Morning");
            if (response.has("Afternoon")) noonArray = response.getJSONArray("Afternoon");
            for (int i = 0; i < 3; i++) {
                JSONArray jsonArray = new JSONArray();

                if (i == 0) {
                    jsonArray = eveningArray;
                } else if (i == 1) {
                    jsonArray = morningArray;
                } else if (i == 2) {
                    jsonArray = noonArray;
                }

                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(k);
                    String startTime = "", endTime = "", premium = "", checked = "", slotId = "", parentId = "",clinicIdSlot="",clinicName="";
                    startTime = jsonObject.getString("startTime");
                    endTime = jsonObject.getString("endTime");
                    premium = jsonObject.getString("premium");
                    checked = jsonObject.getString("checked");
                    slotId = jsonObject.getString("slotId");
                    parentId = jsonObject.getString("parentId");
                    JSONObject objClinic=jsonObject.getJSONObject("clinic");
                    clinicIdSlot= objClinic.getString("clinicId");
                    clinicName= objClinic.getString("clinicName");

                    JSONArray appointmentsList = jsonObject.getJSONArray("appointmentsList");

                    ArrayList<Appointment> appointmentArrayList = new ArrayList<>();
                    for (int z = 0; z < appointmentsList.length(); z++) {
                        String username = "", email = "", primaryphone = "", enabled = "", salutation = "", appointmentStatus = "", firstName = "", middleName = "",
                                lastName = "", dob = "", gender = "", nerve24Id = "", appointmentFor = "", appointmentType = "",
                                appointmentDate = "", appointmentTime = "",
                                clinicDisplayName = "", clinicId = "", episode = "", encounter = "", id = "",
                                refer = "",salutationRefer="";

                        JSONObject jsonObject1 = appointmentsList.getJSONObject(z);

                        JSONObject clinicObj = jsonObject1.getJSONObject("clinic");
                        clinicId = clinicObj.getString("clinicId");
                        clinicDisplayName = clinicObj.getString("clinicName");

                        encounter = jsonObject1.getString("encounter");
                        id = jsonObject1.getString("id");
                        episode = jsonObject1.getJSONObject("episode").getString("episodeType");
                        appointmentStatus = jsonObject1.getString("appointmentStatus");
                        appointmentDate = jsonObject1.getString("appointmentDate");
                        appointmentTime = jsonObject1.getString("appointmentTime");
                        appointmentFor = jsonObject1.getString("appointmentFor");
                        appointmentType = jsonObject1.getString("appointmentType");

                        if (jsonObject1.has("referredByDoctor")) {
                            if (jsonObject1.get("referredByDoctor") instanceof JSONObject) {
                                if (jsonObject1.get("referredByDoctor") != null) {
                                    JSONObject objRefer = jsonObject1.getJSONObject("referredByDoctor");
                                    refer = objRefer.getString("firstName");
                                    if(objRefer.get("salutation") !=null)
                                    salutationRefer = objRefer.getString("salutation");
                                }
                            }
                        }
                        if (jsonObject1.isNull("patientUser")) {

                        } else {
                            JSONObject objPatient = jsonObject1.getJSONObject("patientUser");
                            username = objPatient.getString("username");
                            email = objPatient.getString("email");
                            primaryphone = objPatient.getString("primaryphone");
                            enabled = objPatient.getString("enabled");
                            salutation = objPatient.getString("salutation");
                            firstName = objPatient.getString("firstName");
                            middleName = objPatient.getString("middleName");
                            lastName = objPatient.getString("lastName");
                            dob = objPatient.getString("dob");
                            gender = objPatient.getString("gender");
                            nerve24Id = objPatient.getString("nerve24Id");
                        }
                        Appointment appointment = new Appointment(username, email, primaryphone, enabled, salutation, appointmentStatus, firstName, middleName, lastName, dob, gender, nerve24Id,
                                appointmentFor, appointmentType, clinicDisplayName, clinicId, appointmentDate, appointmentTime,
                                episode, encounter, id, refer,premium,salutationRefer);
                        appointmentArrayList.add(appointment);
                    }
                    SlotWithAppointment slotWithAppointment = new SlotWithAppointment(startTime, endTime, premium, checked, slotId, parentId, appointmentArrayList,clinicIdSlot,clinicName);
                    slots.add(slotWithAppointment);
                }

            }

            String[] daysArray = mActivity.getResources().getStringArray(R.array.array_days_full_name_lowercase);
            if(slots.size()>0) {
                slotWithAppointmentsMap.put(daysArray[0], slots);
                slotWithAppointmentsMap.put(daysArray[1], slots);
                slotWithAppointmentsMap.put(daysArray[2], slots);
                slotWithAppointmentsMap.put(daysArray[3], slots);
                slotWithAppointmentsMap.put(daysArray[4], slots);
                slotWithAppointmentsMap.put(daysArray[5], slots);
                slotWithAppointmentsMap.put(daysArray[6], slots);
            }

            mCallBack.onGetSlots(slotWithAppointmentsMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
