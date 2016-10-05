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
import com.nerve24.doctor.Listeners.Listener_Change_password;
import com.nerve24.doctor.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;




public class API_Change_Password {
    private static final String MODULE = "API_Change_Password";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiChangePassword;
    private Activity mActivity;

    private Listener_Change_password mCallBack;
    private String obj;

    public API_Change_Password(String obj, Listener_Change_password mCallBack, Activity mActivity) {
        this.obj = obj;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void process() {

        TAG = "process";
        Log.d(MODULE, TAG);
        Log.d(MODULE, obj);
        Log.d(MODULE, Str_Url);

        try {

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                    Str_Url,obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.e("response", "" + response);
                       //{"success":false,"statusCode":0,"message":"Old and New Passwords are same"}
                       // JSONObject response=new JSONObject(res);
                        if (response.getString("success").equals("true")) {
                            mCallBack.onPasswordChanged(response.getString("message"));
                        } else {
                            mCallBack.onPasswordChangedError(response.getString("message"));
                        }

                    } catch (Exception e) {
                        mCallBack.onPasswordChangedError(e.toString());
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

                    mCallBack.onPasswordChangedError(Str_Msg);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }

            };


            int socketTimeout = 60000;// 30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
            Volley.newRequestQueue(mActivity).add(req);

           // new ChangePassword().execute();

        } catch (Exception e) {
            Log.e(MODULE, TAG + " Exception Occurs - " + e);
            mCallBack.onPasswordChangedError(e.toString());
        }

    }

  /*  private class ChangePassword extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = RequestBody.create(mediaType, "eyJvbGRQYXNzd29yZCI6InF3ZXJ0eXVpIiwibmV3UGFzc3dvcmQiOiJxd2VydHl1aSIsImNvbmZpcm1QYXNzd29yZCI6InF3ZXJ0eXVpIiwidXNlck5hbWUiOiJzZWx2YSJ9");
                Request request = new Request.Builder()
                        .url("http://192.168.7.58:9090/Admin-Core/users/changePassword")
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .build();

                Response response = client.newCall(request).execute();

                Log.e("response", "" + response.body().string());

                String res = response.body().string();
                if (res != null) {
                    JSONObject responseJson = new JSONObject(res);
                    if (responseJson.getString("success").equals("true")) {
                        mCallBack.onPasswordChanged(responseJson.getString("message"));
                    } else {
                        mCallBack.onPasswordChangedError(responseJson.getString("message"));
                    }
                } else {
                    mCallBack.onPasswordChangedError("Try again later!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
*/

}
