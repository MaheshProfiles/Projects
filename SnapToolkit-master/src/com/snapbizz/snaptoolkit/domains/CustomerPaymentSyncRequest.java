package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="customer_payment")
public class CustomerPaymentSyncRequest extends Request {

    @SerializedName("customerPaymentList")
    private List<CustomerPayment> customerPaymentList;

    public List<CustomerPayment> getCustomerPaymentList() {
        return customerPaymentList;
    }

    public void setCustomerPaymentList(List<CustomerPayment> customerPaymentList) {
        this.customerPaymentList = customerPaymentList;
    }
    
}
