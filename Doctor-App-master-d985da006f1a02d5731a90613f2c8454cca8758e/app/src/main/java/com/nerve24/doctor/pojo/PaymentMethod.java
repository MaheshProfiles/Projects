package com.nerve24.doctor.pojo;

import java.io.Serializable;

public class PaymentMethod implements Serializable {

    public String paymentMethodId, paymentMethod;

    public PaymentMethod(String paymentMethodId, String paymentMethod) {
        this.paymentMethodId = paymentMethodId;
        this.paymentMethod = paymentMethod;
    }
}
