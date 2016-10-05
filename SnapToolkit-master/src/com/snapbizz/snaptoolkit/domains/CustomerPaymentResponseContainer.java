package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CustomerPaymentResponseContainer extends ResponseContainer {

    @SerializedName("customerPaymentList")
    private List<CustomerPayment> customerPaymentList;

    public List<CustomerPayment> getCustomerPaymentList() {
        return customerPaymentList;
    }

    public void setCustomerPaymentList(List<CustomerPayment> customerPaymentList) {
        this.customerPaymentList = customerPaymentList;
    }
    
}
