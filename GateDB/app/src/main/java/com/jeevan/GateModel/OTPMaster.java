package com.jeevan.GateModel;

import java.util.Date;

/**
 * Created by 9Jeevan on 31-08-2016.
 */
public class OTPMaster {
    private String otp;
    private String mobile;
    private String email;
    private Date createdAt;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String toString(){
        return otp+" "+mobile+" "+email ;
    }
}
