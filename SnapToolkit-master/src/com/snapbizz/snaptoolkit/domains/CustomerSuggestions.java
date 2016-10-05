package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="customer_suggestions")
public class CustomerSuggestions {

    @SerializedName("suggestion_id")
    @DatabaseField(columnName="suggestion_id", generatedId=true, allowGeneratedIdInsert=true)
    private int suggestionId;
    @DatabaseField(columnName="customer_id", foreign=true)
    private Customer customer;
    @DatabaseField(columnName="sku_id", foreign=true)
    private ProductSku productSku;
    @SerializedName("customer_id")
    private int customerId;
    @SerializedName("sku_id")
    private String skuCode;
 
    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public String getSkuCode() {
        return skuCode;
    }
    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }
    public int getSuggestionId() {
        return suggestionId;
    }
    public void setSuggestionId(int suggestionId) {
        this.suggestionId = suggestionId;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public ProductSku getProductSku() {
        return productSku;
    }
    public void setProductSku(ProductSku productSku) {
        this.productSku = productSku;
    }
    
}
