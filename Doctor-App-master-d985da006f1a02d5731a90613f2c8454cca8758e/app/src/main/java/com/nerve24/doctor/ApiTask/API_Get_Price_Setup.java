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
import com.nerve24.doctor.Listeners.Listener_Get_Price_Setup;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.pojo.PriceSetup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class API_Get_Price_Setup {
    private static final String MODULE = "API_Get_Price_Setup";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiGetpriceSetup;
    private Activity mActivity;

    private Listener_Get_Price_Setup mCallBack;
    private String nerve24ID;

    public API_Get_Price_Setup(String nerve24ID, Listener_Get_Price_Setup mCallBack, Activity mActivity) {
        this.nerve24ID = nerve24ID;
         Str_Url = Str_Url + this.nerve24ID;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void getPriceSetup() {

        TAG = "getPriceSetup";
        Log.d(MODULE, TAG);
        Log.d(MODULE, Str_Url);

        try {

            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET,
                    Str_Url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        Log.e("response", "" + response);
                        if (response.length() > 0) {
                            parseData(response);
                        } else {
                            ArrayList<PriceSetup> priceSetupList = new ArrayList<>();
                            mCallBack.onGetPriceSetup(priceSetupList);
                        }
                    } catch (Exception e) {
                        mCallBack.onGetPriceSetupError(e.toString());
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

                    mCallBack.onGetPriceSetupError(Str_Msg);
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
            mCallBack.onGetPriceSetupError(e.toString());
        }

    }

    private void parseData(JSONArray response) {
        try {
            new Session(mActivity).savePriceSetup(response.toString());
            ArrayList<PriceSetup> priceSetupList = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                String pricingId = "", clinicName = "", clinicId = "", locality = "", fee = "", doctorDiscount = "", nerve24Discount = "";
                boolean hasOverriddenSlots=false,overridden=false;
                HashMap<String, String> paymentMethodMasterMap = new HashMap<>();
                if (jsonObject.has("pricingId"))
                    pricingId = jsonObject.getString("pricingId");
                if (jsonObject.has("clinicName"))
                    clinicName = jsonObject.getString("clinicName");
                if (jsonObject.has("clinicId"))
                    clinicId = jsonObject.getString("clinicId");
                if (jsonObject.has("locality"))
                    locality = jsonObject.getString("locality");
                if (jsonObject.has("fee")) {
                    fee =jsonObject.getString("fee");
                    BigDecimal b=new BigDecimal(fee);
                    fee=b.toString();
                }
                if (jsonObject.has("hasOverriddenSlots"))
                    hasOverriddenSlots = jsonObject.getBoolean("hasOverriddenSlots");
                if (jsonObject.has("overridden"))
                    overridden = jsonObject.getBoolean("overridden");
                if (jsonObject.has("doctorDiscount"))
                    doctorDiscount = jsonObject.getString("doctorDiscount");
                if (jsonObject.has("nerve24Discount"))
                    nerve24Discount = jsonObject.getString("nerve24Discount");
                if (jsonObject.has("paymentMethodList")) {
                    if (jsonObject.isNull("paymentMethodList")) {
                    } else {
                        JSONArray array=jsonObject.getJSONArray("paymentMethodList");
                        for(int v=0;v<array.length();v++) {
                            JSONObject jsonObject1 = array.getJSONObject(v);
                            String paymentMethodId = jsonObject1.getString("paymentMethodId");
                            String paymentMethod = jsonObject1.getString("paymentMethod");
                            paymentMethodMasterMap.put(paymentMethodId, paymentMethod);
                        }

                    }
                }
                PriceSetup priceSetup = new PriceSetup(pricingId, clinicName,
                        clinicId, locality, fee, overridden, doctorDiscount,
                        nerve24Discount, paymentMethodMasterMap,hasOverriddenSlots);
                priceSetupList.add(priceSetup);
            }
            mCallBack.onGetPriceSetup(priceSetupList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
