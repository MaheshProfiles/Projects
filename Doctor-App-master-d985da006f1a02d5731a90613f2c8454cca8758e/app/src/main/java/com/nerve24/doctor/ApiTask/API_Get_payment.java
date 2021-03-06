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
import com.nerve24.doctor.Listeners.Listener_Get_Payment;
import com.nerve24.doctor.R;
import com.nerve24.doctor.pojo.PaymentPref;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class API_Get_payment {
    private static final String MODULE = "API_Get_payment";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiAccountDetails;
    private Activity mActivity;

    private Listener_Get_Payment mCallBack;
    private String nerve24ID;

    public API_Get_payment(String nerve24ID, Listener_Get_Payment mCallBack, Activity mActivity) {
        this.nerve24ID = nerve24ID;
        Str_Url = Str_Url + "/" + this.nerve24ID;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void getPayment() {

        TAG = "getPayment";
        Log.d(MODULE, TAG);
        Log.d(MODULE, Str_Url);
        try {

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                    Str_Url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.e("response", "" + response);
                        if (response.has("accountHolderNumber")) {
                            parseData(response);
                        } else {
                            mCallBack.onGetPayment(null);
                        }
                    } catch (Exception e) {
                        mCallBack.onGetPaymentError(e.toString());
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

                    mCallBack.onGetPaymentError(Str_Msg);
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
            mCallBack.onGetPaymentError(e.toString());
        }

    }

    private void parseData(JSONObject jsonObject) {
        try {

            String accountHolderName = "", accountHolderNumber = "", id = "", accountType = "",
                    bankName = "", bankBranch = "", ifscCode = "", userName = "", bankDetailId = "";

            accountHolderName = jsonObject.getString("accountHolderName");
            accountHolderNumber = jsonObject.getString("accountHolderNumber");
            if(!jsonObject.isNull("accountType")) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("accountType");
                id = jsonObject1.getString("id");
                accountType = jsonObject1.getString("accountType");
            }
            bankName = jsonObject.getString("bankName");
            bankBranch = jsonObject.getString("bankBranch");
            ifscCode = jsonObject.getString("ifscCode");
            userName = jsonObject.getString("userName");
            bankDetailId = jsonObject.getString("bankDetailId");

            bankName=bankName.equals("null")?"":bankName;
            bankBranch=bankBranch.equals("null")?"":bankBranch;
            ifscCode=ifscCode.equals("null")?"":ifscCode;
            accountType=accountType.equals("null")?"":accountType;
            id=id.equals("null")?"":id;
            accountHolderNumber=accountHolderNumber.equals("0")?"":accountHolderNumber;
            accountHolderName=accountHolderName.equals("null")?"":accountHolderName;


            PaymentPref paymentPref = new PaymentPref(accountHolderName, accountHolderNumber, id, accountType, bankName, bankBranch, ifscCode, userName, bankDetailId);

            mCallBack.onGetPayment(paymentPref);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
