package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RetrieveDistributorProductMapResponseContainer extends ResponseContainer{
    @SerializedName("distributorProductList")
    List<DistributorProductMap> distributorProductList;

    public List<DistributorProductMap> getDistributorProductList() {
        return distributorProductList;
    }

    public void setDistributorProductList(
            List<DistributorProductMap> distributorProductList) {
        this.distributorProductList = distributorProductList;
    }
}
