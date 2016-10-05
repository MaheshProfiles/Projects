package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;

public class StateResponseContainer extends ResponseContainer {
	
	@SerializedName("response")
	private State stateInfo;

	public State getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(State stateInfo) {
		this.stateInfo = stateInfo;
	}

}
