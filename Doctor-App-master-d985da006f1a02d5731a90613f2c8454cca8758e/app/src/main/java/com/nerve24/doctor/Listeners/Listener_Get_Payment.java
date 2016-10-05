package com.nerve24.doctor.Listeners;


import com.nerve24.doctor.pojo.PaymentPref;

public interface Listener_Get_Payment
{
    public void onGetPayment(PaymentPref paymentPref);
    public void onGetPaymentError(String res);
}
