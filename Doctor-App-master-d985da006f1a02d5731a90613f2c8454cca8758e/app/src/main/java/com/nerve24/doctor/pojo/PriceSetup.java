package com.nerve24.doctor.pojo;


import java.io.Serializable;
import java.util.HashMap;

public class PriceSetup implements Serializable {

    /* pricingId: 73,
     clinicName: "britly clinic",
     clinicId: 2,
     locality: "Abmedabad Exp Highway Bandra",
     fee: 200,
     overridden: false,
     doctorDiscount: 0,
     nerve24Discount: 0,
     mediportId: "MP4SRK0000",
     paymentMethodMaster: {
         paymentMethodId: 1,
                 paymentMethod: "credit card"
     }*/
    public String pricingId, clinicName, clinicId, locality, fee,doctorDiscount, nerve24Discount;
    public HashMap<String, String> paymentMethodMasterMap;
    public boolean hasOverriddenSlots,overridden;

    public PriceSetup(String pricingId, String clinicName, String clinicId, String locality, String fee, boolean overridden,
                      String doctorDiscount, String nerve24Discount, HashMap<String, String> paymentMethodMasterMap,boolean hasOverriddenSlots) {
        this.pricingId = pricingId;
        this.clinicName = clinicName;
        this.clinicId = clinicId;
        this.locality = locality;
        this.fee = fee;
        this.overridden = overridden;
        this.doctorDiscount = doctorDiscount;
        this.nerve24Discount = nerve24Discount;
        this.paymentMethodMasterMap=paymentMethodMasterMap;
        this.hasOverriddenSlots=hasOverriddenSlots;
    }

}
