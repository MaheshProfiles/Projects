package com.nerve24.doctor.pojo;

import java.io.Serializable;

/**
 * Created by selva on 8/22/2016.
 */
public class DoctorLocality implements Serializable {

    public String addressBlock,locality;

    public DoctorLocality(String addressBlock, String locality) {
        this.addressBlock = addressBlock;
        this.locality = locality;
    }
    /*{
        "clinicId": 5,
            "clinicName": "St. Lourde's Clinic",
            "addressBlock": null,
            "userAddress": {
        "addressId": 75,
                "addressType": null,
                "pincode": 110001,
                "state": 1,
                "city": 1,
                "locality": 15,
                "landmark": null,
                "addressBlock": "sfdsg"
    },
        "pincode": 0,
            "locality": null,
            "city": null,
            "state": null,
            "landmark": null*/
}
