package com.nerve24.doctor.pojo;

import java.io.Serializable;

public class Appointment implements Serializable {
    public String username,email,primaryphone,enabled,salutation,appointmentStatus,firstName,middleName,
            lastName,dob,gender,nerve24Id,appointmentFor,appointmentType,clinicDisplayName,
            clinicId,appointmentDate,appointmentTime,episode,encounter,id,refer,premium,salutationRefer;

    public Appointment(String username, String email, String primaryphone, String enabled,
                       String salutation, String appointmentStatus, String firstName,
                       String middleName, String lastName, String dob, String gender,
                       String nerve24Id,String appointmentFor,String appointmentType,
                       String clinicDisplayName,String clinicId,
                       String appointmentDate,String appointmentTime,
                       String episode,String encounter,String id,String refer,String premium,String salutationRefer) {
        this.username = username;
        this.email = email;
        this.primaryphone = primaryphone;
        this.enabled = enabled;
        this.salutation = salutation;
        this.appointmentStatus = appointmentStatus;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.nerve24Id = nerve24Id;
        this.appointmentFor=appointmentFor;
        this.appointmentType=appointmentType;
        this.clinicDisplayName=clinicDisplayName;
        this.clinicId=clinicId;
        this.appointmentDate=appointmentDate;
        this.appointmentTime=appointmentTime;
        this.episode=episode;
        this.encounter=encounter;
        this.id=id;
        this.refer=refer;
        this.premium=premium;
        this.salutationRefer=salutationRefer;
    }
}
