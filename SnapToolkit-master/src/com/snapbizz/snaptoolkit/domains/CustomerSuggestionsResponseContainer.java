package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;

public class CustomerSuggestionsResponseContainer extends ResponseContainer {

    @SerializedName("response")
    CustomerSuggestionResponse customerSuggestionsResponse;

    public CustomerSuggestionResponse getCustomerSuggestionsResponse() {
        return customerSuggestionsResponse;
    }

    public void setCustomerSuggestionsResponse(
            CustomerSuggestionResponse customerSuggestionsResponse) {
        this.customerSuggestionsResponse = customerSuggestionsResponse;
    }
    
}
