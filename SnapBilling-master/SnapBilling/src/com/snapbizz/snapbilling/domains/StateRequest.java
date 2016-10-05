package com.snapbizz.snapbilling.domains;

import com.google.gson.annotations.SerializedName;
import com.snapbizz.snaptoolkit.domains.Request;
import com.snapbizz.snaptoolkit.domains.State;

public class StateRequest extends Request{

	@SerializedName("state_details")
	private State stateDetails;

	public State getStateDetails() {
		return stateDetails;
	}

	public void setStateDetails(State stateDetails) {
		this.stateDetails = stateDetails;
	}
	
	
}
