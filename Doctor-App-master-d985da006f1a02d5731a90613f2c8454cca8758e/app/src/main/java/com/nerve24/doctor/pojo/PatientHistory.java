package com.nerve24.doctor.pojo;

import java.io.Serializable;

public class PatientHistory implements Serializable {

    public String id, patientNerve24Id, patientName, age, sex, referedBy, encounter, episodeType, clinicId, clinicName, appointmentDate, appointmentTime;


    public PatientHistory(String id, String patientNerve24Id, String patientName, String age, String sex, String referedBy,
                          String encounter, String episodeType,
                          String clinicId, String clinicName, String appointmentDate, String appointmentTime) {
        this.id = id;
        this.patientNerve24Id = patientNerve24Id;
        this.patientName = patientName;
        this.age = age;
        this.sex = sex;
        this.referedBy = referedBy;
        this.encounter = encounter;
        this.episodeType = episodeType;
        this.clinicId = clinicId;
        this.clinicName = clinicName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }
}
