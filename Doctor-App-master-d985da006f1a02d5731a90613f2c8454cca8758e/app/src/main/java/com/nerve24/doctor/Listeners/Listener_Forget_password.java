package com.nerve24.doctor.Listeners;


import org.json.JSONObject;

public interface Listener_Forget_password {

    public void onOTPsent(String res);

    public void onOTPsentError(String res);

    public void onPasswordChange(String res);

    public void onPasswordChangeError(String res);

    public void onOTP_Validated(String res,JSONObject obj);

    public void onOTP_ValidatedError(String res);

}
