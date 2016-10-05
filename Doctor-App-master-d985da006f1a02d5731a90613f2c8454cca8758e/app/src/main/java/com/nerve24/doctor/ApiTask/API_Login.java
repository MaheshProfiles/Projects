package com.nerve24.doctor.ApiTask;


import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nerve24.doctor.AppConfig.Constants_API;
import com.nerve24.doctor.Listeners.Listener_Login;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class API_Login {
    private static final String MODULE = "API_Login";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiLogin;
    //private String Str_Url="http://192.168.6.189:8080/login?username=sivat&passwd=12345678";
    private Activity mActivity;

    private Listener_Login mCallBack;
    // HashMap<String, String> obj;
    JSONObject obj;

    public API_Login(JSONObject obj, Listener_Login mCallBack, Activity mActivity) {
        this.obj = obj;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void login() {

        TAG = "login";
        Log.d(MODULE, TAG);

        try {

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                    Str_Url, obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.e("response", "" + response);
                        //JSONObject jsonObject = new JSONObject(response);
                        if (response.has("username")) {
                            String str = response.getString("username");
                            if (str != null) {
                                saveSession(response);
                                mCallBack.onLoginSuccess("Login successfully!");
                            } else {
                                mCallBack.onLoginError("Invalid credentials!");
                            }
                        } else {
                            mCallBack.onLoginError("Invalid credentials!");
                        }

                    } catch (Exception e) {
                        mCallBack.onLoginError(e.toString());
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();

                    VolleyLog.e("Error: ", error.getMessage());
                    Log.e(MODULE, TAG + " UnknownResponse");

                    if (error instanceof ParseError) {
                        Str_Msg = "Invalid credentials!";
                        mCallBack.onLoginError(Str_Msg);
                    } else {
                        if (error instanceof NetworkError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_no_internet);
                        } else if (error instanceof ServerError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                        } else if (error instanceof AuthFailureError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                        } else if (error instanceof ParseError) {
                            //Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                            Str_Msg = "Invalid credentials!";
                        } else if (error instanceof NoConnectionError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_no_internet);
                        } else if (error instanceof TimeoutError) {
                            Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                        }
                        mCallBack.onLoginError(Str_Msg);
                    }
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    //headers.put(Constants.tokenName,new Session(mActivity).getToken());
                    return headers;
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    try {

                        Log.e("session",""+response.headers.get("Set-Cookie"));
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));

                    String header_response = String.valueOf(response.headers.values());
                    Log.e("header response",""+header_response);
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
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
            mCallBack.onLoginError(e.toString());
        }

    }

    private void saveSession(JSONObject response) {
        try {
            Session session = new Session(mActivity);
            JSONObject userObj = response.getJSONObject("user");
            String nerve24ID = userObj.getString("nerve24Id");
            if (userObj.has("firstName")) {
                String firstName = userObj.getString("firstName");
                session.saveFirstName(firstName);
            }
            if (userObj.has("username")) {
                String username = userObj.getString("username");
                session.saveUserName(username);
            }

            if (userObj.has("fullName")) {
                String fullName = userObj.getString("fullName");
                session.saveFullName(fullName);
            }

            if (userObj.has("salutation")) {
                String salutation = userObj.getString("salutation");
                session.saveSalutation(salutation);
            }

           // String landingPage=response.getString("landingPage");
            String landingPage=response.getString("mobLandingPage");

            session.setScreenName(landingPage);
            session.saveSession("1");
            session.saveNerve24Id(nerve24ID);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   /* {
        "password": "$2a$10$aUkSDXciZdNMguLdYvR1UOWYbGXEWyOnkI8d03x33CNNSLc2zXxaS",
            "username": "sivaT",
            "authorities": [{
        "authority": "ROLE_USER"
    }],
        "accountNonExpired": true,
            "accountNonLocked": true,
            "credentialsNonExpired": true,
            "enabled": true,
            "user": {
        "mediportId": "MP4SRJ0000",
                "username": "sivaT",
                "email": "siva@nalashaa.com",
                "primaryphone": 1231231232,
                "password": "$2a$10$aUkSDXciZdNMguLdYvR1UOWYbGXEWyOnkI8d03x33CNNSLc2zXxaS",
                "enabled": true,
                "firstName": "siva",
                "lastName": "pakanati",
                "salutation": null,
                "middleName": null,
                "dob": null,
                "gender": null,
                "userType": null,
                "fullName": null
    },
        "status": "ONBOARDING",
            "landingPage": null,
            "landingNavigatorState": null,
            "module": "DOCTOR"
    }*/


}
