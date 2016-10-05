package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CustomerSuggestionResponse {

    @SerializedName("customer_suggestions")
    private List<CustomerSuggestions> customerSuggestions;
    
    @SerializedName("timestamp")
    private String timeStamp;

    public List<CustomerSuggestions> getCustomerSuggestions() {
        return customerSuggestions;
    }

    public void setCustomerSuggestions(List<CustomerSuggestions> customerSuggestions) {
        this.customerSuggestions = customerSuggestions;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    
}
