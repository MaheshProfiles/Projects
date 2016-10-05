package com.nerve24.doctor.pojo;

import java.io.Serializable;

public class Salutation implements Serializable {
    public String salutation_id, salutation, description, isIndexed;

    public Salutation(String salutation_id, String salutation, String description, String isIndexed) {
        this.salutation_id = salutation_id;
        this.salutation = salutation;
        this.description = description;
        this.isIndexed = isIndexed;
    }
}
