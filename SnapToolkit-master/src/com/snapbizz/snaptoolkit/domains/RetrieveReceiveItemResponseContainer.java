package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RetrieveReceiveItemResponseContainer extends ResponseContainer{

    @SerializedName("receiveItemList")
    private List<ReceiveItems> receiveItems;

    public List<ReceiveItems> getReceiveItems() {
        return receiveItems;
    }

    public void setReceiveItems(List<ReceiveItems> receiveItems) {
        this.receiveItems = receiveItems;
    }
    
}
