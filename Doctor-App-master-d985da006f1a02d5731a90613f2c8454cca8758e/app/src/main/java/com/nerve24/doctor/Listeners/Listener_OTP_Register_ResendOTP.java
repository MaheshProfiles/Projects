package com.nerve24.doctor.Listeners;



public interface Listener_OTP_Register_ResendOTP
{
    public void onGetOTP(String res);
    public void onGetResendOTP(String res);
    public void onRegisterSuccess(String res);
    public void onfailure(String res);
}
