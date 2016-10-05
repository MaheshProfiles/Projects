package com.snapbizz.snaptoolkit.utils;

public enum PaymentType {

    CASH("Cash");
    
    private String paymentType;

    private PaymentType(String paymentType){
        this.paymentType = paymentType;
    }
    
    public String getPaymentType(){
        return this.paymentType;
    }
    
    public static PaymentType getPaymentEnum(String paymentValue){
        for (PaymentType paymentType : PaymentType.values()) {
            if(paymentType.paymentType.equals(paymentValue))
                return paymentType;
        }
        return null;
    }
    
}
