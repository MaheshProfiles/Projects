package com.nerve24.doctor.pojo;

import java.io.Serializable;

public class Gender implements Serializable {

    public String genderId, genderCode, description;

    public Gender(String genderId, String genderCode, String description) {
        this.genderId = genderId;
        this.genderCode = genderCode;
        this.description = description;
    }
}
