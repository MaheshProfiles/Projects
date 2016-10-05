package com.nerve24.doctor.pojo;

import java.io.Serializable;

public class Nerver24Member implements Serializable {

    public String nerve24Id, username, email, primaryphone, enabled, firstName, lastName, salutation, middleName, dob, gender, userType, fullName, organizationId, roleId;

    public Nerver24Member(String nerve24Id, String username, String email, String primaryphone,
                          String enabled, String firstName, String lastName, String salutation,
                          String middleName, String dob, String gender,
                          String userType, String fullName, String organizationId, String roleId) {
        this.nerve24Id = nerve24Id;
        this.username = username;
        this.email = email;
        this.primaryphone = primaryphone;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salutation = salutation;
        this.middleName = middleName;
        this.dob = dob;
        this.gender = gender;
        this.userType = userType;
        this.fullName = fullName;
        this.organizationId = organizationId;
        this.roleId = roleId;
    }
}
