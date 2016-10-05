package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DistributorProductMapSyncRequest extends Request {

    @SerializedName("distributorProductMapList")
    List<DistributorProductMap> distributorProductMapList;

    public List<DistributorProductMap> getDistributorProductMapList() {
        return distributorProductMapList;
    }

    public void setDistributorProductMapList(
            List<DistributorProductMap> distributorProductMapList) {
        this.distributorProductMapList = distributorProductMapList;
    }

}
