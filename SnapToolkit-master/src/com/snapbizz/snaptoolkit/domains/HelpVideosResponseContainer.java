package com.snapbizz.snaptoolkit.domains;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class HelpVideosResponseContainer extends ResponseContainer {
    
    @SerializedName("response")
    private ArrayList<HelpVideo> helpVideosResponse;
    @SerializedName("timestamp")
    private String timestamp;

    public ArrayList<HelpVideo> getHelpVideosResponse() {
        return helpVideosResponse;
    }

    public void setHelpVideosResponse(ArrayList<HelpVideo> helpVideosResponse) {
        this.helpVideosResponse = helpVideosResponse;
    }

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
    
    
}
