package com.nerve24.doctor.pojo;

import java.io.Serializable;

/**
 * Created by selva on 7/25/2016.
 */
public class Clinic implements Serializable {
    public String clinicId,clinicName;

    public Clinic(String clinicId, String clinicName) {
        this.clinicId = clinicId;
        this.clinicName = clinicName;
    }
}
