package com.snapbizz.snaptoolkit.domains;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.annotations.SerializedName;

public class VatResponse extends ResponseContainer{

	@SerializedName("response")
	public ArrayList<HashMap<String, String>> vatResponseList ;

	public ArrayList<HashMap<String, String>> getVatResponseList() {
		return vatResponseList;
	}

	public void setVatResponseList(
			ArrayList<HashMap<String, String>> vatResponseList) {
		this.vatResponseList = vatResponseList;
	} 
	
	
}
