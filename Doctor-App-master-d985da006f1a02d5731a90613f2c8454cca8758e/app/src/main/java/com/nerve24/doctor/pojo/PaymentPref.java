package com.nerve24.doctor.pojo;

import java.io.Serializable;

public class PaymentPref implements Serializable {

    public String accountHolderName, accountHolderNumber, id, accountType,bankName,bankBranch,ifscCode,userName,bankDetailId;

    public PaymentPref(String accountHolderName, String accountHolderNumber, String id, String accountType, String bankName, String bankBranch, String ifscCode, String userName, String bankDetailId) {
        this.accountHolderName = accountHolderName;
        this.accountHolderNumber = accountHolderNumber;
        this.id = id;
        this.accountType = accountType;
        this.bankName = bankName;
        this.bankBranch = bankBranch;
        this.ifscCode = ifscCode;
        this.userName = userName;
        this.bankDetailId = bankDetailId;
    }
}
